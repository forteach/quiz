package com.forteach.quiz.service;

import com.forteach.quiz.domain.AskAnswer;
import com.forteach.quiz.domain.BigQuestion;
import com.forteach.quiz.exceptions.AskException;
import com.forteach.quiz.exceptions.ExamQuestionsException;
import com.forteach.quiz.repository.BigQuestionRepository;
import com.forteach.quiz.web.pojo.CircleAnswer;
import com.forteach.quiz.web.pojo.Students;
import com.forteach.quiz.web.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;

import static com.forteach.quiz.common.Dic.*;
import static com.forteach.quiz.common.KeyStorage.CLASSROOM_ASK_QUESTIONS_ID;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/27  15:00
 */
@Slf4j
@Service
public class SseInteractServiceImpl implements InteractService {


    private final ReactiveStringRedisTemplate stringRedisTemplate;

    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;

    private final BigQuestionRepository bigQuestionRepository;

    private final InteractRecordService interactRecordService;

    private final StudentsService studentsService;

    private final CorrectService correctService;

    private final ReactiveMongoTemplate reactiveMongoTemplate;


    public SseInteractServiceImpl(ReactiveStringRedisTemplate stringRedisTemplate, ReactiveHashOperations<String, String, String> reactiveHashOperations,
                                  BigQuestionRepository bigQuestionRepository, InteractRecordService interactRecordService,
                                  StudentsService studentsService, CorrectService correctService, ReactiveMongoTemplate reactiveMongoTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.bigQuestionRepository = bigQuestionRepository;
        this.studentsService = studentsService;
        this.correctService = correctService;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.interactRecordService = interactRecordService;
    }


    /**
     * 课堂提问发题
     *
     * @param giveVo
     * @return
     */
    @Override
    public Mono<Long> sendQuestion(final GiveVo giveVo) {

        HashMap<String, String> map = new HashMap<>(10);
        map.put("questionId", giveVo.getQuestionId());
        map.put("interactive", giveVo.getInteractive());
        map.put("category", giveVo.getCategory());
        map.put("selected", giveVo.getSelected());
        map.put("cut", giveVo.getCut());

        //如果本题和redis里的题目id不一致 视为换题 进行清理
        Mono<Boolean> clearCut = clearCut(giveVo);

        Mono<Boolean> set = reactiveHashOperations.putAll(askQuestionsId(giveVo.getCircleId()), map);
        Mono<Boolean> time = stringRedisTemplate.expire(askQuestionsId(giveVo.getCircleId()), Duration.ofSeconds(60 * 60 * 10));

        //删除抢答答案
        Mono<Boolean> removeRace = stringRedisTemplate.opsForValue().delete(giveVo.getRaceAnswerFlag());

        return Flux.concat(set, time, removeRace, clearCut).filter(flag -> !flag).count()
                .filterWhen(obj -> interactRecordService.releaseQuestion(giveVo.getCircleId(), giveVo.getQuestionId(), giveVo.getSelected(), giveVo.getCategory(), giveVo.getInteractive()));
    }

    private Mono<Boolean> clearCut(final GiveVo giveVo) {
        return reactiveHashOperations.get(askQuestionsId(giveVo.getCircleId()), "questionId")
                .zipWith(Mono.just(giveVo.getQuestionId()), String::equals)
                .flatMap(flag -> {
                    if (flag) {
                        //清除提问标识
                        return Mono.just(false);
                    }
                    return stringRedisTemplate.opsForValue().delete(giveVo.getExamineeIsReplyKey());
                });
    }

    /**
     * 主动推送给教师 当前问题的回答情况
     *
     * @return
     */
    @Override
    public Mono<List<CircleAnswer>> achieveAnswer(final AchieveAnswerVo achieves) {
        return Mono.just(achieves)
                .filterWhen(achieveAnswerVo -> untitled(achieveAnswerVo.getAskKey()))
                .flatMap(achieve -> askSelected(askQuestionsId(achieve.getCircleId())))
                .map(ids -> (Arrays.asList(ids.split(","))))
                .flatMapMany(Flux::fromIterable)
                .flatMap(id -> isMember(achieves.getExamineeIsReplyKey(), id)
                        .flatMap(flag -> askQuestionId(achieves.getAskKey())
                                .flatMap(qid -> findAskAnswer(achieves.getCircleId(), id, qid))
                                .zipWith(studentsService.findStudentsBrief(id), (answ, student) -> {
                                    if (flag) {
                                        return new CircleAnswer(student, ASK_CIRCLE_ANSWER_DID, answ);
                                    } else {
                                        return new CircleAnswer(student, ASK_CIRCLE_ANSWER_ALREADY, new AskAnswer());
                                    }
                                }))
                )
                .collectList()
                .filterWhen(obj -> answerDistinct(achieves.getAnswDistinctKey(), achieves.getExamineeIsReplyKey(), achieves.getAskKey()));
    }

    /**
     * 主动推送给链接的学生考题
     * 只有经过过滤条件的学生 才能收到考题
     * 学生获取答案过滤
     *
     * @param achieveVo
     * @return
     */
    @Override
    public Mono<AskQuestionVo> achieveQuestion(final AchieveVo achieveVo) {
        return askCategoryType(achieveVo.getAskKey())
                .flatMap(type -> {
                    switch (type) {
                        case CATEGORY_PEOPLE:
                            return askPeople(achieveVo);
                        case CATEGORY_TEAM:
                            return Mono.empty();
                        default:
                            throw new ExamQuestionsException("非法参数 错误的数据类型");
                    }
                })
                .transform(bigQuestionMono -> askDistinct(bigQuestionMono, achieveVo))
                .flatMap(bigQuestion -> askQuestionCut(achieveVo.getAskKey())
                        .flatMap(cut ->
                                askInteractiveType(achieveVo.getAskKey())
                                        .map(interactive ->
                                                new AskQuestionVo<BigQuestion>(cut, bigQuestion, interactive))
                        ));
    }


    /**
     * 提交答案
     * 提交答案前判断题目id 是否与当前id一致
     * 提交后判断对错
     * 提交后在当前课堂里添加是否回答
     *
     * @return
     */
    @Override
    public Mono<String> sendAnswer(final InteractAnswerVo answerVo) {

        return Mono.just(answerVo)
                .transform(this::filterSelectVerify)
                .flatMap(answer -> askInteractiveType(answer.getAskKey()))
                .zipWhen(type -> sendAnswerVerify(answerVo.getAskKey(), answerVo.getQuestionId(), answerVo.getCut()))
                .flatMap(tuple2 -> {
                    if (tuple2.getT2()) {
                        switch (tuple2.getT1()) {
                            case ASK_INTERACTIVE_RACE:
                                return sendRace(answerVo);
                            case ASK_INTERACTIVE_RAISE:
                                return sendRaise(answerVo);
                            case ASK_INTERACTIVE_SELECT:
                                return sendSelect(answerVo, ASK_INTERACTIVE_SELECT);
                            case ASK_INTERACTIVE_VOTE:
                                return Mono.empty();
                            default:
                                throw new ExamQuestionsException("非法参数 错误的数据类型");
                        }
                    } else {
                        return Mono.error(new AskException("请重新刷新获取最新题目"));
                    }
                })
                .filterWhen(right -> setRedis(answerVo.getExamineeIsReplyKey(), answerVo.getExamineeId(), answerVo.getAskKey()))
                .filterWhen(right -> interactRecordService.answer(answerVo.getCircleId(), answerVo.getQuestionId(), answerVo.getExamineeId(), answerVo.getAnswer(), right));
    }

    /**
     * 发起提问举手
     * 清空老师端显示的举手学生
     *
     * @param
     * @return
     */
    @Override
    public Mono<Long> launchRaise(final AskLaunchVo askLaunchVo) {
        Mono<Long> deleteDistinctKey = stringRedisTemplate.delete(askLaunchVo.getRaiseDistinctKey());
        Mono<Long> deleteRaiseExmKey = stringRedisTemplate.delete(askLaunchVo.getRaiseKey(), "examinee");
        return deleteDistinctKey.zipWith(deleteRaiseExmKey, (d, e) -> d + e);
    }


    /**
     * 学生进行举手
     * 最后记录
     * @return
     */
    @Override
    public Mono<Long> raiseHand(final RaisehandVo raisehandVo) {
        Mono<Long> set = stringRedisTemplate.opsForSet().add(raisehandVo.getRaiseKey(), raisehandVo.getExamineeId());
        Mono<Boolean> time = stringRedisTemplate.expire(raisehandVo.getRaiseKey(), Duration.ofSeconds(60 * 60 * 10));
        Mono<String> questionId = askQuestionId(raisehandVo.getAskKey());
        return set.zipWith(time, (c, t) -> t ? c : -1)
                .filterWhen(obj -> questionId.flatMap(qid -> interactRecordService.raiseHand(raisehandVo.getCircleId(), raisehandVo.getExamineeId(), qid)));
    }


    /**
     * 主动推送给老师举手的学生
     * 获取读取的数据
     * 转换为学生list
     * 去除空数据
     *
     * @param achieveRaiseVo
     * @return
     */
    @Override
    public Mono<List<Students>> achieveRaise(final AchieveRaiseVo achieveRaiseVo) {
        return stringRedisTemplate.opsForSet().members(achieveRaiseVo.getRaiseKey())
                .flatMap(studentsService::findStudentsBrief).collectList()
                .filter(list -> list.size() > 0)
                .transform(listMono -> raiseDistinct(achieveRaiseVo.getRaiseDistinctKey(), listMono, achieveRaiseVo.getAskKey()));
    }

    /**
     * 请求数据时 通过随机数 判断是否第一次进入拉取
     * 通过去重标识的 value 值 与查询出的list.size 比对 如果相同 则代表已经拉取过
     * 如果返回空数据 则进行获取{并且插入标识 list.size} 否则退出请求
     * RAISE_HAND_STUDENT_DISTINCT
     *
     * @param listMono
     * @return
     */
    private Mono<List<Students>> raiseDistinct(final String distinct, final Mono<List<Students>> listMono, final String askKey) {
        return listMono.filterWhen(list -> raiseIsDistinct(distinct, list.size(), askKey));
    }

    /**
     * 在请求数据时 通过随机数 判断是否拉取过数据
     * 使用 课堂+学生id+考题id+随机数
     * 如果返回空数据 则进行获取{并且插入标识} 否则退出请求
     *
     * @return
     */
    private Mono<OptBigQuestionVo> askDistinct(final Mono<OptBigQuestionVo> bigQuestionMono, final AchieveVo achieveVo) {
        return bigQuestionMono
                .filterWhen(bigQuestion -> distinctKeyIsEmpty(achieveVo.getDistinctKey(bigQuestion.getId()), achieveVo.getAskKey()));
    }

    /**
     * 验证提交的答案信息
     *
     * @return
     */
    private Mono<Boolean> sendAnswerVerify(final String askId, final String oQuestionId, final String oCut) {
        Mono<String> questionId = reactiveHashOperations.get(askId, "questionId");
        Mono<String> cut = reactiveHashOperations.get(askId, "cut");
        return cut.zipWith(questionId, (c, q) -> c.equals(oCut) && q.equals(oQuestionId));
    }

    /**
     * 通过上一题推送的id 和 cut 查看是否推送过
     *
     * @param redisKey
     * @return
     */
    private Mono<String> redisGet(final String redisKey) {
        return stringRedisTemplate.opsForValue().get(redisKey);
    }

    /**
     * 老师请求实时的题目去重
     *
     * @return
     */
    private Mono<Boolean> answerDistinct(final String distinctKey, final String setKey, final String askKey) {

        return redisGet(distinctKey)
                .switchIfEmpty(Mono.just(DISTINCT_INITIAL))
                .zipWhen(origin -> findAnswerFlag(askKey).defaultIfEmpty(DISTINCT_INITIAL))
                .zipWith(stringRedisTemplate.opsForSet().size(setKey), (tuple2, size) -> {
                    //T1 : 去重参数里的答题标识  T2 : 上课对象中的答题标识
                    // 去重参数里的答题标识 & 上课答题标识 & 答题人数
                    if (tuple2.getT1().equals(String.valueOf(size.intValue())) && tuple2.getT2().equals(String.valueOf(size.intValue()))) {
                        //如果等于 排除
                        return Mono.just(false);
                    }
                    //去重参数里的答题标识 = 答题人数 / 但是去重参数里的答题标识 != 上课答题标识  表示第一次
                    //如果去重结果无数据 代表 第一次 返回
                    //如果上次推送长度与本次不一致 进行推送
                    return saveRedis(distinctKey, String.valueOf(size.intValue())).filterWhen(obj -> reactiveHashOperations.put(askKey, "answerFlag", String.valueOf(size.intValue())).map(Objects::nonNull));
                }).flatMap(booleanMono -> booleanMono);
    }

    /**
     * 判断是否已经推送过该题
     * 如果没有拉取过 给予正确 存入课堂题目的cut
     * 如果一致 代表已经拉取过 不再给予
     * 如果不一致 代表同题但是不同提问方式 重新发送
     *
     * @param redisKey
     * @return true 没有推送过该题   false  有推送过该题
     */
    private Mono<Boolean> distinctKeyIsEmpty(final String redisKey, final String askKey) {
        return redisGet(redisKey)
                .switchIfEmpty(Mono.just(DISTINCT_INITIAL))
                .zipWhen(origin -> askQuestionCut(askKey))
                .flatMap(tuple2 -> {
                    if (DISTINCT_INITIAL.equals(tuple2.getT1())) {
                        return saveRedis(redisKey, tuple2.getT2());
                    } else if (tuple2.getT1().equals(tuple2.getT2())) {
                        return Mono.just(false).filterWhen(flag -> saveRedis(redisKey, tuple2.getT2()));
                    } else {
                        return saveRedis(redisKey, tuple2.getT2());
                    }
                });
    }

    private Mono<Boolean> raiseIsDistinct(final String redisKey, final int size, final String askKey) {
        return redisGet(redisKey)
                .switchIfEmpty(Mono.just(DISTINCT_INITIAL))
                .zipWhen(origin -> askQuestionCut(askKey))
                .flatMap(tuple2 -> {
                    if (DISTINCT_INITIAL.equals(tuple2.getT1())) {
                        return saveRedis(redisKey, String.valueOf(size).concat(tuple2.getT2()));
                    } else if (tuple2.getT1().equals(String.valueOf(size).concat(tuple2.getT2()))) {
                        return Mono.just(false);
                    }
                    return saveRedis(redisKey, String.valueOf(size).concat(tuple2.getT2()));
                });
    }

    /**
     * 个人对象 返回题目
     *
     * @param achieveVo
     * @return
     */
    private Mono<OptBigQuestionVo> askPeople(final AchieveVo achieveVo) {
        return reactiveHashOperations.get(achieveVo.getAskKey(), ASK_INTERACTIVE)
                .flatMap(interactive -> {
                    switch (interactive) {
                        case ASK_INTERACTIVE_RACE:
                            return selectQuestion(achieveVo).transform(this::selected);
                        case ASK_INTERACTIVE_RAISE:
                            return findBigQuestion(achieveVo.getAskKey()).flatMap(obj -> raiseQuestion(achieveVo, obj));
                        case ASK_INTERACTIVE_SELECT:
                            return selectQuestion(achieveVo).transform(this::selected);
                        case ASK_INTERACTIVE_VOTE:
                            return Mono.empty();
                        default:
                            throw new ExamQuestionsException("非法参数 错误的数据类型");
                    }
                });
    }

    private Mono<OptBigQuestionVo> selected(final Mono<BigQuestion> bigQuestionMono) {
        return bigQuestionMono.map(bigQuestion -> new OptBigQuestionVo(ASK_QUESTIONS_SELECTED, bigQuestion));
    }

    private Mono<OptBigQuestionVo> raiseQuestion(final AchieveVo achieve, final BigQuestion bigQuestion) {
        return selectVerify(achieve.getAskKey(), achieve.getExamineeId()).map(flag -> {
            if (flag) {
                return new OptBigQuestionVo(ASK_QUESTIONS_SELECTED, bigQuestion);
            } else {
                return new OptBigQuestionVo(ASK_QUESTIONS_UN_SELECTED, bigQuestion);
            }
        });
    }

    /**
     * 提问 选中 回答
     *
     * @param achieveVo
     * @return
     */
    private Mono<BigQuestion> selectQuestion(final AchieveVo achieveVo) {
        return Mono.just(achieveVo)
                .filterWhen(achieve -> selectVerify(achieve.getAskKey(), achieve.getExamineeId()))
                .flatMap(achieve -> findBigQuestion(achieve.getAskKey()));
    }

    /**
     * 通过 提问key,判断是否是选择
     *
     * @param askKey
     * @return
     */
    private Mono<Boolean> selectVerify(final String askKey, final String examineeId) {
        return reactiveHashOperations.get(askKey, "selected")
                .map(selectId -> isSelected(selectId, examineeId));
    }

    private Mono<InteractAnswerVo> filterSelectVerify(final Mono<InteractAnswerVo> answerVo) {
        return answerVo.zipWhen(answer -> selectVerify(answer.getAskKey(), answer.getExamineeId()))
                .flatMap(tuple2 -> {
                    if (tuple2.getT2()) {
                        return Mono.just(tuple2.getT1());
                    } else {
                        return Mono.error(new AskException("该题未被选中 不能答题"));
                    }
                });
    }

    /**
     * 抢答 回答
     *
     * @return
     */
    private Mono<String> sendRace(final InteractAnswerVo interactAnswerVo) {
        return stringRedisTemplate.hasKey(interactAnswerVo.getRaceAnswerFlag())
                .flatMap(flag -> {
                    if (flag) {
                        return Mono.error(new AskException("该抢答题已被回答"));
                    }
                    return sendSelect(interactAnswerVo, ASK_INTERACTIVE_RACE)
                            .filterWhen(askAnswer -> {
                                if (askAnswer != null) {
                                    return stringRedisTemplate.opsForValue().set(interactAnswerVo.getRaceAnswerFlag(), STATUS_SUCCESS, Duration.ofSeconds(60 * 60));
                                } else {
                                    return Mono.empty();
                                }
                            });
                });
    }

    /**
     * 举手 回答
     */
    private Mono<String> sendRaise(final InteractAnswerVo interactVo) {
        return Mono.just(interactVo).flatMap(interactAnswerVo -> sendSelect(interactAnswerVo, ASK_INTERACTIVE_RAISE));
    }

    private Mono<Boolean> isMember(final String redisKey, final String examineeId) {
        return stringRedisTemplate.opsForSet().isMember(redisKey, examineeId);
    }

    /**
     * 选人 回答
     *
     * @return
     */
    private Mono<String> sendSelect(final InteractAnswerVo interactAnswerVo, final String type) {

        return correctService.correcting(interactAnswerVo.getQuestionId(), interactAnswerVo.getAnswer())
                .flatMap(f -> {
                    Query query = Query.query(
                            Criteria.where("circleId").is(interactAnswerVo.getCircleId())
                                    .and("questionId").is(interactAnswerVo.getQuestionId())
                                    .and("examineeId").is(interactAnswerVo.getExamineeId()));

                    Update update = Update.update("answer", interactAnswerVo.getAnswer());
                    update.set("interactive", type);
                    update.set("right", String.valueOf(f));
                    update.set("uDate", new Date());

                    return reactiveMongoTemplate.upsert(query, update, AskAnswer.class).flatMap(result -> {
                        if (result.wasAcknowledged()) {
                            return Mono.just(f);
                        } else {
                            return Mono.error(new AskException("操作失败"));
                        }
                    });
                });
    }

    private Mono<BigQuestion> findBigQuestion(final String askKey) {
        return reactiveHashOperations.get(askKey, "questionId").flatMap(bigQuestionRepository::findById);
    }

    private Mono<String> findAnswerFlag(final String askKey) {
        return reactiveHashOperations.get(askKey, "answerFlag");
    }

    /**
     * 判断学生是否被选中
     *
     * @return
     */
    private Boolean isSelected(final String selectId, final String examineeId) {
        return Arrays.asList(selectId.split(",")).contains(examineeId);
    }


    /**
     * 根据课堂 获取题目id
     *
     * @param classId
     * @return
     */
    private String askQuestionsId(final String classId) {
        return CLASSROOM_ASK_QUESTIONS_ID.concat(classId);
    }

    /**
     * 获取提问类型
     *
     * @param askKey
     * @return
     */
    private Mono<String> askCategoryType(final String askKey) {
        return reactiveHashOperations.get(askKey, "category");
    }

    /**
     * 获取提问类型
     *
     * @param askKey
     * @return
     */
    private Mono<String> askInteractiveType(final String askKey) {
        return reactiveHashOperations.get(askKey, "interactive");
    }

    /**
     * 获取选择信息
     *
     * @param askKey
     * @return
     */
    private Mono<String> askSelected(final String askKey) {
        return reactiveHashOperations.get(askKey, "selected");
    }

    private Mono<Boolean> saveRedis(final String redisKey, final String value) {
        return stringRedisTemplate.opsForValue().set(redisKey, value, Duration.ofSeconds(60 * 60));
    }

    private Mono<Boolean> isRedisEmpty(final String redisKey) {
        return stringRedisTemplate.hasKey(redisKey);
    }

    private Mono<Boolean> setRedis(final String redisKey, final String value, final String askKey) {

        Mono<Long> set = stringRedisTemplate.opsForSet().add(redisKey, value);
        Mono<Boolean> time = stringRedisTemplate.expire(redisKey, Duration.ofSeconds(60 * 60 * 10));

        return set.zipWith(time, (c, t) -> t ? c : -1).map(monoLong -> monoLong != -1).filterWhen(take -> reactiveHashOperations.increment(askKey, "answerFlag", 1).map(Objects::nonNull));
    }


    /**
     * 获取课堂提问的切换值
     *
     * @return
     */
    private Mono<String> askQuestionCut(final String askKey) {
        return reactiveHashOperations.get(askKey, "cut");
    }

    /**
     * 获得课堂的提问id
     */
    private Mono<String> askQuestionId(final String askKey) {
        return reactiveHashOperations.get(askKey, "questionId");
    }

    private Mono<AskAnswer> findAskAnswer(final String circleId, final String examineeId, final String questionId) {

        Query query = Query.query(
                Criteria.where("circleId").in(circleId)
                        .and("questionId").in(questionId)
                        .and("examineeId").in(examineeId));

        return reactiveMongoTemplate.findOne(query, AskAnswer.class);
    }

    /**
     * 过滤掉没有题的情况
     *
     * @param askKey
     * @return
     */
    private Mono<Boolean> untitled(final String askKey) {
        return reactiveHashOperations.hasKey(askKey, "questionId");
    }
}

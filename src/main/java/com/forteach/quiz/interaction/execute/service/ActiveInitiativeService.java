package com.forteach.quiz.interaction.execute.service;

import com.forteach.quiz.exceptions.ExamQuestionsException;
import com.forteach.quiz.interaction.execute.domain.AskAnswer;
import com.forteach.quiz.questionlibrary.domain.BigQuestion;
import com.forteach.quiz.questionlibrary.domain.QuestionType;
import com.forteach.quiz.questionlibrary.repository.BigQuestionRepository;
import com.forteach.quiz.service.StudentsService;
import com.forteach.quiz.web.pojo.CircleAnswer;
import com.forteach.quiz.web.pojo.Students;
import com.forteach.quiz.web.vo.*;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.forteach.quiz.common.Dic.*;
import static com.forteach.quiz.common.KeyStorage.CLASSROOM_ASK_QUESTIONS_ID;

/**
 * @Description: 主动推送 或者轮询调用 / 现已用websocket做替代
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/15  14:34
 */
@Service
public class ActiveInitiativeService {

    private final ReactiveStringRedisTemplate stringRedisTemplate;

    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;

    private final StudentsService studentsService;

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    private final BigQuestionRepository bigQuestionRepository;


    public ActiveInitiativeService(ReactiveStringRedisTemplate stringRedisTemplate,
                                   ReactiveHashOperations<String, String, String> reactiveHashOperations,
                                   StudentsService studentsService,
                                   ReactiveMongoTemplate reactiveMongoTemplate,
                                   BigQuestionRepository bigQuestionRepository) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.studentsService = studentsService;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.bigQuestionRepository = bigQuestionRepository;
    }

    /**
     * 主动推送给老师举手的学生
     * 获取读取的数据
     * 转换为学生list
     * 去除空数据
     *
     * @param achieveRaiseVo 加入课堂的学生信息vo
     * @return 学生信息list集合(Mono)
     */
    public Mono<List<Students>> achieveRaise(final AchieveRaiseVo achieveRaiseVo) {
        return stringRedisTemplate.opsForSet().members(achieveRaiseVo.getRaiseKey())
                .flatMap(studentsService::findStudentsBrief).collectList()
                .filter(list -> list.size() > 0)
                .transform(listMono -> raiseDistinct(achieveRaiseVo.getRaiseDistinctKey(), listMono, achieveRaiseVo.getAskKey(QuestionType.BigQuestion)));
    }

    /**
     * 主动推送给教师 当前问题的回答情况
     *
     * @return
     */
    public Mono<List<CircleAnswer>> achieveAnswer(final AchieveAnswerVo achieves) {
        return Mono.just(achieves)
                .filterWhen(achieveAnswerVo -> untitled(achieveAnswerVo.getAskKey(QuestionType.BigQuestion)))
                .flatMap(achieve -> askSelected(askQuestionsId(QuestionType.BigQuestion, achieve.getCircleId())))
                .map(ids -> (Arrays.asList(ids.split(","))))
                .flatMapMany(Flux::fromIterable)
                .flatMap(id -> isMember(achieves.getExamineeIsReplyKey(QuestionType.BigQuestion), id)
                        .flatMap(flag -> askQuestionId(achieves.getAskKey(QuestionType.BigQuestion))
                                .flatMap(qid -> findAskAnswer(achieves.getCircleId(), id, qid))
                                .zipWith(studentsService.findStudentsBrief(id), (answ, student) -> {
                                    if (flag) {
                                        answ.setLibraryType("bigQuestion");
                                        return new CircleAnswer(student, ASK_CIRCLE_ANSWER_DID, answ);
                                    } else {
                                        return new CircleAnswer(student, ASK_CIRCLE_ANSWER_ALREADY, new AskAnswer());
                                    }
                                }))
                )
                .collectList()
                .filterWhen(obj -> answerDistinct(achieves.getAnswDistinctKey(), achieves.getExamineeIsReplyKey(QuestionType.BigQuestion), achieves.getAskKey(QuestionType.BigQuestion)));
    }

    /**
     * 主动推送给链接的学生考题
     * 只有经过过滤条件的学生 才能收到考题
     * 学生获取答案过滤
     *
     * @param achieveVo
     * @return
     */
    public Mono<AskQuestionVo> achieveQuestion(final AchieveVo achieveVo) {
        return askCategoryType(achieveVo.getAskKey(QuestionType.BigQuestion))
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
                .flatMap(bigQuestion -> askQuestionCut(achieveVo.getAskKey(QuestionType.BigQuestion))
                        .flatMap(cut ->
                                askInteractiveType(achieveVo.getAskKey(QuestionType.BigQuestion))
                                        .map(interactive ->
                                                new AskQuestionVo<BigQuestion>(cut, bigQuestion, interactive))
                        ));
    }

    /**
     * 请求数据时 通过随机数 判断是否第一次进入拉取
     * 通过去重标识的 value 值 与查询出的list.size 比对 如果相同 则代表已经拉取过
     * 如果返回空数据 则进行获取{并且插入标识 list.size} 否则退出请求
     * RAISE_HAND_STUDENT_DISTINCT
     * @param distinct 去重标识
     * @param listMono 查询到的学生信息(Mono)
     * @param askKey
     * @return
     */
    private Mono<List<Students>> raiseDistinct(final String distinct, final Mono<List<Students>> listMono, final String askKey) {
        return listMono.filterWhen(list -> raiseIsDistinct(distinct, list.size(), askKey));
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
     * 获取课堂提问的切换值
     *
     * @return
     */
    private Mono<String> askQuestionCut(final String askKey) {
        return reactiveHashOperations.get(askKey, "cut");
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

    /**
     * 在请求数据时 通过随机数 判断是否拉取过数据
     * 使用 课堂+学生id+考题id+随机数
     * 如果返回空数据 则进行获取{并且插入标识} 否则退出请求
     *
     * @return
     */
    private Mono<OptBigQuestionVo> askDistinct(final Mono<OptBigQuestionVo> bigQuestionMono, final AchieveVo achieveVo) {
        return bigQuestionMono
                .filterWhen(bigQuestion -> distinctKeyIsEmpty(achieveVo.getDistinctKey(bigQuestion.getId()), achieveVo.getAskKey(QuestionType.BigQuestion)));
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

    /**
     * 个人对象 返回题目
     *
     * @param achieveVo
     * @return
     */
    private Mono<OptBigQuestionVo> askPeople(final AchieveVo achieveVo) {
        return reactiveHashOperations.get(achieveVo.getAskKey(QuestionType.BigQuestion), ASK_INTERACTIVE)
                .flatMap(interactive -> {
                    switch (interactive) {
                        case ASK_INTERACTIVE_RACE:
                            return selectQuestion(achieveVo).transform(this::selected);
                        case ASK_INTERACTIVE_RAISE:
                            return findBigQuestion(achieveVo.getAskKey(QuestionType.BigQuestion)).flatMap(obj -> raiseQuestion(achieveVo, obj));
                        case ASK_INTERACTIVE_SELECT:
                            return selectQuestion(achieveVo).transform(this::selected);
                        case ASK_INTERACTIVE_VOTE:
                            return Mono.empty();
                        default:
                            throw new ExamQuestionsException("非法参数 错误的数据类型");
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
                .filterWhen(achieve -> selectVerify(achieve.getAskKey(QuestionType.BigQuestion), achieve.getExamineeId()))
                .flatMap(achieve -> findBigQuestion(achieve.getAskKey(QuestionType.BigQuestion)));
    }

    private Mono<BigQuestion> findBigQuestion(final String askKey) {
        return reactiveHashOperations.get(askKey, "questionId").flatMap(bigQuestionRepository::findById);
    }

    private Mono<String> findAnswerFlag(final String askKey) {
        return reactiveHashOperations.get(askKey, "answerFlag");
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

    private Mono<Boolean> isMember(final String redisKey, final String examineeId) {
        return stringRedisTemplate.opsForSet().isMember(redisKey, examineeId);
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


    private Mono<OptBigQuestionVo> selected(final Mono<BigQuestion> bigQuestionMono) {
        return bigQuestionMono.map(bigQuestion -> new OptBigQuestionVo(ASK_QUESTIONS_SELECTED, bigQuestion));
    }

    private Mono<OptBigQuestionVo> raiseQuestion(final AchieveVo achieve, final BigQuestion bigQuestion) {
        return selectVerify(achieve.getAskKey(QuestionType.BigQuestion), achieve.getExamineeId()).map(flag -> {
            if (flag) {
                return new OptBigQuestionVo(ASK_QUESTIONS_SELECTED, bigQuestion);
            } else {
                return new OptBigQuestionVo(ASK_QUESTIONS_UN_SELECTED, bigQuestion);
            }
        });
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
     * @return
     */
    private String askQuestionsId(QuestionType type, String circleId) {
        return CLASSROOM_ASK_QUESTIONS_ID.concat(type.name()).concat(circleId);
    }

    /**
     * 获得课堂的提问id
     */
    private Mono<String> askQuestionId(final String askKey) {
        return reactiveHashOperations.get(askKey, "questionId");
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

}

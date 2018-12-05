package com.forteach.quiz.service;

import com.forteach.quiz.domain.AskAnswer;
import com.forteach.quiz.domain.BigQuestion;
import com.forteach.quiz.exceptions.AskException;
import com.forteach.quiz.exceptions.ExamQuestionsException;
import com.forteach.quiz.repository.AskAnswerRepository;
import com.forteach.quiz.repository.BigQuestionRepository;
import com.forteach.quiz.web.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

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
public class ClassInteractService {


    private final ReactiveStringRedisTemplate stringRedisTemplate;

    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;

    private final BigQuestionRepository bigQuestionRepository;

    private final AskAnswerRepository askAnswerRepository;

    public ClassInteractService(ReactiveStringRedisTemplate stringRedisTemplate, ReactiveHashOperations<String, String, String> reactiveHashOperations,
                                BigQuestionRepository bigQuestionRepository, AskAnswerRepository askAnswerRepository) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.bigQuestionRepository = bigQuestionRepository;
        this.askAnswerRepository = askAnswerRepository;
    }

    /**
     * 课堂提问发题
     *
     * @param giveVo
     * @return
     */
    public Mono<Long> sendQuestion(final GiveVo giveVo) {

        HashMap<String, String> map = new HashMap<>(3);
        map.put("questionId", giveVo.getQuestionId());
        map.put("interactive", giveVo.getInteractive());
        map.put("category", giveVo.getCategory());
        map.put("selected", giveVo.getSelected());
        map.put("cut", String.valueOf(giveVo.getCut()));

        Mono<Boolean> set = reactiveHashOperations.putAll(askQuestionsId(giveVo.getCircleId()), map);
        Mono<Boolean> time = stringRedisTemplate.expire(askQuestionsId(giveVo.getCircleId()), Duration.ofSeconds(60 * 60 * 10));
        //删除抢答答案
        Mono<Boolean> removeRace = stringRedisTemplate.opsForValue().delete(giveVo.getRaceAnswerFlag());

        return Flux.concat(set, time, removeRace
        ).filter(flag -> !flag).count();
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
                                                new AskQuestionVo(cut, bigQuestion, interactive))
                        ));
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
     * 提交答案
     * 提交答案前判断题目id 是否与当前id一致
     *
     * @return
     */
    public Mono<AskAnswer> sendAnswer(final InteractAnswerVo answerVo) {
        return askInteractiveType(answerVo.getAskKey())
                .zipWhen(type -> sendAnswerVerify(answerVo.getAskKey(), answerVo.getQuestionId(), answerVo.getCut()))
                .flatMap(tuple2 -> {
                    if (tuple2.getT2()) {
                        switch (tuple2.getT1()) {
                            case ASK_INTERACTIVE_RACE:
                                return sendRace(answerVo);
                            case ASK_INTERACTIVE_RAISE:
                                return sendSelect(answerVo);
                            case ASK_INTERACTIVE_SELECT:
                                return sendSelect(answerVo);
                            case ASK_INTERACTIVE_VOTE:
                                return Mono.empty();
                            default:
                                throw new ExamQuestionsException("非法参数 错误的数据类型");
                        }
                    } else {
                        return Mono.error(new AskException("请重新刷新获取最新题目"));
                    }
                });
    }

    /**
     * 发起提问举手
     * 清空老师端显示的举手学生
     *
     * @param
     * @return
     */
    public Mono<Long> launchRaise(final AskLaunchVo askLaunchVo) {
        return reactiveHashOperations.remove(askLaunchVo.getRaiseKey(), "examinee");
    }


    /**
     * 学生进行举手
     *
     * @return
     */
    public Mono<Boolean> raiseHand(final RaisehandVo raisehandVo) {
        return reactiveHashOperations.put(raisehandVo.getRaiseKey(), "examineeId", raisehandVo.getExamineeId());
    }

//    public Mono achieveRaise(final AchieveRaiseVo achieveRaiseVo){
//        return reactiveHashOperations.get(achieveRaiseVo.getRaiseKey(),"examinee")
//                .flatMap()
//    }

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
     * 在请求数据时 通过随机数 判断是否拉取过数据
     * 使用 课堂+学生id+考题id+随机数
     * 如果返回空数据 则进行获取{并且插入标识} 否则退出请求
     *
     * @return
     */
    private Mono<BigQuestion> askDistinct(final Mono<BigQuestion> bigQuestionMono, final AchieveVo achieveVo) {
        return bigQuestionMono
                .filterWhen(bigQuestion -> distinctKeyIsEmpty(achieveVo.getDistinctKey(bigQuestion.getId()), achieveVo.getAskKey()));
    }

    /**
     * 通过上一题推送的id 和 cut 查看是否推送过
     *
     * @param redisKey
     * @return
     */
    private Mono<String> distinct(final String redisKey) {
        return stringRedisTemplate.opsForValue().get(redisKey);
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
        return distinct(redisKey)
                .switchIfEmpty(Mono.just(ASK_DISTINCT_INITIAL))
                .zipWhen(origin -> askQuestionCut(askKey))
                .flatMap(tuple2 -> {
                    if (ASK_DISTINCT_INITIAL.equals(tuple2.getT1())) {
                        return saveCut(redisKey, tuple2.getT2());
                    } else if (tuple2.getT1().equals(tuple2.getT2())) {
                        return Mono.just(false).filterWhen(flag -> saveCut(redisKey, tuple2.getT2()));
                    } else {
                        return saveCut(redisKey, tuple2.getT2());
                    }
                });
    }

    /**
     * 个人对象 返回题目
     *
     * @param achieveVo
     * @return
     */
    private Mono<BigQuestion> askPeople(final AchieveVo achieveVo) {
        return reactiveHashOperations.get(achieveVo.getAskKey(), ASK_INTERACTIVE)
                .flatMap(interactive -> {
                    switch (interactive) {
                        case ASK_INTERACTIVE_RACE:
                            return selectQuestion(achieveVo);
                        case ASK_INTERACTIVE_RAISE:
                            return findBigQuestion(achieveVo.getAskKey());
                        case ASK_INTERACTIVE_SELECT:
                            return selectQuestion(achieveVo);
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
                .flatMap(selectId -> {
                    if (isSelected(selectId, examineeId)) {
                        return Mono.just(true);
                    } else {
                        return Mono.error(new AskException("该题未被选中 不能答题"));
                    }
                });
    }

    private Mono<BigQuestion> findBigQuestion(final String askKey) {
        return reactiveHashOperations.get(askKey, "questionId").flatMap(bigQuestionRepository::findById);
    }

    /**
     * 抢答 回答
     *
     * @return
     */
    private Mono<AskAnswer> sendRace(final InteractAnswerVo interactAnswerVo) {
        return stringRedisTemplate.hasKey(interactAnswerVo.getRaceAnswerFlag())
                .flatMap(flag -> {
                    if (flag) {
                        return Mono.error(new AskException("该抢答题已被回答"));
                    }
                    return sendSelect(interactAnswerVo)
                            .filterWhen(askAnswer -> {
                                if (askAnswer != null) {
                                    return stringRedisTemplate.opsForValue().set(interactAnswerVo.getRaceAnswerFlag(), STATUS_SUCCESS, Duration.ofSeconds(60 * 60));
                                } else {
                                    return Mono.empty();
                                }
                            });
                });
    }

//    public Mono achieveAnswer(final AskAnswerVo askAnswerVo){
//
//
//
//
//    }

    private Mono<AskAnswer> sendSelect(final InteractAnswerVo interactAnswerVo) {
        return askAnswerRepository
                .save(new AskAnswer(interactAnswerVo.getExamineeId(),
                        ASK_INTERACTIVE_RACE,
                        interactAnswerVo.getAnswer(),
                        interactAnswerVo.getQuestionId(),
                        new Date())
                );
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

    private Mono<Boolean> saveCut(final String redisKey, final String cut) {
        return stringRedisTemplate.opsForValue().set(redisKey, cut, Duration.ofSeconds(60 * 60));
    }

}

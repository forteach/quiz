package com.forteach.quiz.service;

import com.forteach.quiz.domain.BigQuestion;
import com.forteach.quiz.exceptions.ExamQuestionsException;
import com.forteach.quiz.repository.BigQuestionRepository;
import com.forteach.quiz.web.vo.AchieveVo;
import com.forteach.quiz.web.vo.GiveVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
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

    public ClassInteractService(ReactiveStringRedisTemplate stringRedisTemplate, ReactiveHashOperations<String, String, String> reactiveHashOperations,
                                BigQuestionRepository bigQuestionRepository) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.bigQuestionRepository = bigQuestionRepository;
    }


    public Mono<Long> sendQuestion(final GiveVo giveVo) {

        HashMap<String, String> map = new HashMap<>(3);
        map.put("questionId", giveVo.getQuestionId());
        map.put("interactive", giveVo.getInteractive());
        map.put("category", giveVo.getCategory());
        map.put("selected", giveVo.getSelected());

        Mono<Boolean> set = reactiveHashOperations.putAll(askQuestionsId(giveVo.getCircleId()), map);

        Mono<Boolean> time = stringRedisTemplate.expire(askQuestionsId(giveVo.getCircleId()), Duration.ofSeconds(60 * 60 * 10));

        return Flux.concat(set, time).filter(flag -> !flag).count();
    }

    public Mono<BigQuestion> achieveQuestion(final AchieveVo achieveVo) {
        return askCategoryType(achieveVo.getAskKey())
                .flatMap(type -> {
                    switch (type) {
                        case CATEGORY_PEOPLE:
                            return askPeople(achieveVo.getAskKey());
                        case CATEGORY_TEAM:
                            return askPeople(achieveVo.getAskKey());
                        default:
                            throw new ExamQuestionsException("非法参数 错误的数据类型");
                    }
                })
                .transform(bigQuestionMono -> askDistinct(bigQuestionMono, achieveVo));
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
                .filterWhen(bigQuestion -> distinctKeyIsEmpty(achieveVo.getDistinctKey(bigQuestion.getId())));
    }

    private Mono<Boolean> redisIsEmpty(final String redisKey) {
        return stringRedisTemplate.hasKey(redisKey).map(flag -> !flag);
    }

    private Mono<Boolean> distinctKeyIsEmpty(final String redisKey) {
        return redisIsEmpty(redisKey)
                .flatMap(flag -> {
                    if (flag) {
                        //通过重复验证后 添加表示
                        return stringRedisTemplate.opsForValue().set(redisKey, STATUS_SUCCESS, Duration.ofSeconds(60 * 60));
                    }
                    return Mono.just(flag);
                });
    }

    /**
     * 个人对象
     *
     * @param askKey
     * @return
     */
    private Mono<BigQuestion> askPeople(final String askKey) {
        return reactiveHashOperations.get(askKey, ASK_INTERACTIVE)
                .flatMap(interactive -> {
                    switch (interactive) {
                        case ASK_INTERACTIVE_RACE:
                            return passQuestion(askKey);
                        case ASK_INTERACTIVE_RAISE:
                            return passQuestion(askKey);
                        case ASK_INTERACTIVE_SELECT:
                            return Mono.empty();
                        case ASK_INTERACTIVE_VOTE:
                            return Mono.empty();
                        default:
                            throw new ExamQuestionsException("非法参数 错误的数据类型");
                    }
                });
    }

    /**
     * 抢答 所有人都可以接受答案
     *
     * @return
     */
    private Mono<BigQuestion> passQuestion(final String askKey) {
        return reactiveHashOperations.get(askKey, "questionId")
                .flatMap(bigQuestionRepository::findById);
    }

    public String askQuestionsId(final String classId) {
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
     * 获取选择信息
     *
     * @param askKey
     * @return
     */
    private Mono<String> askSelected(final String askKey) {
        return reactiveHashOperations.get(askKey, "selected");
    }


}

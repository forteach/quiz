package com.forteach.quiz.interaction.service;

import com.forteach.quiz.interaction.domain.MoreGiveVo;
import com.forteach.quiz.questionlibrary.domain.QuestionType;
import com.forteach.quiz.questionlibrary.repository.SurveyQuestionRepository;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;

import static com.forteach.quiz.common.KeyStorage.CLASSROOM_ASK_QUESTIONS_ID;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/14  15:08
 */
@Service
public class BrainstormInteractService {

    private final ReactiveStringRedisTemplate stringRedisTemplate;

    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    private final SurveyQuestionRepository questionRepository;

    public BrainstormInteractService(ReactiveStringRedisTemplate stringRedisTemplate,
                                     ReactiveHashOperations<String, String, String> reactiveHashOperations,
                                     ReactiveMongoTemplate reactiveMongoTemplate,
                                     SurveyQuestionRepository questionRepository) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.questionRepository = questionRepository;
    }

    /**
     * 发布问卷问题
     *
     * @param giveVo
     * @return
     */
    public Mono<Long> sendQuestion(final MoreGiveVo giveVo) {

        HashMap<String, String> map = new HashMap<>(10);
        map.put("questionId", giveVo.getQuestionId());
        map.put("category", giveVo.getCategory());
        map.put("selected", giveVo.getSelected());
        map.put("cut", giveVo.getCut());

        //如果本题和redis里的题目id不一致 视为换题 进行清理
        Mono<Boolean> clearCut = clearCut(giveVo);

        Mono<Boolean> set = reactiveHashOperations.putAll(askQuestionsId(QuestionType.BrainstormQuestion, giveVo.getCircleId()), map);
        Mono<Boolean> time = stringRedisTemplate.expire(askQuestionsId(QuestionType.BrainstormQuestion, giveVo.getCircleId()), Duration.ofSeconds(60 * 60 * 10));

        //TODO 未记录
        return Flux.concat(set, time, clearCut).filter(flag -> !flag).count();
//                .filterWhen(obj -> interactRecordExecuteService.releaseQuestion(giveVo.getCircleId(), giveVo.getQuestionId(), giveVo.getSelected(), giveVo.getCategory(), giveVo.getInteractive()));

    }

    private Mono<Boolean> clearCut(final MoreGiveVo giveVo) {
        return reactiveHashOperations.get(askQuestionsId(QuestionType.BrainstormQuestion, giveVo.getCircleId()), "questionId")
                .zipWith(Mono.just(giveVo.getQuestionId()), String::equals)
                .flatMap(flag -> {
                    if (flag) {
                        //清除提问标识
                        return Mono.just(false);
                    }
                    return stringRedisTemplate.opsForValue().delete(giveVo.getExamineeIsReplyKey(QuestionType.BrainstormQuestion));
                });
    }

    /**
     * 根据课堂 获取去重key
     *
     * @return
     */
    private String askQuestionsId(QuestionType type, String circleId) {
        return CLASSROOM_ASK_QUESTIONS_ID.concat(type.name()).concat(circleId);
    }


}

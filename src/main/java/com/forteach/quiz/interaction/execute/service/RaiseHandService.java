package com.forteach.quiz.interaction.execute.service;

import com.forteach.quiz.common.Dic;
import com.forteach.quiz.interaction.execute.config.BigQueKey;
import com.forteach.quiz.interaction.execute.service.record.InteractRecordExecuteService;
import com.forteach.quiz.questionlibrary.domain.QuestionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.Duration;

@Slf4j
@Service
public class RaiseHandService {

    private final ReactiveStringRedisTemplate stringRedisTemplate;

    private final InteractRecordExecuteService interactRecordExecuteService;

    public RaiseHandService(ReactiveStringRedisTemplate stringRedisTemplate,
                            InteractRecordExecuteService interactRecordExecuteService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.interactRecordExecuteService = interactRecordExecuteService;
    }

    /**
     * 学生进行举手
     * 最后记录
     * @return
     */
    public Mono<Long> raiseHand(final String circleId,String examineeId,String questId) {
        //创建题目提问举手的KEY=学生ID
        Mono<Long> set = stringRedisTemplate.opsForSet().add(BigQueKey.askTypeQuestionsId(QuestionType.TiWen.name(),circleId, Dic.ASK_INTERACTIVE_RAISE,questId), examineeId);
        //设置举手过期时间30分钟
        Mono<Boolean> time = stringRedisTemplate.expire(BigQueKey.askTypeQuestionsId(QuestionType.TiWen.name(),circleId, Dic.ASK_INTERACTIVE_RAISE,questId), Duration.ofSeconds(60 * 30 ));
        return set.filterWhen(r->time)
                .filterWhen(obj -> interactRecordExecuteService.raiseHand(circleId, examineeId, questId));
    }

    /**
     * 重新发起举手学生进行举手
     * 最后记录
     *
     * @return
     */
    public Mono<Long> launchRaise(final String circleId,String examineeId,String questId) {
        //清空上次举手题目的信息
        return stringRedisTemplate.delete(BigQueKey.askTypeQuestionsId(QuestionType.TiWen.name(),circleId, Dic.ASK_INTERACTIVE_RAISE,questId))
                //重新创建举手信息
                .flatMap(r->raiseHand(circleId,examineeId,questId));
    }
}

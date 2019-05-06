package com.forteach.quiz.interaction.execute.service.SingleQue;

import com.forteach.quiz.interaction.execute.service.Key.AchieveRaiseKey;
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
    public Mono<Long> raiseHand(final String circleId,final String examineeId,final String questId,final String questionType) {
        //创建题目提问举手的KEY=学生ID
        Mono<Long> set = stringRedisTemplate.opsForSet().add(AchieveRaiseKey.askTypeQuestionsId(questionType,circleId, AchieveRaiseKey.CLASSROOM_CLEAR_TAG_RAISE,questId), examineeId);
        //设置举手过期时间30分钟
        Mono<Boolean> time = stringRedisTemplate.expire(AchieveRaiseKey.askTypeQuestionsId(questionType,circleId, AchieveRaiseKey.CLASSROOM_CLEAR_TAG_RAISE,questId), Duration.ofSeconds(60 * 30 ));
        return set.filterWhen(r->time)
                .filterWhen(obj -> interactRecordExecuteService.raiseHand(circleId, examineeId, questId));
    }

    /**
     * 重新发起举手学生进行举手
     * 最后记录
     *
     * @return
     */
    public Mono<Long> launchRaise(final String circleId,final String examineeId,final String questId,final String questionType) {
        //清空上次举手题目的信息
        return stringRedisTemplate.delete(AchieveRaiseKey.askTypeQuestionsId(QuestionType.TiWen.name(),circleId, AchieveRaiseKey.CLASSROOM_CLEAR_TAG_RAISE,questId))
                //重新创建举手信息
                .flatMap(r->raiseHand(circleId,examineeId,questId,questionType));
    }

}

package com.forteach.quiz.interaction.execute.service.SingleQue;

import com.forteach.quiz.interaction.execute.domain.record.InteractRecord;
import com.forteach.quiz.interaction.execute.service.ClassRoom.ClassRoomService;
import com.forteach.quiz.interaction.execute.service.Key.AchieveRaiseKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;

import static com.forteach.quiz.common.Dic.INTERACT_RECORD_QUESTIONS;
import static com.forteach.quiz.common.Dic.MONGDB_ID;

@Slf4j
@Service
public class RaiseHandService {

    private final ReactiveStringRedisTemplate stringRedisTemplate;

//    private final InteractRecordExecuteService interactRecordExecuteService;

    private final ClassRoomService classRoomService;

    private final ReactiveMongoTemplate mongoTemplate;

    public RaiseHandService(ReactiveStringRedisTemplate stringRedisTemplate,
                            ClassRoomService classRoomService,
                            ReactiveMongoTemplate mongoTemplate
//                            InteractRecordExecuteService interactRecordExecuteService
    ) {
        this.stringRedisTemplate = stringRedisTemplate;
//        this.interactRecordExecuteService = interactRecordExecuteService;
        this.classRoomService = classRoomService;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * 学生进行举手
     * 最后记录
     *
     * @return
     */
    public Mono<Long> raiseHand(final String circleId, final String examineeId, final String questId, final String questionType) {
        //创建题目提问举手的KEY=学生ID
        Mono<Long> set = stringRedisTemplate.opsForSet().add(AchieveRaiseKey.askTypeQuestionsId(questionType, circleId, AchieveRaiseKey.CLASSROOM_CLEAR_TAG_RAISE, questId), examineeId);
        //设置举手过期时间30分钟
        Mono<Boolean> time = stringRedisTemplate.expire(AchieveRaiseKey.askTypeQuestionsId(questionType, circleId, AchieveRaiseKey.CLASSROOM_CLEAR_TAG_RAISE, questId), Duration.ofSeconds(60 * 30));
        return set.filterWhen(r -> time)
                //设置当前课堂当前活动是提问
                .filterWhen(r -> setInteractionType(circleId, questionType))
                .filterWhen(obj -> this.raiseHand(circleId, examineeId, questId));
    }

    /**
     * 学生举手时记录
     *
     * @param circleId
     * @param student
     * @param questionId
     * @return
     */
    private Mono<Boolean> raiseHand(final String circleId, final String student, final String questionId) {

        final Query query = Query.query(
                Criteria.where(MONGDB_ID).is(circleId).and(INTERACT_RECORD_QUESTIONS.concat(".raiseHandsId")).ne(student).and(INTERACT_RECORD_QUESTIONS.concat(".questionsId")).is(questionId)
        ).with(new Sort(Sort.Direction.DESC, "index")).limit(1);

        Update update = new Update();
        update.addToSet(INTERACT_RECORD_QUESTIONS.concat(".$.raiseHandsId"), student);
        update.inc(INTERACT_RECORD_QUESTIONS.concat(".$.raiseHandsNumber"), 1);

        return mongoTemplate.findAndModify(query, update, InteractRecord.class).switchIfEmpty(Mono.just(new InteractRecord())).map(Objects::nonNull);
    }

    /**
     * 重新发起举手学生进行举手
     * 最后记录
     *
     * @return
     */
    public Mono<Long> launchRaise(final String circleId, final String examineeId, final String questId, final String questionType) {
        //清空上次举手题目的信息
        return stringRedisTemplate.delete(AchieveRaiseKey.askTypeQuestionsId(questionType, circleId, AchieveRaiseKey.CLASSROOM_CLEAR_TAG_RAISE, questId))
                //重新创建举手信息
                .flatMap(r -> raiseHand(circleId, examineeId, questId, questionType));
    }

    /**
     * 设置当前课堂当前活动主题为：提问
     *
     * @param circleId
     */
    private Mono<Boolean> setInteractionType(String circleId, final String questionType) {

        return classRoomService.setInteractionType(circleId, questionType);
    }

}

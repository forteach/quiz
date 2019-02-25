package com.forteach.quiz.interaction.execute.repository;

import com.forteach.quiz.interaction.execute.domain.InteractRecord;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/3  15:22
 */
public interface InteractRecordRepository extends ReactiveMongoRepository<InteractRecord, String> {

    /**
     * 根据互动课堂id及教师id查询对象
     *
     * @param teacherId
     * @param teacherId
     * @return
     */
    Flux<InteractRecord> findByCircleIdAndTeacherId(final String circleId, final String teacherId);

    /**
     * 通过课堂id查找记录
     *
     * @param circleId
     * @return
     */
    Mono<InteractRecord> findByCircleIdIs(final String circleId);

    /**
     * 查询记录不为空的信息
     * @param circleId
     * @return
     */
    @Transactional(readOnly = true)
    @Query(value = " { 'circleId': ?0, 'questions' : {$exists:true}}")
    Mono<InteractRecord> findByCircleIdAndQuestionsNotNull(final String circleId);

    /**
     * 查询对应的记录存在不
     * @param circleId 课堂id
     * @param recordName 记录名字 questions
     * @return
     * db.interactRecord.find({"circleId" : "bd4a84e4a61943e6b07e02947ecc85f1"},{"students":1, _id: 0})
     */
    @Transactional(readOnly = true)
    @Query(value = "{'circleId': ?0, ?1 : {$exists:true}}")
    Mono<InteractRecord> findByCircleIdAndRecord(final String circleId, final String recordName);

    /**
     * 查询答题信息
     * @param circleId　课堂id
     * @param questionsId 学生 id
     * @return
     */
//    @Query(value = "{'circleId': ?0, 'questions.questionsId' : ?1 }", fields = "{ '_id' : 1, 'selectId' : 1}")
    @Transactional(readOnly = true)
    @Query(value = "{'circleId': ?0, 'questions.questionsId' : ?1 }")
    Mono<InteractRecord> findByCircleIdAndQuestionsId(final String circleId, final String questionsId);
}

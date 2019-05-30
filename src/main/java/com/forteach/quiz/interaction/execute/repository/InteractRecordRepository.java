package com.forteach.quiz.interaction.execute.repository;

import com.forteach.quiz.interaction.execute.domain.record.InteractRecord;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
/**
 * @Description: 记录课堂的交互情况 学生回答情况
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
//    Flux<InteractRecord> findByIdAndTeacherId(final String circleId, final String teacherId);

    /**
     * 通过课堂id查找记录
     *
     * @param circleId
     * @return
     */
//    Mono<InteractRecord> findByCircleIdIs(final String circleId);
    /**
     * 通过课堂id和习题册id
     *
     * @param id
     * @return
     */
//    @Transactional(readOnly = true)
//    @Query(value = "{'_id': ?0, 'exerciseBooks.questionsId' : ?1 }", fields = "{ '_id' : 0, 'exerciseBooks' : 1}")
//    Mono<ExerciseBooksDto> findExerciseBooksByIdAndQuestionsId(final String id, final String questionsId);

    /**
     * 查询记录不为空的信息
     * @param id
     * @return
     */
//    @Transactional(readOnly = true)
//    @Query(value = " { '_id': ?0, 'questions' : {$exists:true}}")
//    Mono<InteractRecord> findByIdAndQuestionsNotNull(final String id);

    /**
     * 查询对应的记录存在不
     * @param id 课堂id
     * @param recordName 记录名字 questions
     * @return
     * db.interactRecord.find({"_id" : "bd4a84e4a61943e6b07e02947ecc85f1"},{"students":1, _id: 0})
     */
//    @Transactional(readOnly = true)
//    @Query(value = "{'_id': ?0, ?1 : {$exists:true}}")
//    Mono<InteractRecord> findByIdAndRecord(final String id, final String recordName);

    /**
     * 查询答题信息
     * @param 　课堂id
     * @param questionsId 问题 id
     * @return
     */
//    @Transactional(readOnly = true)
//    @Query(value = "{'_id': ?0 , 'questions.questionsId' : ?1}", fields = "{ '_id' : 0, 'questions' : 1}")
//    Mono<QuestionsDto> findRecordByIdAndQuestionsId(final String objectId, final String questionsId);

    /**
     * 查询问卷记录
     * @param id　课堂id
     * @param questionsId 问题 id
     * @return
     */
//    @Transactional(readOnly = true)
//    @Query(value = "{'_id': ?0, 'surveys.questionsId' : ?1 }", fields = "{ '_id' : 0, 'surveys' : 1}")
//    Mono<SurveysDto> findRecordSurveysByIdAndQuestionsId(final String id, final String questionsId);

    /**
     * 查询任务记录
     * @param id
     * @param questionsId
     * @return
     */
//    @Transactional(readOnly = true)
//    @Query(value = "{'_id': ?0, 'interacts.questionsId' : ?1 }", fields = "{ '_id' : 0, 'interacts' : 1}")
//    Mono<TaskInteractDto> findRecordTaskByIdAndQuestionsId(final String id, final String questionsId);

    /**
     * 查询头脑风暴记录
     * @param id
     * @param questionsId
     * @return
     */
//    @Transactional(readOnly = true)
//    @Query(value = "{'_id' : ?0, 'brainstorms.questionsId' : ?1 }", fields = "{'_id' : 0, 'brainstorms' : 1}")
//    Mono<BrainstormDto> findBrainstormsByIdAndQuestionsId(final String id, final String questionsId);
}

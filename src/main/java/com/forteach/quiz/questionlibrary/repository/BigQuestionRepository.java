package com.forteach.quiz.questionlibrary.repository;

import com.forteach.quiz.questionlibrary.domain.BigQuestion;
import com.forteach.quiz.questionlibrary.repository.base.QuestionMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  11:16
 */
@Repository
public interface BigQuestionRepository extends QuestionMongoRepository<BigQuestion> {
    //    List<BigQuestion> findAllByGro
//    @Query(value = "$group: {}")
    List findGroupByCourseId();
}

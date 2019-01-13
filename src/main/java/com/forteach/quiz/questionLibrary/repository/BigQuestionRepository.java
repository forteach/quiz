package com.forteach.quiz.questionLibrary.repository;

import com.forteach.quiz.questionLibrary.domain.BigQuestion;
import com.forteach.quiz.questionLibrary.repository.base.QuestionMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  11:16
 */
@Repository
public interface BigQuestionRepository extends QuestionMongoRepository<BigQuestion> {
}

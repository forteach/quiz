package com.forteach.quiz.questionLibrary.repository;

import com.forteach.quiz.questionLibrary.domain.BrainstormQuestion;
import com.forteach.quiz.questionLibrary.repository.base.QuestionMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/10  11:46
 */
@Repository
public interface BrainstormQuestionRepository extends QuestionMongoRepository<BrainstormQuestion> {
}

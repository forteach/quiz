package com.forteach.quiz.questionlibrary.repository;

import com.forteach.quiz.questionlibrary.domain.TaskQuestion;
import com.forteach.quiz.questionlibrary.repository.base.QuestionMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/10  11:48
 */
@Repository
public interface TaskQuestionRepository extends QuestionMongoRepository<TaskQuestion> {
}

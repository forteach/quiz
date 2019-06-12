package com.forteach.quiz.practiser.repository;

import com.forteach.quiz.practiser.domain.ExerciseAnswerQuestionBook;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-12 14:20
 * @version: 1.0
 * @description: 学生回答作业练习的记录及评分、快照、评价
 */
public interface AskAnswerQuestionBookRepository extends ReactiveMongoRepository<ExerciseAnswerQuestionBook, String> {
}

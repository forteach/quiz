package com.forteach.quiz.repository;

import com.forteach.quiz.problemsetlibrary.domain.base.ExerciseBook;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/16  9:10
 */
public interface ExerciseBookRepository extends ReactiveMongoRepository<ExerciseBook, String> {
}

package com.forteach.quiz.problemsetlibrary.repository.base;

import com.forteach.quiz.problemsetlibrary.domain.base.ExerciseBook;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/13  21:33
 */
@NoRepositoryBean
public interface ExerciseBookMongoRepository<T extends ExerciseBook> extends ReactiveMongoRepository<T, String> {
}

package com.forteach.quiz.problemsetlibrary.repository;

import com.forteach.quiz.problemsetlibrary.domain.BigQuestionExerciseBook;
import com.forteach.quiz.problemsetlibrary.repository.base.ExerciseBookMongoRepository;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/13  21:36
 */
public interface BigQuestionExerciseBookRepository extends ExerciseBookMongoRepository<BigQuestionExerciseBook> {


//    @Query(value = " {'exeBookType': ?0, 'courseId': ?1, 'chapterId': ?2}", fields = "{'questionChildren._id': 1}")
//    Mono<List<String>> findByCourseIdAndChapterId(final String exeBookType, final String courseId, final String chapterId);
}

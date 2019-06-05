package com.forteach.quiz.practiser.repository;

import com.forteach.quiz.practiser.domain.AskAnswerExercise;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-5 10:36
 * @version: 1.0
 * @description:
 */
public interface AskAnswerExerciseRepository extends ReactiveMongoRepository<AskAnswerExercise, String> {

}

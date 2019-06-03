package com.forteach.quiz.practiser.repository;

import com.forteach.quiz.practiser.domain.ExerciseBookAnswer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-3 13:50
 * @version: 1.0
 * @description:
 */
public interface ExerciseBookAnswerRepository extends ReactiveMongoRepository<ExerciseBookAnswer, String> {

}

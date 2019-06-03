package com.forteach.quiz.practiser.repository;

import com.forteach.quiz.practiser.domain.AskAnswerStudents;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-3 16:37
 * @version: 1.0
 * @description:
 */
public interface AskAnswerStudentsRepository extends ReactiveMongoRepository<AskAnswerStudents, String> {

}

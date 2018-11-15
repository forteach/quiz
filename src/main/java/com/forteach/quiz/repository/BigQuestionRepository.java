package com.forteach.quiz.repository;

import com.forteach.quiz.domain.BigQuestion;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  11:16
 */
public interface BigQuestionRepository extends ReactiveMongoRepository<BigQuestion,String> {
}

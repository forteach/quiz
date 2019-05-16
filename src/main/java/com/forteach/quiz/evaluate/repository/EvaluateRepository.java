package com.forteach.quiz.evaluate.repository;

import com.forteach.quiz.evaluate.domain.Cumulative;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/17  10:21
 */
public interface EvaluateRepository extends ReactiveMongoRepository<Cumulative, String> {
}

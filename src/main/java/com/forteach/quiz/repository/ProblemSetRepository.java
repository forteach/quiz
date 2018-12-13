package com.forteach.quiz.repository;

import com.forteach.quiz.domain.ProblemSet;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/11  16:42
 */
public interface ProblemSetRepository extends ReactiveMongoRepository<ProblemSet, String> {
}

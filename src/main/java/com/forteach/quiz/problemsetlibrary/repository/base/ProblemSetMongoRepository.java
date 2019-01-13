package com.forteach.quiz.problemsetlibrary.repository.base;

import com.forteach.quiz.problemsetlibrary.domain.base.ProblemSet;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/13  18:42
 */
@NoRepositoryBean
public interface ProblemSetMongoRepository<T extends ProblemSet> extends ReactiveMongoRepository<T, String> {
}

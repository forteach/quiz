package com.forteach.quiz.repository;

import com.forteach.quiz.domain.ProblemSetBackup;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/21  15:52
 */
public interface ProblemSetBackupRepository extends ReactiveMongoRepository<ProblemSetBackup, String> {
}

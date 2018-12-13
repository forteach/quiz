package com.forteach.quiz.repository;

import com.forteach.quiz.domain.Reward;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * @Description: 提问 答题奖励
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/11  14:06
 */
public interface RewardRepository extends ReactiveMongoRepository<Reward, String> {
}

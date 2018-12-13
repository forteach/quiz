package com.forteach.quiz.service;

import com.forteach.quiz.domain.Reward;
import com.forteach.quiz.repository.RewardRepository;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/11  14:09
 */
@Service
public class RewardService {

    private final RewardRepository rewardRepository;

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public RewardService(RewardRepository rewardRepository, ReactiveMongoTemplate reactiveMongoTemplate) {
        this.rewardRepository = rewardRepository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    /**
     * 对学生的奖励进行累加
     *
     * @return
     */
    public Mono<UpdateResult> cumulative(final String sutdentId, final Double amount) {

        Query query = Query.query(Criteria.where("sutdentId").is(sutdentId));

        Update update = new Update();
        update.inc("amount", amount);

        return reactiveMongoTemplate.upsert(query, update, Reward.class);
    }


}

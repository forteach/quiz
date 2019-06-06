package com.forteach.quiz.evaluate.service;

import cn.hutool.core.util.NumberUtil;
import com.forteach.quiz.evaluate.config.RewardKey;
import com.forteach.quiz.evaluate.domain.Reward;
import com.forteach.quiz.evaluate.repository.RewardRepository;
import com.forteach.quiz.evaluate.web.control.res.CumulativeRes;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * @Description:
 * @author: zjw
 * @version: V1.0
 * @date: 2018/12/11  14:09
 */
@Service
public class RewardService {

    private final RewardRepository rewardRepository;

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    private final ReactiveStringRedisTemplate stringRedisTemplate;

    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;

    public RewardService(RewardRepository rewardRepository, ReactiveHashOperations<String, String, String> reactiveHashOperations, ReactiveMongoTemplate reactiveMongoTemplate,ReactiveStringRedisTemplate stringRedisTemplate) {
        this.rewardRepository = rewardRepository;
        this.stringRedisTemplate=stringRedisTemplate;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
    }

    /**
     * 对学生的奖励进行累加
     *
     * @return
     */
    public Mono<CumulativeRes> cumulative(final  String circleId,final String sutdentId, final int addNum) {

        //redis添加小红花数量
        Mono<String> add=redisFlowerAdd(circleId,sutdentId,addNum);

        //Mongo记录小红花数量
        Query query = Query.query(Criteria.where("sutdentId").is(sutdentId));
        Update update = new Update()
        .inc("amount", addNum);

        //没有记录小红花明细

        return add
                .flatMap(num->Mono.just(new CumulativeRes(circleId,sutdentId,num,RewardKey.REWARD_FLOWER_KEY)))
               .filterWhen(num->reactiveMongoTemplate.upsert(query, update, Reward.class).flatMap(r->Mono.just(r.wasAcknowledged())));
    }

    public Mono<CumulativeRes> cumulativeResMono(final  String circleId,final String studentId, final int addNum) {
        Query query = Query.query(Criteria.where("sutdentId").is(studentId));
        Update update = new Update().inc("amount", addNum);

        //没有记录小红花明细

        return reactiveMongoTemplate.upsert(query, update, Reward.class)
                .map(UpdateResult::wasAcknowledged)
                .flatMap(b -> {
                    if (b){
                        return reactiveMongoTemplate.findOne(query, Reward.class)
                                .map(Reward::getAmount)
                                .flatMap(num -> Mono.just(new CumulativeRes(circleId,studentId, NumberUtil.toStr(num),RewardKey.REWARD_FLOWER_KEY)));
                    }else {
                        return Mono.error(new Throwable("保存奖励记录失败"));
                    }
                });
    }

    /**
     * 获得学生已存在的红花，并增加小红花
     * @param circleId
     * @param sutdentId
     * @param amount
     * @return
     */
    private Mono<String> redisFlowerAdd(final  String circleId,final String sutdentId, final int amount){
        //当前课堂的奖励MAP
        String  key=RewardKey.rewardAddKey(circleId,RewardKey.REWARD_FLOWER_KEY);
        return reactiveHashOperations.hasKey(key,sutdentId).flatMap(r-> {
            if(r) {
                return stringRedisTemplate.opsForValue().get(key)
                        .flatMap(num->Mono.just(String.valueOf(Integer.parseInt(num)+amount)))
                        .filterWhen(num->reactiveHashOperations.put(key,sutdentId, String.valueOf(Integer.parseInt(num)+amount)));
            }else{
                return Mono.just(String.valueOf(amount))
                .filterWhen(num->reactiveHashOperations.put(key,sutdentId, num))
                .filterWhen(num->stringRedisTemplate.expire(key, Duration.ofSeconds(60*60*2))) ;
            }
                }
        );


    }

}

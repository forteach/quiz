package com.forteach.quiz.evaluate.Service;

import com.forteach.quiz.evaluate.config.RewardKey;
import com.forteach.quiz.evaluate.domain.Reward;
import com.forteach.quiz.evaluate.web.control.res.CumulativeRes;
import com.forteach.quiz.repository.RewardRepository;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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

    public RewardService(RewardRepository rewardRepository, ReactiveMongoTemplate reactiveMongoTemplate,ReactiveStringRedisTemplate stringRedisTemplate) {
        this.rewardRepository = rewardRepository;
        this.stringRedisTemplate=stringRedisTemplate;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
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

    //获得学生已存在的红花，并增加小红花
    private Mono<String> redisFlowerAdd(final  String circleId,final String sutdentId, final int amount){
        String  key=RewardKey.rewardAddKey(circleId,sutdentId,RewardKey.REWARD_FLOWER_KEY);
        return stringRedisTemplate.hasKey(key).flatMap(r-> {
            if(r) {
                return stringRedisTemplate.opsForValue().get(key)
                        .flatMap(num->Mono.just(String.valueOf(Integer.parseInt(num)+amount)))
                        .filterWhen(num->stringRedisTemplate.opsForValue().set(key, String.valueOf(Integer.parseInt(num)+amount), Duration.ofSeconds(60 * 60 * 12)));
            }else{
                return Mono.just(String.valueOf(amount))
                .filterWhen(num->stringRedisTemplate.opsForValue().set(key, num, Duration.ofSeconds(60 * 60 * 12)));
            }
                }
        );


    }

}

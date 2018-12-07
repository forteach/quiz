package com.forteach.quiz;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/4  16:08
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

//    @Resource
//    private ReactiveStringRedisTemplate stringRedisTemplate;

    @Resource
    private ReactiveHashOperations<String, String, String> reactiveHashOperations;

//    public RedisTest(ReactiveStringRedisTemplate stringRedisTemplate,
//                     ReactiveHashOperations<String, String, String> reactiveHashOperations) {
//        this.stringRedisTemplate = stringRedisTemplate;
//        this.reactiveHashOperations = reactiveHashOperations;
//    }

    @Test
    public void studentAdd() {


        Mono<Long> mono = Flux.range(1, 100)
                .flatMap(i -> {
                    Map<String, String> map = new HashMap<>(5);
                    map.put("id", String.valueOf(10000 + i));
                    map.put("name", "学生" + i);
                    map.put("portrait", "https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=1670224544,3291774560&fm=58&bpow=464&bpoh=608");
                    return reactiveHashOperations.putAll(("studentsData$".concat(String.valueOf(10000 + i))), map);
                }).count();

        StepVerifier.create(mono).expectNextCount(100).verifyComplete();

    }

    @Test
    public void testDate() {
        System.out.println(new Date());
    }


}

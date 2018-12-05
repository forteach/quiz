package com.forteach.quiz;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/4  16:08
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    private final ReactiveStringRedisTemplate stringRedisTemplate;

    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;

    public RedisTest(ReactiveStringRedisTemplate stringRedisTemplate,
                     ReactiveHashOperations<String, String, String> reactiveHashOperations) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
    }

    @Test
    public void studentAdd() {


    }


}

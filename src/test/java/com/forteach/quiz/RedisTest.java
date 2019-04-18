package com.forteach.quiz;


import com.forteach.quiz.web.pojo.Students;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private final ReactiveStringRedisTemplate stringRedisTemplate;
    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;

    public RedisTest(ReactiveStringRedisTemplate stringRedisTemplate, ReactiveHashOperations<String, String, String> reactiveHashOperations) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
    }

    @Test
    public void studentAdd() {


        Mono<Long> mono = Flux.range(1, 100)
                .flatMap(i -> {
                    Map<String, String> map = new HashMap<>(5);
                    map.put("id", String.valueOf(10000 + i));
                    map.put("name", "学生" + i);
                    map.put("portrait", "https://cdn.v2ex.com/gravatar/cc2fa800888e12870e0739750cd9c9e7.jpg?s=100&d=identicon");
                    return reactiveHashOperations.putAll(("studentsData$".concat(String.valueOf(10000 + i))), map);
                }).count();
////
        StepVerifier.create(mono).expectNextCount(100).verifyComplete();
//
//        Mono<Long> mono = stringRedisTemplate.opsForSet().size("distinctAnswHand120001234");
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
    }

    @Test
    public void testL() {
        stringRedisTemplate.opsForList().leftPush("tlist","1");
        stringRedisTemplate.opsForList().leftPush("tlist","2");
        stringRedisTemplate.opsForList().leftPush("tlist","3");
        stringRedisTemplate.opsForList().index("list",1).subscribe(System.out::println);
        System.out.println("----------------------------------------");
        stringRedisTemplate.opsForList().range("list",0,-1)
        .subscribe(System.out::println);

    }

    @Test
    public void testHash() {
//        Flux.range(1, 10)
//                .flatMap(i -> {
//                    Map<String, Object> map = new HashMap<>(16);
//                    List<Students> studentsList = new ArrayList<>();
//                    studentsList.add(Students.builder()
//                            .id(i + "100")
//                            .name("100000" + i)
//                            .portrait("https://cdn.v2ex.com/gravatar/" + i + "999999999.jpg?s=100&d=identicon")
//                            .build());
//                    map.put("students", studentsList);
//                    map.put("teacherId", "55212132");
//                    map.put("expType", "forever");
//                    reactiveHashOperations.putAll(("studentsData$".concat(String.valueOf(10000 + i))), map);
//                    reactiveHashOperations.putAll(("studentsData$".concat(String.valueOf(10000 + i))), map);
//                    return stringRedisTemplate.expire(("studentsData$".concat(String.valueOf(10000 + i))), Duration.ofHours(1));
//                    Map<String, String> map = new HashMap<>(5);
//                    map.put("id", String.valueOf(10000 + i));
//                    map.put("name", "学生" + i);
//                    map.put("portrait", "https://cdn.v2ex.com/gravatar/cc2fa800888e12870e0739750cd9c9e7.jpg?s=100&d=identicon");
//                    return reactiveHashOperations.putAll(("studentsData$".concat(String.valueOf(10000 + i))), map);
//                });
    }

}

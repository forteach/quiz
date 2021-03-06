package com.forteach.quiz.service;

import com.forteach.quiz.web.pojo.Students;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.forteach.quiz.common.KeyStorage.STUDENT_ADO;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/5  23:20
 */
@Service
public class StudentsService {

    private final ReactiveStringRedisTemplate stringRedisTemplate;

    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;

    public StudentsService(ReactiveStringRedisTemplate stringRedisTemplate, ReactiveHashOperations<String, String, String> reactiveHashOperations) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
    }


    public Mono<Students> findStudentsBrief(final String id) {
        return Mono.just(id)
                .zipWith(findStudentsName(id), (i, n) -> Students.builder().id(i).name(n).build())
                .zipWith(findStudentsPortrait(id), (s, p) -> {
                    s.setPortrait(p);
                    return s;
                });
    }


    public Mono<String> findStudentsName(final String id) {
        return reactiveHashOperations.get(STUDENT_ADO.concat(id), "name");
    }

    public Mono<String> findStudentsPortrait(final String id) {
        return reactiveHashOperations.get(STUDENT_ADO.concat(id), "portrait");
    }


}

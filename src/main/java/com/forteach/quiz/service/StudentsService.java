package com.forteach.quiz.service;

import com.forteach.quiz.web.pojo.Students;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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


    /**
     * 根据学生id转换为用户学生信息
     * @param strings
     * @return
     */
    public Mono<List<Students>> exchangeStudents(List<String> strings){
        return Flux.fromIterable(strings)
                .flatMap(this::findStudentsBrief)
                .collectList();
    }

    /**
     * 从redis 查询学生信息并返回相关对象
     * @param id 加入课堂的学生id
     * @return 学生 Mono
     */
    public Mono<Students> findStudentsBrief(final String id) {
        return Mono.just(id)
                .zipWith(findStudentsName(id), (i, n) -> Students.builder().id(i).name(n).build())
                .zipWith(findStudentsPortrait(id), (s, p) -> {
                    s.setPortrait(p);
                    return s;
                });
    }


    /**
     * 从redis 查找学生名称
     * @param id　学生对应的id
     * @return 学生名字
     */
    public Mono<String> findStudentsName(final String id) {
        return reactiveHashOperations.get(STUDENT_ADO.concat(id), "name");
    }

    /**
     * 从redis 查找学生头像
     * @param id　学生对应的id
     * @return 学生头像对应的url
     */
    public Mono<String> findStudentsPortrait(final String id) {
        return reactiveHashOperations.get(STUDENT_ADO.concat(id), "portrait");
    }


}

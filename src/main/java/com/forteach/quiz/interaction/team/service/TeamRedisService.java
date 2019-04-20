package com.forteach.quiz.interaction.team.service;

import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.interaction.team.web.req.ChangeTeamReq;
import com.forteach.quiz.interaction.team.web.req.DeleteTeamReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-4-20 18:42
 * @version: 1.0
 * @description:
 */
@Service
public class TeamRedisService {

    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;
    private final ReactiveRedisTemplate redisTemplate;
    private final ReactiveStringRedisTemplate stringRedisTemplate;
    public TeamRedisService(ReactiveHashOperations<String, String, String> reactiveHashOperations,
                            ReactiveStringRedisTemplate stringRedisTemplate,
                            ReactiveRedisTemplate reactiveRedisTemplate){
        this.reactiveHashOperations = reactiveHashOperations;
        this.redisTemplate = reactiveRedisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    Mono<String> getRedisStudents(final String key){
        return reactiveHashOperations.get(key, "students")
                .flatMap(s -> MyAssert.isNull(s, DefineCode.ERR0002, "不存在相关记录"));
    }
    Mono<Boolean> redisHasKey(final String key){
        return stringRedisTemplate.hasKey(key)
                .filterWhen(b -> MyAssert.isFalse(b, DefineCode.ERR0002, "不存在相关记录"));
    }

    Mono<String> findCircleId(final String key){
        return reactiveHashOperations.get(key, "circleId")
                .flatMap(c -> MyAssert.isNull(c, DefineCode.ERR0002, "课程(课堂)信息不存在"));
    }

    Mono<String> findClassId(final String key){
        return reactiveHashOperations.get(key, "classId")
                .flatMap(c -> MyAssert.isNull(c, DefineCode.ERR0002, "班级信息不存在"));
    }

    Mono<Boolean> putRedisStudents(final String key, final List<String> students){
        return reactiveHashOperations.put(key, "students", this.studentsListToStr(students))
                .flatMap(b -> MyAssert.isFalse(!b, DefineCode.ERR0013, "redis修改失败"));
    }

    /**
     * list --> strings
     *
     * @param students
     * @return
     */
    String studentsListToStr(final List<String> students) {
        return String.join(",", students.toArray(new String[students.size()]));
    }

    Mono<Boolean> deleteTeams(final String key) {
        return stringRedisTemplate.opsForSet()
                .members(key)
                .collectList()
                .flatMapMany(Flux::fromIterable)
                .flatMap(teamId -> reactiveHashOperations.delete(ChangeTeamReq.concatTeamKey(teamId)))
                .collectList()
                .flatMap(l -> {
                    return reactiveHashOperations.delete(key);
                })
                .flatMap(f -> Mono.just(true));
    }
}

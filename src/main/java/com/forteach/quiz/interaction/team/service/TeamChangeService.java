package com.forteach.quiz.interaction.team.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-4-22 17:08
 * @version: 1.0
 * @description:
 */
@Service
public class TeamChangeService {

    /**
     * 移除已经加入的学生
     *
     * @param studentsLess 需要移除的学生
     * @param stringJoin   原来加入的学生
     * @return 移除后的学生列表
     */
    Mono<List<String>> lessJoinTeamStudents(final String studentsLess, final String stringJoin) {
        return Mono.just(studentsLess)
                .map(s -> Arrays.asList(s.split(",")))
                .flatMap(strings -> {
                    List<String> list = Arrays.asList(stringJoin.split(","));
                    List<String> stringList = new ArrayList<>();
                    list.forEach(s -> {
                        strings.forEach(ss -> {
                            if (!s.equals(ss)) {
                                stringList.add(s);
                            }
                        });
                    });

                    return Mono.just(stringList);
                });
    }

    /**
     * 添加学生
     *
     * @param studentsAdd  新添加的学生id字符串
     * @param studentsJoin 原来已经加入的学生id字符串
     * @return 全部加入的学生id字符串
     */
    Mono<List<String>> moreJoinTeamStudents(final String studentsAdd, final String studentsJoin) {
        return Mono.just(studentsJoin)
                .map(s -> new HashSet<>(Arrays.asList(s.split(","))))
                .flatMap(set -> {
                    set.addAll(Arrays.asList(studentsAdd.split(",")));
                    return Mono.just(new ArrayList<>(set));
                });
    }
}
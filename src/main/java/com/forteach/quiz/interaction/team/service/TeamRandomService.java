package com.forteach.quiz.interaction.team.service;

import cn.hutool.core.util.IdUtil;
import com.forteach.quiz.exceptions.CustomException;
import com.forteach.quiz.interaction.execute.service.ClassRoomService;
import com.forteach.quiz.interaction.team.web.req.ChangeTeamReq;
import com.forteach.quiz.interaction.team.web.req.GroupRandomReq;
import com.forteach.quiz.interaction.team.web.resp.GroupTeamResp;
import com.forteach.quiz.interaction.team.web.resp.TeamResp;
import com.forteach.quiz.service.StudentsService;
import com.forteach.quiz.web.pojo.Students;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-4-11 13:53
 * @version: 1.0
 * @description:
 */
@Service
@Slf4j
public class TeamRandomService {

    private final ReactiveStringRedisTemplate stringRedisTemplate;
    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final ReactiveRedisTemplate redisTemplate;
    private final ClassRoomService classRoomService;
    private final StudentsService studentsService;
    private final TeamRedisService teamRedisService;

    public TeamRandomService(ReactiveStringRedisTemplate stringRedisTemplate,
                             ReactiveHashOperations<String, String, String> reactiveHashOperations,
                             ReactiveMongoTemplate reactiveMongoTemplate,
                             ReactiveRedisTemplate reactiveRedisTemplate,
                             TeamRedisService teamRedisService,
                             ClassRoomService classRoomService,
                             StudentsService studentsService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.redisTemplate = reactiveRedisTemplate;
        this.classRoomService = classRoomService;
        this.studentsService = studentsService;
        this.teamRedisService = teamRedisService;
    }




    Mono<GroupTeamResp> groupTeam(final List<Students> list, final GroupRandomReq randomVo) {
        return Mono.just(list)
                .map(students -> {
                    //总数 , 组数 , 每组个数 , 余数 ,余数累加值
                    int size = students.size();
                    int teamNumber = randomVo.getNumber();
                    int tuple = size / teamNumber;
                    int residue = size % teamNumber;
                    int cumulative = 0;

                    GroupTeamResp grouping = new GroupTeamResp();

                    //截取分组
                    for (int i = 0; i < teamNumber; i++) {

                        List<Students> studentsList;
                        //余数累加
                        if (residue != 0) {
                            studentsList = students.subList(i * tuple + cumulative, i * tuple + tuple + 1 + cumulative);
                            cumulative++;
                            residue--;
                        } else {
                            studentsList = students.subList(i * tuple + cumulative, i * tuple + tuple + cumulative);
                        }
                        grouping.addTeamList(new TeamResp(IdUtil.objectId(), "小组 ".concat(String.valueOf(i + 1)), studentsList));
                    }
                    return grouping;
                });
    }



    Mono<TeamResp> findTeam(final String teamId) {
        return teamRedisService.getRedisStudents(ChangeTeamReq.concatTeamKey(teamId))
                .flatMap(this::findListStudentsByStudentStr)
                .filter(Objects::nonNull)
                .flatMap(studentsList -> {
                    return reactiveHashOperations.get(ChangeTeamReq.concatTeamKey(teamId), "teamName")
                            .flatMap(name -> {
                                return Mono.just(new TeamResp(teamId, name, studentsList));
                            });
                });
    }

    Mono<List<Students>> findListStudentsByStudentStr(final String students) {
        return Mono.just(Arrays.asList(students.split(",")))
                .flatMap(studentsService::exchangeStudents);
    }




    /**
     * 至少每组两个人
     *
     * @param size
     * @param number
     * @return
     */
    Mono<Boolean> allotVerify(final Long size, final Integer number) {
        return Mono.just(size)
                .map(count -> count > number * 2)
                .map(flag -> {
                    if (flag) {
                        return true;
                    } else {
                        throw new CustomException("分组时 至少需要条件达到每组两个人");
                    }
                });
    }

    /**
     * 打乱学生列表顺序
     *
     * @param listMono
     * @return
     */
    Mono<List<Students>> shuffle(final Mono<List<Students>> listMono) {
        return listMono
                .map(list -> {
                    Collections.shuffle(list);
                    return list;
                });
    }

}


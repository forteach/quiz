package com.forteach.quiz.interaction.team.service;

import cn.hutool.core.util.IdUtil;
import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.exceptions.CustomException;
import com.forteach.quiz.interaction.team.domain.Team;
import com.forteach.quiz.interaction.team.web.req.ChangeTeamReq;
import com.forteach.quiz.interaction.team.web.req.GroupRandomReq;
import com.forteach.quiz.interaction.team.web.resp.GroupTeamResp;
import com.forteach.quiz.web.pojo.Students;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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

    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;
    private final TeamRedisService teamRedisService;

    public TeamRandomService(ReactiveHashOperations<String, String, String> reactiveHashOperations,
                             TeamRedisService teamRedisService) {
        this.reactiveHashOperations = reactiveHashOperations;
        this.teamRedisService = teamRedisService;
    }

    Mono<GroupTeamResp> groupTeamBuild(final List<Students> list, final GroupRandomReq random) {
        return Mono.just(list)
                .flatMap(studentsList -> MyAssert.isNull(studentsList, DefineCode.ERR0002, "不存在相关数据"))
                .transform(this::shuffle)
                .flatMap(l -> this.groupTeam(l, random));
    }

    /**
     * 随机分组 根据班级或课堂人数进行分组,从前到后排序多出部分依次从第一组到最后一组添加
     *
     * @param list
     * @param randomVo
     * @return
     */
    private Mono<GroupTeamResp> groupTeam(final List<Students> list, final GroupRandomReq randomVo) {
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
                        grouping.addTeamList(new Team(IdUtil.objectId(), "小组 ".concat(String.valueOf(i + 1)), studentsList));
                    }
                    return grouping;
                });
    }

    /**
     * 从redis中查询小组信息转换为需要数据传给前台
     *
     * @param teamId
     * @return
     */
    Mono<Team> findTeam(final String teamId) {
        return teamRedisService.getRedisStudents(ChangeTeamReq.concatTeamKey(teamId))
                .flatMap(teamRedisService::findStudentsListByStr)
                .filter(Objects::nonNull)
                .flatMap(studentsList -> {
                    return reactiveHashOperations.get(ChangeTeamReq.concatTeamKey(teamId), "teamName")
                            .flatMap(name -> {
                                return Mono.just(new Team(teamId, name, studentsList));
                            });
                });
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
    private Mono<List<Students>> shuffle(final Mono<List<Students>> listMono) {
        return listMono
                .map(list -> {
                    Collections.shuffle(list);
                    return list;
                });
    }

}


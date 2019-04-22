package com.forteach.quiz.interaction.team.service;

import cn.hutool.core.util.IdUtil;
import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.interaction.execute.service.ClassRoomService;
import com.forteach.quiz.interaction.team.web.req.*;
import com.forteach.quiz.interaction.team.web.resp.GroupTeamResp;
import com.forteach.quiz.interaction.team.web.resp.TeamResp;
import com.forteach.quiz.web.pojo.Students;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

import static com.forteach.quiz.common.Dic.TEAM_FOREVER;
import static com.forteach.quiz.common.Dic.TEAM_TEMPORARILY;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/22  15:48
 */
@Service
@Slf4j
public class TeamService {

    private final ReactiveStringRedisTemplate stringRedisTemplate;
    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;
    private final ClassRoomService classRoomService;
    private final TeamRedisService teamRedisService;
    private final TeamRandomService teamRandomService;
    private final TeamChangeService teamChangeService;

    public TeamService(ReactiveStringRedisTemplate stringRedisTemplate,
                       ReactiveHashOperations<String, String, String> reactiveHashOperations,
                       TeamRandomService teamRandomService,
                       ClassRoomService classRoomService,
                       TeamChangeService teamChangeService,
                       TeamRedisService teamRedisService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.teamRandomService = teamRandomService;
        this.teamChangeService = teamChangeService;
        this.classRoomService = classRoomService;
        this.teamRedisService = teamRedisService;
    }

    /**
     * 随机分组
     *
     * @return
     */
    public Mono<GroupTeamResp> groupRandom(final GroupRandomReq random) {

        return Mono.just(random)
                .filterWhen(randomVo -> {
                    if (TEAM_TEMPORARILY.equals(randomVo.getExpType())) {
                        return classRoomService.studentNumber(randomVo.getCircleId())
                                .flatMap(number -> MyAssert.isNull(number, DefineCode.ERR0002, "不存在相关数据"))
                                .flatMap(number -> teamRandomService.allotVerify(number, randomVo.getNumber()))
                                .flatMap(f -> MyAssert.isFalse(f, DefineCode.ERR0002, "分组时 至少需要条件达到每组两个人"));
                    } else if (TEAM_FOREVER.equals(randomVo.getExpType())) {
                        return Mono.just(true);
                    } else {
                        return MyAssert.isNull(null, DefineCode.ERR0002, "有效期参数错误");
                    }
                })
                .flatMap(randomVo -> {
                    // TODO 需要做查询对应的班级所有学生
                    Mono<List<Students>> list = classRoomService.findInteractiveStudents(randomVo.getCircleId(), random.getTeacherId())
                            .flatMap(studentsList -> MyAssert.isNull(studentsList, DefineCode.ERR0002, "不存在相关数据"))
                            .transform(teamRandomService::shuffle);
                    return list.flatMap(l -> teamRandomService.groupTeam(l, randomVo));
                })
                .filterWhen(groupTeamResp -> teamRedisService.deleteTeams(random.getGroupKey()))
                .filterWhen(groupTeamResp -> teamRedisService.saveRedisTeams(groupTeamResp.getTeamList(), random));
    }

    /**
     * 修改移动小组信息
     * @param changeVo
     * @return
     */
    public Mono<Boolean> teamChange(final ChangeTeamReq changeVo) {
        return Mono.just(changeVo.getTeamKey(changeVo.getRemoveTeamId()))
                .flatMap(key -> {
                    return  teamRedisService.getRedisStudents(key)
                            .filterWhen(j -> {
                                return teamChangeService.lessJoinTeamStudents(changeVo.getStudents(), j)
                                        .flatMap(students -> teamRedisService.putRedisStudents(key, students));
                            })
                            .flatMap(s -> teamRedisService.getRedisStudents(changeVo.getTeamKey(changeVo.getAddTeamId())))
                            .filterWhen(a -> {
                                return teamChangeService.moreJoinTeamStudents(changeVo.getStudents(), a)
                                        .flatMap(students -> teamRedisService.putRedisStudents(changeVo.getTeamKey(changeVo.getAddTeamId()), students));
                            });
                }).map(Objects::nonNull);
    }

    /**
     * 删除小组信息
     * @param req
     * @return
     */
    public Mono<Boolean> deleteTeam(final DeleteTeamReq req) {
        return Mono.zip(teamRedisService.findCircleId(req.getTeamKey()), teamRedisService.findClassId(req.getTeamKey()))
                .flatMap(t -> {
                    return stringRedisTemplate.opsForSet().remove(ChangeTeamReq.getGroupKey(t.getT1(), t.getT2()), req.getTeamId())
                            .flatMap(l -> reactiveHashOperations.delete(req.getTeamKey()));
                }).filterWhen(b -> MyAssert.isFalse(b, DefineCode.ERR0013, "删除失败"));
    }

    /**
     * 查询小组信息
     * @param req
     * @return
     */
    public Mono<List<TeamResp>> nowTeam(final CircleIdReq req) {
        return Mono.just(req.getGroupKey())
                .flatMap(key -> stringRedisTemplate.opsForSet().members(key).collectList())
                .flatMapMany(Flux::fromIterable)
                .flatMap(teamRandomService::findTeam)
                .collectList();
    }

    /**
     * 修改小组名字
     * @param req
     * @return
     */
    public Mono<Boolean> updateTeamName(final ChangeTeamNameReq req) {
        return reactiveHashOperations.put(req.getTeamKey(), "teamName", req.getTeamName())
                .flatMap(b -> MyAssert.isFalse(!b, DefineCode.ERR0013, "redis修改失败"));
    }

    /**
     * 添加新的小组
     * @param req
     * @return
     */
    public Mono<TeamResp> addTeam(final AddTeamReq req) {
        final String teamId = IdUtil.objectId();
        return Mono.just(req)
                .flatMap(r -> {
                    return stringRedisTemplate.opsForSet().add(req.getGroupKey(), teamId);
                })
                .filterWhen(b -> teamRedisService.saveRedisTeam(teamId, req.getTeamName(), req.getExpType(), req.getStudents(), req.getCircleId(), req.getClassId()))
                .flatMap(l -> Mono.just(new TeamResp(teamId, req.getTeamName())));
    }
}

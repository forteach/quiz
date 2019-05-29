package com.forteach.quiz.interaction.team.service;

import cn.hutool.core.util.IdUtil;
import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.interaction.execute.service.ClassRoom.ClassRoomService;
import com.forteach.quiz.interaction.team.domain.Team;
import com.forteach.quiz.interaction.team.web.req.*;
import com.forteach.quiz.interaction.team.web.resp.GroupTeamResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static com.forteach.quiz.interaction.team.constant.Dic.*;

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
    private final TeamMongoDBService teamMongoDBService;

    public TeamService(ReactiveStringRedisTemplate stringRedisTemplate,
                       ReactiveHashOperations<String, String, String> reactiveHashOperations,
                       TeamRandomService teamRandomService,
                       ClassRoomService classRoomService,
                       TeamMongoDBService teamMongoDBService,
                       TeamChangeService teamChangeService,
                       TeamRedisService teamRedisService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.teamRandomService = teamRandomService;
        this.teamChangeService = teamChangeService;
        this.classRoomService = classRoomService;
        this.teamMongoDBService = teamMongoDBService;
        this.teamRedisService = teamRedisService;
    }

    /**
     * 随机分组
     * TODO 需要修改根据班级和课程进行分组
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
                        final String key = CLASS_ROOM.concat(random.getClassId());
                        return stringRedisTemplate.opsForSet().size(key)
                                .flatMap(number -> MyAssert.isNull(number, DefineCode.ERR0002, "不存在相关数据"))
                                .flatMap(number -> teamRandomService.allotVerify(number, randomVo.getNumber()))
                                .flatMap(f -> MyAssert.isFalse(f, DefineCode.ERR0002, "班级人数不足,分组时最少需要每组两个人"));
                    } else {
                        return MyAssert.isNull(null, DefineCode.ERR0002, "有效期参数错误");
                    }
                })
                .flatMap(randomVo -> {
                    if (TEAM_FOREVER.equals(randomVo.getExpType())){
                        //   永久小组/班级小组
                        return classRoomService.findClassStudents(randomVo.getClassId())
                                .flatMap(l -> teamRandomService.groupTeamBuild(l, randomVo));
                    }else if (TEAM_TEMPORARILY.equals(randomVo.getExpType())){
                        //    临时小组/课堂小组
                        return classRoomService.findInteractiveStudents(randomVo.getCircleId(), randomVo.getTeacherId())
                                .flatMap(l -> teamRandomService.groupTeamBuild(l, randomVo));
                    }else {
                        return MyAssert.isNull(null, DefineCode.ERR0002, "有效期参数错误");
                    }
                })
                .filterWhen(groupTeamResp -> teamRedisService.deleteTeams(random.getGroupKey()))
                .filterWhen(groupTeamResp -> teamRedisService.saveRedisTeams(groupTeamResp.getTeamList(), random))
                .filterWhen(groupTeamResp -> teamMongoDBService.saveTeamList(groupTeamResp, random));
    }

    /**
     * 修改移动小组信息
     * @param changeVo
     * @return
     */
    public Mono<Boolean> teamChange(final ChangeTeamReq changeVo) {
        return Mono.just(changeVo.getTeamKey(changeVo.getRemoveTeamId()))
                .flatMap(key -> {
                    return teamRedisService.getRedisStudents(key)
                            .filterWhen(j -> {
                                //移除的小组减去相应的学生id
                                return teamChangeService.lessJoinTeamStudentStr(changeVo.getStudents(), j)
                                        .flatMap(students -> teamRedisService.putRedisStudents(key, students));
                            })
                            .flatMap(s -> teamRedisService.getRedisStudents(changeVo.getTeamKey(changeVo.getAddTeamId())))
                            .filterWhen(a -> {
                                //移入的小组添加进对应的学生id
                                return teamChangeService.moreJoinTeamStudentStr(changeVo.getStudents(), a)
                                        .flatMap(students -> teamRedisService.putRedisStudents(changeVo.getTeamKey(changeVo.getAddTeamId()), students));
                            })
                    .filterWhen(a -> teamMongoDBService.teamChange(changeVo));
                }).map(Objects::nonNull);
    }

    /**
     * 删除小组信息
     *
     * @param req
     * @return
     */
    public Mono<Boolean> deleteTeam(final DeleteTeamReq req) {
        return Mono.zip(teamRedisService.findCircleId(req.getTeamKey()), teamRedisService.findClassId(req.getTeamKey()))
                .flatMap(t -> {
                    return stringRedisTemplate.opsForSet().remove(ChangeTeamReq.getGroupKey(t.getT1(), t.getT2()), req.getTeamId())
                            .filterWhen(b -> teamMongoDBService.deleteTeam(req.getTeamKey(), req.getTeamId(), t.getT1(), t.getT2()))
                            .flatMap(l -> reactiveHashOperations.delete(req.getTeamKey()));
                }).filterWhen(b -> MyAssert.isFalse(b, DefineCode.ERR0013, "删除失败"));
    }

    /**
     * 查询小组信息
     *
     * @param req
     * @return
     */
    public Mono<List<Team>> nowTeam(final CircleIdReq req) {
        return stringRedisTemplate.hasKey(req.getGroupKey())
                .flatMap(b -> {
                    if (!b) {
                        //不存在没有记录需要去mongodb查询并将查询的结果保存到redis
                        return teamMongoDBService.findTeamList(req)
                                .filterWhen(teamRedisService::saveRedisTeamList)
                                .flatMap(baseTeam -> Mono.just(baseTeam.getTeamList()));
                    } else {
                        //存在记录直接走redis查询相应的记录即可
                        return stringRedisTemplate.opsForSet().members(req.getGroupKey()).collectList()
                                .flatMapMany(Flux::fromIterable)
                                .flatMap(teamRandomService::findTeam)
                                .collectList();
                    }
                });
    }

    /**
     * 修改小组名字
     *
     * @param req
     * @return
     */
    public Mono<Boolean> updateTeamName(final ChangeTeamNameReq req) {
        return reactiveHashOperations.put(req.getTeamKey(), "teamName", req.getTeamName())
                .flatMap(b -> MyAssert.isFalse(!b, DefineCode.ERR0013, "redis修改失败"))
                .flatMap(b -> teamMongoDBService.updateTeamName(req));
    }

    /**
     * 添加新的小组
     *
     * @param req
     * @return
     */
    public Mono<Team> addTeam(final AddTeamReq req) {
        final String teamId = IdUtil.objectId();
        return Mono.just(req)
                .flatMap(r -> {
                    return stringRedisTemplate.opsForSet().add(req.getGroupKey(), teamId);
                })
                .filterWhen(b -> teamRedisService.saveRedisTeam(teamId, req.getTeamName(), req.getExpType(), req.getStudents(), req.getCircleId(), req.getClassId()))
                .filterWhen(b -> teamMongoDBService.addTeam(teamId, req))
                .flatMap(l -> Mono.just(new Team(teamId, req.getTeamName())));
    }
}

package com.forteach.quiz.interaction.team.service;

import cn.hutool.core.util.IdUtil;
import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.interaction.execute.service.ClassRoomService;
import com.forteach.quiz.interaction.team.web.req.*;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.*;
import java.util.logging.Logger;
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
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final ReactiveRedisTemplate redisTemplate;
    private final ClassRoomService classRoomService;
    private final StudentsService studentsService;
    private final TeamRedisService teamRedisService;
    private final TeamRandomService teamRandomService;

    public TeamService(ReactiveStringRedisTemplate stringRedisTemplate,
                       ReactiveHashOperations<String, String, String> reactiveHashOperations,
                       ReactiveMongoTemplate reactiveMongoTemplate,
                       ReactiveRedisTemplate reactiveRedisTemplate,
                       TeamRandomService teamRandomService,
                       ClassRoomService classRoomService,
                       TeamRedisService teamRedisService,
                       StudentsService studentsService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.redisTemplate = reactiveRedisTemplate;
        this.teamRandomService = teamRandomService;
        this.classRoomService = classRoomService;
        this.studentsService = studentsService;
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
                .filterWhen(groupTeamResp -> this.saveRedisTeams(groupTeamResp.getTeamList(), random));
    }

    /**
     * 设置小组的有效期时间
     *
     * @param key
     * @param expType
     * @return
     */
    private Mono<Boolean> setExpire(final String key, final String expType) {
        return Mono.just(key).flatMap(k -> {
            if (TEAM_TEMPORARILY.equals(expType)) {
                return stringRedisTemplate.expire(key, Duration.ofDays(1))
                        .flatMap(b -> MyAssert.isFalse(b,DefineCode.ERR0013, "设置redis有效期失败"));
            } else if (TEAM_FOREVER.equals(expType)) {
                return stringRedisTemplate.expire(key, Duration.ofDays(366))
                        .flatMap(b -> MyAssert.isFalse(b,DefineCode.ERR0013, "设置redis有效期失败"));
            } else {
                return Mono.error(new Exception("分组的有效期不正确"));
            }
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
        return Mono.just(studentsAdd)
                .map(s -> new HashSet<>(Arrays.asList(s.split(","))))
                .flatMap(set -> {
                    set.addAll(Arrays.asList(studentsJoin.split(",")));
                    return Mono.just(new ArrayList<>(set));
                });
    }

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




    private Mono<Boolean> putRedisTeam(final TeamResp teamResp, final String expType, final String circleId, final String classId) {
        return Mono.just(studentListToStr(teamResp.getStudents()))
                .flatMap(students -> {
                    return saveRedisTeam(teamResp.getTeamId(), teamResp.getTeamName(), expType, students, circleId, classId);
                }).filterWhen(b -> this.setExpire(teamResp.getTeamsGroupKey(teamResp.getTeamId()), expType))
                .filterWhen(b -> MyAssert.isFalse(b, DefineCode.ERR0013, "保存小组信息到redis失败"));
    }

    Mono<Boolean> saveRedisTeam(final String teamId, final String teamName, final String expType,
                                final String students, final String circleId, final String classId) {
        Map<String, String> map = new HashMap<>(8);
        map.put("teamId", teamId);
        map.put("teamName", teamName);
        map.put("expType", expType);
        map.put("circleId", circleId);
        map.put("classId", classId);
        map.put("students", students);
        return stringRedisTemplate.opsForHash().putAll(ChangeTeamReq.concatTeamKey(teamId), map).map(Objects::nonNull);
    }

    public Mono<Boolean> updateTeamName(final ChangeTeamNameReq req) {
        return reactiveHashOperations.put(req.getTeamKey(), "teamName", req.getTeamName())
                .flatMap(b -> MyAssert.isFalse(!b, DefineCode.ERR0013, "redis修改失败"));
    }



    public Mono<TeamResp> addTeam(final AddTeamReq req) {
        final String teamId = IdUtil.objectId();
        return Mono.just(req)
                .flatMap(r -> {
                    return stringRedisTemplate.opsForSet().add(req.getGroupKey(), teamId);
                })
                .log(Logger.GLOBAL_LOGGER_NAME)
                .filterWhen(b -> saveRedisTeam(teamId, req.getTeamName(), req.getExpType(), req.getStudents(), req.getCircleId(), req.getClassId()).log())
                .flatMap(l -> Mono.just(new TeamResp(teamId, req.getTeamName())));
    }

    private String studentListToStr(final List<Students> studentsList) {
        StringBuffer str = new StringBuffer();
        studentsList.forEach(s -> {
            str.append(s.getId()).append(",");
        });
        return str.toString();
    }

    public Mono<Boolean> teamChange(final ChangeTeamReq changeVo) {
        return Mono.just(changeVo.getTeamKey(changeVo.getRemoveTeamId()))
                .flatMap(key -> {
                    return teamRedisService.redisHasKey(key)
                            .flatMap(b -> teamRedisService.getRedisStudents(key))
                            .filterWhen(s -> {
                                return this.lessJoinTeamStudents(changeVo.getStudents(), s)
                                        .flatMap(students -> teamRedisService.putRedisStudents(key, students));
                            })
                            .filterWhen(s -> {
                                return this.moreJoinTeamStudents(changeVo.getStudents(), s)
                                        .flatMap(students -> teamRedisService.putRedisStudents(key, students));
                            });
                }).map(Objects::nonNull);
    }

    public Mono<Boolean> deleteTeam(final DeleteTeamReq req) {
        return Mono.zip(teamRedisService.findCircleId(req.getTeamKey()), teamRedisService.findClassId(req.getTeamKey()))
                .flatMap(t -> {
                    return stringRedisTemplate.opsForSet().remove(ChangeTeamReq.getGroupKey(t.getT1(), t.getT2()), req.getTeamId())
                            .flatMap(l -> reactiveHashOperations.delete(req.getTeamKey()));
                }).filterWhen(b -> MyAssert.isFalse(b, DefineCode.ERR0013, "删除失败"));
    }

    public Mono<List<TeamResp>> nowTeam(final CircleIdReq req) {
        return Mono.just(req.getGroupKey())
                .flatMap(key -> stringRedisTemplate.opsForSet().members(key).collectList())
                .flatMapMany(Flux::fromIterable)
                .flatMap(teamRandomService::findTeam)
                .collectList();
    }
    Mono<Boolean> saveRedisTeams(final List<TeamResp> teamList, final GroupRandomReq randomVo) {
        Mono<List<Long>> addRedisGroup = Mono.just(teamList)
                .flatMapMany(Flux::fromIterable)
                .flatMap(teamResp -> {
                    String teamId = teamResp.getTeamId();
                    return stringRedisTemplate.opsForSet().add(randomVo.getGroupKey(), teamId);
                }).collectList();
        Mono<List<Boolean>> teamRedis = Mono.just(teamList)
                .flatMapMany(Flux::fromIterable)
                .flatMap(teamResp -> {
                    return putRedisTeam(teamResp, randomVo.getExpType(), randomVo.getCircleId(), randomVo.getClassId());
                }).collectList();
        return Mono.zip(addRedisGroup, teamRedis)
                .flatMap(g -> {
                    return setExpire(randomVo.getGroupKey(), randomVo.getExpType());
                });
    }
}

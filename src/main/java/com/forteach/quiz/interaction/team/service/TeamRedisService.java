package com.forteach.quiz.interaction.team.service;

import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.interaction.team.domain.BaseTeam;
import com.forteach.quiz.interaction.team.domain.Team;
import com.forteach.quiz.interaction.team.domain.TeamCircle;
import com.forteach.quiz.interaction.team.domain.TeamCourse;
import com.forteach.quiz.interaction.team.web.req.ChangeTeamReq;
import com.forteach.quiz.interaction.team.web.req.GroupRandomReq;
import com.forteach.quiz.service.StudentsService;
import com.forteach.quiz.web.pojo.Students;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;

import static com.forteach.quiz.interaction.team.constant.Dic.TEAM_FOREVER;
import static com.forteach.quiz.interaction.team.constant.Dic.TEAM_TEMPORARILY;

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
    private final ReactiveStringRedisTemplate stringRedisTemplate;
    private final StudentsService studentsService;

    public TeamRedisService(ReactiveHashOperations<String, String, String> reactiveHashOperations,
                            StudentsService studentsService,
                            ReactiveStringRedisTemplate stringRedisTemplate) {
        this.reactiveHashOperations = reactiveHashOperations;
        this.stringRedisTemplate = stringRedisTemplate;
        this.studentsService = studentsService;
    }

    /**
     * 根据 key 获取 分组的学生id ‘,’分割
     *
     * @param key
     * @return
     */
    Mono<String> getRedisStudents(final String key) {
        return reactiveHashOperations.get(key, "students")
                .flatMap(s -> MyAssert.isNull(s, DefineCode.ERR0002, "不存在相关记录"));
    }

    /**
     * 判断是否存redis 在相关信息
     *
     * @param key
     * @return
     */
    Mono<Boolean> redisHasKey(final String key) {
        return stringRedisTemplate.hasKey(key)
                .filterWhen(b -> MyAssert.isFalse(b, DefineCode.ERR0002, "不存在相关记录"));
    }

    /**
     * 获取课程/课堂id信息
     *
     * @param key
     * @return
     */
    Mono<String> findCircleId(final String key) {
        return reactiveHashOperations.get(key, "circleId")
                .flatMap(c -> MyAssert.isNull(c, DefineCode.ERR0002, "课程(课堂)信息不存在"));
    }

    /**
     * 获取班级信息
     *
     * @param key
     * @return
     */
    Mono<String> findClassId(final String key) {
        return reactiveHashOperations.get(key, "classId")
                .flatMap(c -> MyAssert.isNull(c, DefineCode.ERR0002, "班级信息不存在"));
    }

    /**
     * 修改加入课堂的学生id信息
     *
     * @param key
     * @param students
     * @return
     */
    Mono<Boolean> putRedisStudents(final String key, final List<String> students) {
        return reactiveHashOperations.put(key, "students", this.studentsListToStr(students))
                .flatMap(b -> MyAssert.isFalse(!b, DefineCode.ERR0013, "redis修改失败"));
    }

    /**
     * 获取redis hash 中对应的值
     *
     * @param key
     * @param value
     * @return
     */
    Mono<String> findHashString(final String key, final String value) {
        return reactiveHashOperations.get(key, value)
                .flatMap(s -> MyAssert.isNull(s, DefineCode.ERR0012, "要查询的值在redis 不存在"));
    }

    /**
     * 删除redis 记录的学生信息
     *
     * @param key
     * @return
     */
    Mono<Boolean> deleteTeams(final String key) {
        return stringRedisTemplate.opsForSet()
                .members(key)
                .filter(Objects::nonNull)
                .collectList()
                .filter(list -> list != null && list.size() > 0)
                .flatMapMany(Flux::fromIterable)
                .flatMap(teamId -> reactiveHashOperations.delete(ChangeTeamReq.concatTeamKey(teamId)))
                .collectList()
                .flatMap(l -> {
                    return reactiveHashOperations.delete(key);
                })
                .flatMap(f -> Mono.just(true));
    }

    /**
     * redis 保存小组的详细信息
     *
     * @param teamId
     * @param teamName
     * @param expType
     * @param students
     * @param circleId
     * @param classId
     * @return
     */
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

    /**
     * 保存学生信息到redis
     *
     * @param teamResp
     * @param expType
     * @param circleId
     * @param classId
     * @return
     */
    private Mono<Boolean> putRedisTeam(final Team teamResp, final String expType, final String circleId, final String classId) {
        return Mono.just(studentListToStr(teamResp.getStudents()))
                .flatMap(students -> {
                    return saveRedisTeam(teamResp.getTeamId(), teamResp.getTeamName(), expType, students, circleId, classId);
                }).filterWhen(b -> this.setExpire(teamResp.getTeamsGroupKey(teamResp.getTeamId()), expType))
                .filterWhen(b -> MyAssert.isFalse(b, DefineCode.ERR0013, "保存小组信息到redis失败"));
    }

    /**
     * 添加小组信息到redis列表
     *
     * @param teamList
     * @param randomVo
     * @return
     */
    Mono<Boolean> saveRedisTeams(final List<Team> teamList, final GroupRandomReq randomVo) {
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
                return stringRedisTemplate.expire(key, Duration.ofHours(4))
                        .flatMap(b -> MyAssert.isFalse(b, DefineCode.ERR0013, "设置redis有效期失败"));
            } else if (TEAM_FOREVER.equals(expType)) {
                return stringRedisTemplate.expire(key, Duration.ofDays(5))
                        .flatMap(b -> MyAssert.isFalse(b, DefineCode.ERR0013, "设置redis有效期失败"));
            } else {
                return Mono.error(new Exception("分组的有效期不正确"));
            }
        });
    }


    /**
     * List<String> listStr ==> 123,1234,
     *
     * @param students
     * @return
     */
    String studentsListToStr(final List<String> students) {
        return String.join(",", students.toArray(new String[students.size()]));
    }

    List<String> findListString(final String string) {
        return Arrays.asList(string.split(","));
    }

    /**
     * 123,1234, ==> List<Students> listStudents
     *
     * @param students
     * @return
     */
    Mono<List<Students>> findStudentsListByStr(final String students) {
        return studentsService.exchangeStudents(Arrays.asList(students.split(",")));
    }


    /**
     * 将学生列表信息转换为学生字符串信息
     *
     * @param studentsList
     * @return
     */
    private String studentListToStr(final List<Students> studentsList) {
        StringBuffer str = new StringBuffer();
        studentsList.forEach(s -> {
            str.append(s.getId()).append(",");
        });
        return str.toString();
    }

    /**
     * 将从mongodb中查询道德小组信息保存到redis
     *
     * @param baseTeam
     * @return
     */
    public Mono<Boolean> saveRedisTeamList(BaseTeam baseTeam) {
        if (baseTeam instanceof TeamCircle) {
            return saveRedisTeams(baseTeam.getTeamList(),
                    new GroupRandomReq(((TeamCircle) baseTeam).getCircleId(),
                            baseTeam.getClassId(), baseTeam.getExpType(), baseTeam.getTeacherId()));
        } else if (baseTeam instanceof TeamCourse) {
            return saveRedisTeams(baseTeam.getTeamList(),
                    new GroupRandomReq(((TeamCourse) baseTeam).getCourseId(),
                            baseTeam.getClassId(), baseTeam.getExpType(), baseTeam.getTeacherId()));
        }
        return MyAssert.isNull(null, DefineCode.ERR0013, "redis操作失败");
    }
}

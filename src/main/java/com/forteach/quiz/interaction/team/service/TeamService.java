package com.forteach.quiz.interaction.team.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.interaction.execute.service.ClassRoomService;
import com.forteach.quiz.interaction.team.web.req.GroupRandomReq;
import com.forteach.quiz.interaction.team.web.req.PickTeamReq;
import com.forteach.quiz.interaction.team.web.resp.TeamResp;
import com.forteach.quiz.service.StudentsService;
import com.forteach.quiz.web.pojo.Students;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
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
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final ReactiveRedisTemplate redisTemplate;
    private final ClassRoomService classRoomService;
    private final StudentsService studentsService;

    public TeamService(ReactiveStringRedisTemplate stringRedisTemplate,
                       ReactiveHashOperations<String, String, String> reactiveHashOperations,
                       ReactiveMongoTemplate reactiveMongoTemplate,
                       ReactiveRedisTemplate reactiveRedisTemplate,
                       ClassRoomService classRoomService,
                       StudentsService studentsService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.redisTemplate = reactiveRedisTemplate;
        this.classRoomService = classRoomService;
        this.studentsService = studentsService;
    }

    Mono<Boolean> checkJoinStudents(final List<String> students, final String circleId, final String teacherId){
        //待校验的学生
//        Mono<List<String>> checkStudent = this.changeStringStudent(students);
        Mono<List<String>> joninCircle = classRoomService.findInteractiveStudentsID(circleId, teacherId);

//        joninCircle.flatMap(j -> {
//            students.forEach(s -> {
//                if (!j.contains(s)){
////                    return Mono.just(false);
//                }
//            });
//        });
        return Mono.just(true);
    }



    /**
     * 构建新的选人小组
     * @param req
     * @param studentsList
     * @return
     */
    Mono<TeamResp> builderTeam(final PickTeamReq req, final List<Students> studentsList){
        final String teamId = IdUtil.objectId();
        final String key = req.getTeamRedisKey(teamId);
        Map<String, String> map = new HashMap<>(8);
        map.put("students", req.getStudents());
        map.put("teacherId", req.getTeacherId());
        map.put("expType", req.getExpType());
        map.put("teamName", req.getTeamName());
        map.put("circleId", req.getCircleId());
        map.put("teamId", teamId);
        return reactiveHashOperations.putAll(key, map)
                .filterWhen(f -> {
                    if (f) {
                        return this.setExpire(key, req.getExpType());
                    } else {
                        return MyAssert.isFalse(!f, DefineCode.ERR0013, "保存redis失败");
                    }
                })
                .flatMap(f -> Mono.just(new TeamResp(teamId, req.getTeamName(), studentsList)));
    }

    /**
     * 设置小组的有效期时间
     * @param key
     * @param expType
     * @return
     */
    private Mono<Boolean> setExpire(final String key, final String expType){
        return Mono.just(key).flatMap(k -> {
            if (TEAM_TEMPORARILY.equals(expType)){
                return stringRedisTemplate.expire(key, Duration.ofDays(1));
            }else if (TEAM_FOREVER.equals(expType)){
                return stringRedisTemplate.expire(key, Duration.ofDays(366));
            }else {
                return Mono.error(new Exception("分组的有效期不正确"));
            }
        });
    }

    /**
     * 根据分割的学生信息转换为学生对象信息
     * @param students
     * @return
     */
    public Mono<List<Students>> changeStudents(final String students){
        return Flux.fromIterable(Arrays.asList(students.split(","))).flatMap(studentsService::findStudentsBrief).collectList();
    }

    /**
     * 将学生信息分割字符串转换集合
     * @param students
     * @return
     */
    public Mono<List<String>> changeStringStudent(final String students){
        return Mono.just(Arrays.asList(students.split(",")));
    }

    /**
     * list --> strings
     * @param students
     * @return
     */
    String studentsListToStr(final List<String> students){
        return String.join(",", students.toArray(new String[students.size()]));
    }


    Mono<Boolean> updateRedisTeamName(final String key, final String teamName) {
        return Mono.just(key)
                .flatMap(k -> {
                    if (StrUtil.isNotBlank(teamName)){
                        return reactiveHashOperations.put(key, "teamName", teamName);
                    }
                    return Mono.just(true);
                });
    }

    /**
     * 添加学生
     * @param studentsAdd 新添加的学生id字符串
     * @param studentsJoin 原来已经加入的学生id字符串
     * @return 全部加入的学生id字符串
     */
    Mono<List<String>> moreJoinTeamStudents(final String studentsAdd, final String studentsJoin){
        return Mono.just(studentsAdd)
                .map(s -> new HashSet<>(Arrays.asList(s.split(","))))
                .flatMap(set -> {
                    set.addAll(Arrays.asList(studentsJoin.split(",")));
                    return Mono.just(new ArrayList<>(set));
                });
    }
    /**
     * 移除已经加入的学生
     * @param studentsLess 需要移除的学生
     * @param stringJoin 原来加入的学生
     * @return 移除后的学生列表
     */
    Mono<List<String>> lessJoinTeamStudents(final String studentsLess, final String stringJoin){
        return Mono.just(studentsLess)
                .map(s -> Arrays.asList(s.split(",")))
                .flatMap(strings -> {
                    List<String> list = Arrays.asList(stringJoin.split(","));
                    List<String> stringList = new ArrayList<>();
                    list.forEach(s -> {
                        strings.forEach(ss -> {
                            if (!s.equals(ss)){
                                stringList.add(s);
                            }
                        });
                    });
                    return Mono.just(stringList);
                });
    }

    Mono<Boolean> saveRedisPutStudents(final String teamKey, final String students){
        return reactiveHashOperations.put(teamKey, "students", students);
    }

    public Mono<TeamResp> updateData(final PickTeamReq req, final List<String> list, final String key) {
        return Mono.just(list).filterWhen(stringList -> {
            return this.saveRedisPutStudents(key, this.studentsListToStr(stringList))
                    .flatMap(f -> MyAssert.isFalse(!f, DefineCode.ERR0013, "更新redis失败"));
        })
                .filterWhen(s -> {
                    return this.updateRedisTeamName(key, req.getTeamName())
                            .flatMap(f -> MyAssert.isFalse(!f, DefineCode.ERR0013, "更新redis失败"));
                })
                .flatMap(studentsService::exchangeStudents)
                .map(studentsList -> {
                    return TeamResp.builder()
                            .teamId(req.getTeamId())
                            .students(studentsList)
                            .teamName(req.getTeamName())
                            .build();
                });
    }


    public Mono<Boolean> saveRedisTeams(final List<TeamResp> teamList, final GroupRandomReq randomVo) {
        return Mono.just(teamList)
        .flatMap(teamResps -> {
            Set<String> stringSet = new LinkedHashSet<>();
            teamResps.stream().forEach(teamResp -> {
                stringSet.add(teamResp.getTeamId());
                this.saveRedisTeam(teamResp, randomVo.getExpType(), randomVo.getCircleId(), randomVo.getClassId());
            });
            return redisTemplate.opsForZSet().addAll(randomVo.getGroupKey(), stringSet);
        });
    }

    protected String studentListToStr(List<Students> studentsList) {
        StringBuffer str = new StringBuffer();
        studentsList.stream()
                .forEach(s -> {
                    str.append(s.getId()).append(",");
                });
        return str.toString();
    }

    private Mono<Boolean> saveRedisTeam(final TeamResp teamResp, final String expType, final String circleId, final String classId) {
        return Mono.just(teamResp.getTeamsGroupKey(teamResp.getTeamId()))
                .flatMap(key -> {
                    Map<String, String> map = new HashMap<>(8);
                    map.put("teamId", teamResp.getTeamId());
                    map.put("teamName", teamResp.getTeamName());
                    map.put("expType", expType);
                    map.put("circleId", circleId);
                    map.put("classId", classId);
                    map.put("students", this.studentListToStr(teamResp.getStudents()));
                    return redisTemplate.opsForHash().putAll(key, map);
                }).filterWhen(b -> this.setExpire(teamResp.getTeamsGroupKey(teamResp.getTeamId()), expType));
    }
}

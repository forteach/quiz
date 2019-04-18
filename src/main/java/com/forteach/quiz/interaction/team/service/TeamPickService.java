package com.forteach.quiz.interaction.team.service;

import cn.hutool.core.util.StrUtil;
import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.interaction.execute.service.ClassRoomService;
import com.forteach.quiz.interaction.team.web.req.PickTeamReq;
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
import java.util.List;
import java.util.Objects;
import static com.forteach.quiz.common.Dic.TEAM_STRUDENT_MORE;
import static com.forteach.quiz.common.Dic.TEAM_STUDENT_LESS;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-4-11 13:55
 * @version: 1.0
 * @description:
 */
@Service
@Slf4j
public class TeamPickService {
    private final ReactiveStringRedisTemplate stringRedisTemplate;
    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final ReactiveRedisTemplate redisTemplate;
    private final ClassRoomService classRoomService;
    private final StudentsService studentsService;
    private final TeamService teamService;

    public TeamPickService(ReactiveStringRedisTemplate stringRedisTemplate,
                       ReactiveHashOperations<String, String, String> reactiveHashOperations,
                       ReactiveMongoTemplate reactiveMongoTemplate,
                       ReactiveRedisTemplate reactiveRedisTemplate,
                       ClassRoomService classRoomService,
                       TeamService teamService,
                       StudentsService studentsService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.redisTemplate = reactiveRedisTemplate;
        this.classRoomService = classRoomService;
        this.studentsService = studentsService;
        this.teamService = teamService;
    }

    public Mono<TeamResp> pickTeam(final PickTeamReq req) {
        return Mono.just(Arrays.asList(req.getStudents().split(",")))
                //校验是否是加入课堂的学生
                .filterWhen(s -> teamService.checkJoinStudents(s, req.getCircleId(), req.getTeacherId()))
                .flatMap(studentsService::exchangeStudents)
                .flatMap(s -> {
                    return this.changeTeam(req, s);
                });
    }

    Mono<TeamResp> changeTeam(final PickTeamReq req, final List<Students> studentsList){
        if (StrUtil.isBlank(req.getTeamId())){
            return teamService.builderTeam(req, studentsList);
        }else if (TEAM_STRUDENT_MORE.equals(req.getMoreOrLess())){
            //已经存在相关小组进行小组人员变更
            //追加小组成员
            final String key = req.getTeamRedisKey(req.getTeamId());
            return reactiveHashOperations.get(key, "students")
                    .filter(Objects::nonNull)
                    .flatMap(s -> teamService.moreJoinTeamStudents(req.getStudents(), s))
                    .flatMap(stringList -> teamService.updateData(req, stringList, key));
        }else if (TEAM_STUDENT_LESS.equals(req.getMoreOrLess())){
            //追加小组成员
            final String key = req.getTeamRedisKey(req.getTeamId());
            return reactiveHashOperations.get(key, "students")
                    .filter(Objects::nonNull)
                    .flatMap(s -> teamService.lessJoinTeamStudents(req.getStudents(), s))
                    .flatMap(stringList -> teamService.updateData(req, stringList, key));
        }else {
            return MyAssert.isNull(null, DefineCode.ERR0002, "增加或减少 参数错误");
        }
    }

}


package com.forteach.quiz.interaction.team.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.interaction.team.domain.BaseTeam;
import com.forteach.quiz.interaction.team.domain.Team;
import com.forteach.quiz.interaction.team.domain.TeamCircle;
import com.forteach.quiz.interaction.team.domain.TeamCourse;
import com.forteach.quiz.interaction.team.web.req.*;
import com.forteach.quiz.interaction.team.web.resp.GroupTeamResp;
import com.forteach.quiz.service.StudentsService;
import com.forteach.quiz.web.pojo.Students;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.forteach.quiz.common.Dic.MONGDB_ID;
import static com.forteach.quiz.interaction.team.constant.Dic.TEAM_FOREVER;
import static com.forteach.quiz.interaction.team.constant.Dic.TEAM_TEMPORARILY;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-4-22 17:31
 * @version: 1.0
 * @description:
 */
@Service
public class TeamMongoDBService {

    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final TeamRedisService teamRedisService;
    private final StudentsService studentsService;

    public TeamMongoDBService(ReactiveMongoTemplate reactiveMongoTemplate,
                              StudentsService studentsService,
                              TeamRedisService teamRedisService) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.teamRedisService = teamRedisService;
        this.studentsService = studentsService;
    }

    Mono<Boolean> saveTeamList(final GroupTeamResp groupTeamResp, final GroupRandomReq random) {
        return Mono.just(random)
                .flatMap(r -> {
                    Query query = Query.query(Criteria.where("circleId")
                            .is(r.getCircleId()).and("classId").is(r.getClassId()));
                    Update update = new Update();
                    update.set("teacherId", r.getTeacherId());
                    update.set("expType", r.getExpType());
                    update.set("teamList", groupTeamResp.getTeamList());
                    update.set("createTime", DateUtil.formatDateTime(new Date()));
                    update.set("uDate", DateUtil.formatDateTime(new Date()));
                    update.set("classId", r.getClassId());
                    update.set("loseTime", DateUtil.formatDateTime(DateUtil.offsetHour(new Date(), 4)));
                    if (TEAM_TEMPORARILY.equals(r.getExpType())) {
                        //是临时的小组
                        update.set("circleId", r.getCircleId());
                        return reactiveMongoTemplate.upsert(query, update, TeamCircle.class, "teamCircle")
                                .flatMap(updateResult -> {
                                    return MyAssert.isFalse(updateResult.wasAcknowledged(), DefineCode.ERR0012, "保存mongodb失败");
                                })
                                .map(Objects::nonNull);
                    } else if (TEAM_FOREVER.equals(r.getExpType())) {
                        //是永久课程小组
                        update.set("courseId", r.getCircleId());
                        return reactiveMongoTemplate.upsert(query, update, TeamCourse.class)
                                .filter(Objects::nonNull)
                                .flatMap(updateResult -> {
                                    return MyAssert.isFalse(updateResult.wasAcknowledged(), DefineCode.ERR0012, "保存mongodb失败");
                                }).map(Objects::nonNull);
                    }
                    return MyAssert.isNull(null, DefineCode.ERR0002, "分组的有效期参数不正确");
                });
    }

    public Mono<Boolean> teamChange(final ChangeTeamReq changeVo, final String circleId, final String classId) {
        Query query = Query.query(Criteria.where(MONGDB_ID)
                .is(circleId).and("classId").is(classId));
        Update update = new Update();
        return Mono.just(true);
    }

    Mono<Boolean> updateTeamName(final ChangeTeamNameReq req) {
        final String key = req.getTeamKey();
        Mono<String> circleId = teamRedisService.findHashString(key, "circleId");
        Mono<String> classId = teamRedisService.findHashString(key, "classId");
        Mono<String> expType = teamRedisService.findHashString(key, "expType");
        return Mono.zip(circleId, classId).zipWith(expType).flatMap(e -> {
            Query query = Query.query(Criteria.where("circleId").is(e.getT1().getT1())
                    .and("classId").is(e.getT1().getT2()).and("teamList.teamId").is(req.getTeamId()));
            Update update = new Update();
            update.push("teamList.teamName", req.getTeamName());
            update.push("uDate", DateUtil.formatDateTime(new Date()));

            if (TEAM_TEMPORARILY.equals(e.getT2())) {
                TeamCircle teamCircle = new TeamCircle();

                return reactiveMongoTemplate
                        .updateFirst(query, update, TeamCircle.class)
                        .flatMap(updateResult -> MyAssert.isFalse(updateResult.wasAcknowledged(), DefineCode.ERR0012, "修改失败"))
                        .map(Objects::nonNull);
            } else if (TEAM_FOREVER.equals(e.getT2())) {
                return reactiveMongoTemplate
                        .upsert(query, update, TeamCourse.class)
                        .flatMap(updateResult -> MyAssert.isFalse(updateResult.wasAcknowledged(), DefineCode.ERR0012, "修改失败"))
                        .map(Objects::nonNull);
            }
            return MyAssert.isNull(null, DefineCode.ERR0012, "参数不正确");
        });
    }

    Mono<Boolean> addTeam(final String teamId, final AddTeamReq req) {
        Mono<List<Students>> students = studentsService.exchangeStudents(Arrays.asList(req.getStudents().split(",")));
        if (TEAM_TEMPORARILY.equals(req.getExpType())){
            return students.flatMap(studentsList -> {
                    Query query = Query.query(Criteria.where("circleId").is(req.getCircleId()).and("classId").is(req.getClassId()));
                    Update update = Update.update("uDate", DateUtil.formatDateTime(new Date()));
                    update.addToSet("teamList", new Team(teamId, req.getTeamName(), studentsList));
                    return reactiveMongoTemplate.upsert(query, update, TeamCircle.class)
                            .flatMap(updateResult -> MyAssert.isFalse(updateResult.wasAcknowledged(), DefineCode.ERR0012, "添加失败"));
                    });
        }else if (TEAM_FOREVER.equals(req.getExpType())){
            return students.flatMap(studentsList -> {
                Query query = Query.query(Criteria.where("courseId").is(req.getCircleId()).and("classId").is(req.getClassId()));
                Update update = Update.update("uDate", DateUtil.formatDateTime(new Date()));
                update.addToSet("teamList", new Team(teamId, req.getTeamName(), studentsList));
                return reactiveMongoTemplate.upsert(query, update, TeamCourse.class)
                        .flatMap(updateResult -> MyAssert.isFalse(updateResult.wasAcknowledged(), DefineCode.ERR0012, "添加失败"));
            });
        }
        return MyAssert.isNull(null, DefineCode.ERR0002, "参数不正确");
    }

    Mono<Boolean> deleteTeam(final String key, final String teamId, final String circleId, final String classId) {
        return teamRedisService.findHashString(key, "expType")
                .flatMap(e -> {
                    if (TEAM_TEMPORARILY.equals(e)){
                        Query query = Query.query(Criteria.where("circleId").is(circleId).and("classId").is(classId));
                        Update update = Update.update("uDate", DateUtil.formatDateTime(new Date()));
                        update.pull("teamList", Query.query(Criteria.where("teamId").is(teamId)));
                        return reactiveMongoTemplate.updateFirst(query, update, TeamCircle.class)
                                .flatMap(teamCircle -> MyAssert.isNull(teamCircle, DefineCode.ERR0012, "mongodb不为空"));
                    }else if (TEAM_FOREVER.equals(e)){
                        Query query = Query.query(Criteria.where("courseId").is(circleId).and("classId").is(classId));
                        Update update = Update.update("uDate", DateUtil.formatDateTime(new Date()));
                        update.pull("teamList", Query.query(Criteria.where("teamId").is(teamId)));
                        return reactiveMongoTemplate.updateMulti(query, update, TeamCourse.class)
                                .flatMap(teamCircle -> MyAssert.isNull(teamCircle, DefineCode.ERR0012, "mongodb不为空"));
                    }
                    return MyAssert.isNull(null, DefineCode.ERR0012, "参数不正确");
                }).map(Objects::nonNull);
    }

    Mono<? extends BaseTeam> findTeamList(final CircleIdReq req) {
        if (StrUtil.isBlank(req.getClassId())){
            return reactiveMongoTemplate.findOne(Query.query(Criteria.where("circleId").is(req.getCircleId())), TeamCircle.class);
        }else {
            return reactiveMongoTemplate.findOne(Query.query(Criteria.where("courseId").is(req.getCircleId()).and("classId").is(req.getClassId())), TeamCourse.class);
        }
    }
}

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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Objects;

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

    /**
     * 将随机的小组信息覆盖原来生成的小组信息
     *
     * @param groupTeamResp
     * @param random
     * @return
     */
    Mono<Boolean> saveTeamList(final GroupTeamResp groupTeamResp, final GroupRandomReq random) {
        return Mono.just(random)
                .flatMap(r -> {
                    Update update = Update.update("uDate", DateUtil.formatDateTime(new Date()));
                    update.set("teacherId", r.getTeacherId());
                    update.set("expType", r.getExpType());
                    update.set("teamList", groupTeamResp.getTeamList());
                    update.set("createTime", DateUtil.formatDateTime(new Date()));
                    update.set("classId", r.getClassId());
                    if (TEAM_TEMPORARILY.equals(r.getExpType())) {
                        //是临时的小组
                        Query query = Query.query(Criteria.where("circleId")
                                .is(r.getCircleId()).and("classId").is(r.getClassId()));
                        update.set("circleId", r.getCircleId());
                        update.set("loseTime", DateUtil.formatDateTime(DateUtil.offsetHour(new Date(), 4)));
                        return reactiveMongoTemplate.upsert(query, update, TeamCircle.class, "teamCircle")
                                .flatMap(updateResult -> {
                                    return MyAssert.isFalse(updateResult.wasAcknowledged(), DefineCode.ERR0012, "保存mongodb失败");
                                })
                                .map(Objects::nonNull);
                    } else if (TEAM_FOREVER.equals(r.getExpType())) {
                        //是永久课程小组
                        Query query = Query.query(Criteria.where("courseId")
                                .is(r.getCircleId()).and("classId").is(r.getClassId()));
                        update.set("courseId", r.getCircleId());
                        update.set("loseTime", DateUtil.formatDateTime(DateUtil.offsetMonth(new Date(), 12)));
                        return reactiveMongoTemplate.upsert(query, update, TeamCourse.class)
                                .filter(Objects::nonNull)
                                .flatMap(updateResult -> {
                                    return MyAssert.isFalse(updateResult.wasAcknowledged(), DefineCode.ERR0012, "保存mongodb失败");
                                }).map(Objects::nonNull);
                    }
                    return MyAssert.isNull(null, DefineCode.ERR0002, "分组的有效期参数不正确");
                });
    }

    /* ----------移动小组成员　开始方法　----------*/

    Mono<Boolean> teamChange(final ChangeTeamReq changeVo) {
        final String key = changeVo.getTeamKey(changeVo.getRemoveTeamId());
        Mono<String> circleId = teamRedisService.findHashString(key, "circleId");
        Mono<String> classId = teamRedisService.findHashString(key, "classId");
        Mono<String> expType = teamRedisService.findHashString(key, "expType");
        return Mono.zip(circleId, classId).zipWith(expType)
                .flatMap(e -> {
                    if (TEAM_TEMPORARILY.equals(e.getT2())) {
                        //是临时时小组(课堂小组)
                        return updateChangeTeamCircle(e.getT1().getT1(), e.getT1().getT2(), changeVo.getRemoveTeamId(), changeVo.getAddTeamId(), changeVo.getStudents());
                    } else if (TEAM_FOREVER.equals(e.getT2())) {
                        //是课程小组(永久小组)
                        return updateChangeTeamCourse(e.getT1().getT1(), e.getT1().getT2(), changeVo.getRemoveTeamId(), changeVo.getAddTeamId(), changeVo.getStudents());
                    }
                    return MyAssert.isNull(null, DefineCode.ERR0012, "参数不正确");
                });
    }

    /**
     * 修改临时小组mongodb数据
     *
     * @param circleId
     * @param classId
     * @param removeTeamId
     * @param addTeamId
     * @param students
     * @return
     */
    private Mono<Boolean> updateChangeTeamCircle(final String circleId, final String classId,
                                                 final String removeTeamId, final String addTeamId,
                                                 final String students) {

        Query query1 = Query.query(Criteria.where("circleId").is(circleId)
                .and("classId").is(classId).and("teamList.teamId").is(removeTeamId));
        Update update1 = Update.update("uDate", DateUtil.formatDateTime(new Date()));

        teamRedisService.findListString(students)
                .forEach(s -> {
                    update1.pull("teamList.$.students", Query.query(Criteria.where("_id").is(s)));
                });

        Query query2 = Query.query(Criteria.where("circleId").is(circleId)
                .and("classId").is(classId).and("teamList.teamId").is(addTeamId));
        Update update2 = Update.update("uDate", DateUtil.formatDateTime(new Date()));

        return teamRedisService.findStudentsListByStr(students)
                .flatMapMany(Flux::fromIterable)
                .flatMap(s -> {
                    //修改要移出的小组学生信息
                    return updateResultMono(query1, update1, TeamCircle.class)
                            .flatMap(b -> {
                                update2.addToSet("teamList.$.students", s);
                                //添加移除的学生到移入的学生处
                                return updateResultMono(query2, update2, TeamCircle.class);
                            });
                }).collectList()
                .map(Objects::nonNull);
    }

    /**
     * 操作mongodb　更新记录
     *
     * @param query
     * @param update
     * @param entityClass
     * @return
     */
    Mono<Boolean> updateResultMono(Query query, Update update, Class<?> entityClass) {
        return reactiveMongoTemplate.updateMulti(query, update, entityClass)
                .flatMap(updateResult -> MyAssert.isFalse(updateResult.wasAcknowledged(), DefineCode.ERR0012, "修改失败"));
    }

    private Mono<Boolean> updateChangeTeamCourse(final String circleId, final String classId,
                                                 final String removeTeamId, final String addTeamId, final String students) {

        Query query1 = Query.query(Criteria.where("courseId").is(circleId)
                .and("classId").is(classId).and("teamList.teamId").is(removeTeamId));
        Update update1 = Update.update("uDate", DateUtil.formatDateTime(new Date()));

        teamRedisService.findListString(students)
                .forEach(s -> {
                    update1.pull("teamList.$.students", Query.query(Criteria.where("_id").is(s)));
                });

        Query query2 = Query.query(Criteria.where("courseId").is(circleId)
                .and("classId").is(classId).and("teamList.teamId").is(addTeamId));
        Update update2 = Update.update("uDate", DateUtil.formatDateTime(new Date()));

        return teamRedisService.findStudentsListByStr(students)
                .flatMapMany(Flux::fromIterable)
                .flatMap(s -> {
                    return updateResultMono(query1, update1, TeamCourse.class)
                            .flatMap(b -> {
                                update2.addToSet("teamList.$.students", s);
                                return updateResultMono(query2, update2, TeamCourse.class);
                            });
                })
                .collectList()
                .map(Objects::nonNull);
    }

    /*-------------- 移动小组成员结束 -----------------*/

    /**
     * 修改分组信息的组名
     *
     * @param req
     * @return
     */
    Mono<Boolean> updateTeamName(final ChangeTeamNameReq req) {
        final String key = req.getTeamKey();
        Mono<String> circleId = teamRedisService.findHashString(key, "circleId");
        Mono<String> classId = teamRedisService.findHashString(key, "classId");
        Mono<String> expType = teamRedisService.findHashString(key, "expType");
        return Mono.zip(circleId, classId).zipWith(expType).flatMap(e -> {
            Query query = Query.query(Criteria.where("circleId").is(e.getT1().getT1())
                    .and("classId").is(e.getT1().getT2()).and("teamList.teamId").is(req.getTeamId()));
            Update update = Update.update("uDate", DateUtil.formatDateTime(new Date()));
            update.set("teamList.$.teamName", req.getTeamName());
            if (TEAM_TEMPORARILY.equals(e.getT2())) {
                return reactiveMongoTemplate
                        .updateMulti(query, update, TeamCircle.class)
                        .flatMap(updateResult -> MyAssert.isFalse(updateResult.wasAcknowledged(), DefineCode.ERR0012, "修改失败"))
                        .map(Objects::nonNull);
            } else if (TEAM_FOREVER.equals(e.getT2())) {
                return reactiveMongoTemplate
                        .updateMulti(query, update, TeamCourse.class)
                        .flatMap(updateResult -> MyAssert.isFalse(updateResult.wasAcknowledged(), DefineCode.ERR0012, "修改失败"))
                        .map(Objects::nonNull);
            }
            return MyAssert.isNull(null, DefineCode.ERR0012, "参数不正确");
        });
    }

    /**
     * 在mongdo 中添加一个新的分组信息
     *
     * @param teamId
     * @param req
     * @return
     */
    Mono<Boolean> addTeam(final String teamId, final AddTeamReq req) {
        Mono<List<Students>> students = teamRedisService.findStudentsListByStr(req.getStudents());
        if (TEAM_TEMPORARILY.equals(req.getExpType())) {
            return students.flatMap(studentsList -> {
                Query query = Query.query(Criteria.where("circleId").is(req.getCircleId()).and("classId").is(req.getClassId()));
                Update update = Update.update("uDate", DateUtil.formatDateTime(new Date()));
                update.addToSet("teamList", new Team(teamId, req.getTeamName(), studentsList));
                return reactiveMongoTemplate.upsert(query, update, TeamCircle.class)
                        .flatMap(updateResult -> MyAssert.isFalse(updateResult.wasAcknowledged(), DefineCode.ERR0012, "添加失败"));
            });
        } else if (TEAM_FOREVER.equals(req.getExpType())) {
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

    /**
     * 删除mongobd 中的分组信息
     *
     * @param key
     * @param teamId
     * @param circleId
     * @param classId
     * @return
     */
    Mono<Boolean> deleteTeam(final String key, final String teamId, final String circleId, final String classId) {
        return teamRedisService.findHashString(key, "expType")
                .flatMap(e -> {
                    if (TEAM_TEMPORARILY.equals(e)) {
                        Query query = Query.query(Criteria.where("circleId").is(circleId).and("classId").is(classId));
                        Update update = Update.update("uDate", DateUtil.formatDateTime(new Date()));
                        update.pull("teamList", Query.query(Criteria.where("teamId").is(teamId)));
                        return reactiveMongoTemplate.updateMulti(query, update, TeamCircle.class)
                                .flatMap(teamCircle -> MyAssert.isNull(teamCircle, DefineCode.ERR0012, "mongodb不为空"));
                    } else if (TEAM_FOREVER.equals(e)) {
                        Query query = Query.query(Criteria.where("courseId").is(circleId).and("classId").is(classId));
                        Update update = Update.update("uDate", DateUtil.formatDateTime(new Date()));
                        update.pull("teamList", Query.query(Criteria.where("teamId").is(teamId)));
                        return reactiveMongoTemplate.updateMulti(query, update, TeamCourse.class)
                                .flatMap(teamCircle -> MyAssert.isNull(teamCircle, DefineCode.ERR0012, "mongodb不为空"));
                    }
                    return MyAssert.isNull(null, DefineCode.ERR0012, "参数不正确");
                }).map(Objects::nonNull);
    }

    /**
     * 从mongodb中查询分组信息
     *
     * @param req
     * @return
     */
    Mono<? extends BaseTeam> findTeamList(final CircleIdReq req) {
        if (StrUtil.isNotBlank(req.getClassId())) {
            return reactiveMongoTemplate.findOne(Query.query(Criteria.where("courseId").is(req.getCircleId()).and("classId").is(req.getClassId())), TeamCourse.class)
                    .defaultIfEmpty(new TeamCourse());
        } else {
            return reactiveMongoTemplate.findOne(Query.query(Criteria.where("circleId").is(req.getCircleId())), TeamCircle.class)
                    .defaultIfEmpty(new TeamCircle());
        }
    }
}

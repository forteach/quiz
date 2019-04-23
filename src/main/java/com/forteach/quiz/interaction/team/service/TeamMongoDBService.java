package com.forteach.quiz.interaction.team.service;

import cn.hutool.core.date.DateUtil;
import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.interaction.team.domain.TeamCircle;
import com.forteach.quiz.interaction.team.domain.TeamCourse;
import com.forteach.quiz.interaction.team.web.req.ChangeTeamNameReq;
import com.forteach.quiz.interaction.team.web.req.ChangeTeamReq;
import com.forteach.quiz.interaction.team.web.req.GroupRandomReq;
import com.forteach.quiz.interaction.team.web.resp.GroupTeamResp;
import com.mongodb.client.result.UpdateResult;
import org.reactivestreams.Publisher;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;
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

    public TeamMongoDBService(ReactiveMongoTemplate reactiveMongoTemplate, TeamRedisService teamRedisService) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.teamRedisService = teamRedisService;
    }

    public Mono<Boolean> saveTeamList(final GroupTeamResp groupTeamResp, final GroupRandomReq random) {
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
        //update.set("")
//        return reactiveMongoTemplate.
        return Mono.just(true);
    }

    public Mono<Boolean> updateTeamName(final ChangeTeamNameReq req) {
        final String key = req.getTeamKey();
        Mono<String> circleId = teamRedisService.findHashString(key, "circleId");
        Mono<String> classId = teamRedisService.findHashString(key, "classId");
        Mono<String> expType = teamRedisService.findHashString(key, "expType");
        return Mono.zip(circleId, classId).zipWith(expType).flatMap(e -> {
            Query query = Query.query(Criteria.where("circleId").is(e.getT1().getT1())
                    .and("classId").is(e.getT1().getT2()).and("teamList.teamId").is(req.getTeamId()));
            Update update = new Update();
            update.pull("teamList.teamName", req.getTeamName());
            update.set("uDate", DateUtil.formatDateTime(new Date()));
            if (TEAM_TEMPORARILY.equals(e.getT2())) {
                return reactiveMongoTemplate.updateMulti(query, Update.update("teamList.teamName", req.getTeamName()), TeamCircle.class)
                        .flatMap(updateResult -> MyAssert.isFalse(updateResult.wasAcknowledged(), DefineCode.ERR0012, "修改失败"))
                        .map(Objects::nonNull);
            } else if (TEAM_FOREVER.equals(e.getT2())) {
                return reactiveMongoTemplate.updateMulti(query, update, TeamCourse.class)
                        .log("updateResult ==>> ")
                        .flatMap(updateResult -> MyAssert.isFalse(updateResult.wasAcknowledged(), DefineCode.ERR0012, "修改失败"))
                        .map(Objects::nonNull);
            }
            return MyAssert.isNull(null, DefineCode.ERR0012, "参数不正确");
        });
    }

//    private Mono<Boolean> removeSaveTeam(final GroupTeamResp groupTeamResp, final GroupRandomReq random){
//        return reactiveMongoTemplate.findAndRemove(random.getCircleId())
//                .flatMap(deleteResult -> {
//                    if (deleteResult.wasAcknowledged()){
//                        return builderTeamCircle(groupTeamResp, random);
//                    }
//                    return MyAssert.isFalse(false, DefineCode.ERR0012, "mongodb 删除原来分组失败");
//                });
//    }
}

package com.forteach.quiz.interaction.team.service;

import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.interaction.team.domain.TeamCircle;
import com.forteach.quiz.interaction.team.web.req.GroupRandomReq;
import com.forteach.quiz.interaction.team.web.resp.GroupTeamResp;
import lombok.Builder;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.forteach.quiz.common.Dic.*;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-4-22 17:31
 * @version: 1.0
 * @description:
 */
@Service
public class MongoDBService {

    private final ReactiveMongoTemplate reactiveMongoTemplate;
//    ReactiveMongoOperations

    public MongoDBService(ReactiveMongoTemplate reactiveMongoTemplate){
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

//    public Mono<Boolean> saveTeamList(final GroupTeamResp groupTeamResp, final GroupRandomReq random){
//        return Mono.just(random)
//                .flatMap(r -> {
//                    if (TEAM_TEMPORARILY.equals(r.getExpType())){
//                        //是临时的小组
//                        Query query = Query.query(Criteria.where(MONGDB_ID).is(random.getCircleId()));
//                        return reactiveMongoTemplate.findAndRemove(query, TeamCircle.class, "teamCircle")
//                                .filter(Objects::nonNull)
//                                .flatMap(teamCircle -> {
//                                    return removeSaveTeam(groupTeamResp, random);
//                                })
//                                .flatMap(f -> MyAssert.isFalse(f, DefineCode.ERR0012, "mongodb　保存新分组信息失败"));
//                    }else if (TEAM_FOREVER.equals(r.getExpType())){
//
//                    }
//                    return MyAssert.isNull(null, DefineCode.ERR0002, "");
//                });
//    }

    private Mono<Boolean> builderTeamCircle(GroupTeamResp groupTeamResp, GroupRandomReq r) {
        return Mono.just(true);
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

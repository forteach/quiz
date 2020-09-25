package com.forteach.quiz.interaction.execute.service;

import cn.hutool.core.util.StrUtil;
import com.forteach.quiz.interaction.execute.domain.AskAnswer;
import com.forteach.quiz.interaction.execute.web.req.GradePapersReq;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-5-30 15:01
 * @version: 1.0
 * @description:
 */
@Service
public class GradePapersService {
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    public GradePapersService(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    public Mono<Boolean> saveGradePapersService(final GradePapersReq gradePapersReq) {
        //查询要批改的题
        Criteria criteria = Criteria.where("examineeId").is(gradePapersReq.getExamineeId())
                .and("questionId").is(gradePapersReq.getQuestionId());
        if (StrUtil.isNotBlank(gradePapersReq.getQuestionType())) {
            criteria.and("questionType").is(gradePapersReq.getQuestionType());
        }
        if (StrUtil.isNotBlank(gradePapersReq.getInteractive())) {
            criteria.and("interactive").is(gradePapersReq.getInteractive());
        }
        if (StrUtil.isNotBlank(gradePapersReq.getCircleId())) {
            criteria.and("circleId").is(gradePapersReq.getCircleId());
        }

        Query query = Query.query(criteria);

        //修改题批的题
        Update update = Update.update("score", gradePapersReq.getScore());

        if (StrUtil.isNotBlank(gradePapersReq.getRight())) {
            update.set("right", gradePapersReq.getRight());
        }
        if (StrUtil.isNotBlank(gradePapersReq.getEvaluate())) {
            update.set("evaluate", gradePapersReq.getEvaluate());
        }

        return reactiveMongoTemplate.updateFirst(query, update, AskAnswer.class)
                .map(UpdateResult::wasAcknowledged);
    }
}

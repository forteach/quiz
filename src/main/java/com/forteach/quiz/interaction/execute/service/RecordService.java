package com.forteach.quiz.interaction.execute.service;

import cn.hutool.core.util.StrUtil;
import com.forteach.quiz.interaction.execute.domain.ActivityAskAnswer;
import com.forteach.quiz.interaction.execute.domain.AskAnswer;
import com.forteach.quiz.interaction.execute.web.resp.ActivityAskAnswerResp;
import com.forteach.quiz.interaction.execute.web.resp.InteractAnswerRecordResp;
import com.forteach.quiz.interaction.execute.web.resp.InteractRecordResp;
import com.forteach.quiz.service.StudentsService;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static com.forteach.quiz.common.Dic.MONGDB_ID;

/**
 * @Auther: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2019/5/2 19:10
 * @Version: 1.0
 * @Description:
 */
@Service
public class RecordService {

    private final StudentsService studentsService;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public RecordService(ReactiveMongoTemplate reactiveMongoTemplate, StudentsService studentsService){
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.studentsService = studentsService;
    }

    /**
     * 查询答题结果历史记录
     * @param circleId
     * @param questionsId
     * @return
     */
    public Mono<InteractRecordResp> findQuestionRecord(final String circleId, final String questionsId, final String examineeId, final String questionType, final String interactive) {
        Criteria criteria = Criteria.where("circleId").is(circleId);
        if (StrUtil.isNotBlank(questionsId)){
            criteria.and("questionId").is(questionsId);
        }
        if (StrUtil.isNotBlank(examineeId)){
            criteria.and("examineeId").is(examineeId);
        }
        if (StrUtil.isNotBlank(questionType)){
            criteria.and("questionType").is(questionType);
        }
        if (StrUtil.isNotBlank(interactive)){
            criteria.and("interactive").is(interactive);
        }

        Query query = Query.query(criteria);

        return reactiveMongoTemplate.find(query, AskAnswer.class)
                .collectList()
                .flatMapMany(Flux::fromIterable)
                .filter(Objects::nonNull)
                .flatMap(this::changRecordResp)
                .collectList()
                .flatMap(list -> {
                    return Mono.just(new InteractRecordResp(circleId, questionsId, list));
                });
    }

    private Mono<InteractAnswerRecordResp> changRecordResp(final AskAnswer askAnswer) {
        Mono<String> name = studentsService.findStudentsName(askAnswer.getExamineeId());
        Mono<String> portrait = studentsService.findStudentsPortrait(askAnswer.getExamineeId());
        return name.zipWith(portrait)
                .flatMap(t -> {
                    return Mono.just(new InteractAnswerRecordResp(askAnswer.getExamineeId(), t.getT1(), t.getT2(), askAnswer.getAnswer(), askAnswer.getRight(), askAnswer.getUDate()));
                });
    }

    /**
     * 查询大题历史记录
     * @param circleId
     * @param questionId
     * @return
     */
    public Mono<List<ActivityAskAnswerResp>> findAskRecord(final String circleId, final String questionId, final String examineeId, final String questionType) {
        Criteria criteria = Criteria.where("circleId").is(circleId);
        if (StrUtil.isNotBlank(examineeId)){
            criteria.and("examineeId").is(examineeId);
        }
        if (StrUtil.isNotBlank(questionType)){
            criteria.and("questionType").is(questionType);
        }
        if (StrUtil.isNotBlank(questionId)){
            criteria.and("answList.questionId").is(questionId);
        }
        Query query = Query.query(criteria);

        query.fields().exclude(MONGDB_ID).exclude("udate");

        return reactiveMongoTemplate.find(query, ActivityAskAnswer.class)
                .collectList()
                .flatMapMany(Flux::fromIterable)
                .filter(Objects::nonNull)
                .flatMap(this::recordResp)
                .collectList();
    }

    /**
     * 数据对象进行转换
     * @param activityAskAnswer
     * @return
     */
    private Mono<ActivityAskAnswerResp> recordResp(final ActivityAskAnswer activityAskAnswer) {
        Mono<String> name = studentsService.findStudentsName(activityAskAnswer.getExamineeId());
        Mono<String> portrait = studentsService.findStudentsPortrait(activityAskAnswer.getExamineeId());
        return name.zipWith(portrait)
                .flatMap(t -> {
                    return Mono.just(new ActivityAskAnswerResp(t.getT1(), t.getT2(),
                            activityAskAnswer.getExamineeId(),
                            activityAskAnswer.getQuestionType(),
                            activityAskAnswer.getEvaluate(),
                            activityAskAnswer.getCircleId(),
                            activityAskAnswer.getAnswList()));
                });
    }
}

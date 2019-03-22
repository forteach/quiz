package com.forteach.quiz.interaction.execute.service.record;

import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.interaction.execute.domain.record.InteractRecord;
import com.forteach.quiz.interaction.execute.domain.record.SurveyInteractRecord;
import com.forteach.quiz.interaction.execute.dto.SurveysDto;
import com.forteach.quiz.interaction.execute.repository.InteractRecordRepository;
import com.forteach.quiz.interaction.execute.web.resp.InteractRecordResp;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Arrays;
import java.util.Objects;
import static com.forteach.quiz.common.Dic.INTERACT_RECORD_SURVEYS;
import static com.forteach.quiz.common.Dic.MONGDB_ID;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-3-14 11:35
 * @version: 1.0
 * @description:　问卷调查记录
 */
@Slf4j
@Service
public class InteractRecordSurveyService {
    private final InteractRecordRepository repository;

    private final ReactiveMongoTemplate mongoTemplate;

    private final InteractRecordExecuteService interactRecordExecuteService;

    public InteractRecordSurveyService(InteractRecordRepository repository, ReactiveMongoTemplate mongoTemplate, InteractRecordExecuteService interactRecordExecuteService) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
        this.interactRecordExecuteService = interactRecordExecuteService;
    }

    /**
     * 查询记录
     * @param circleId
     * @param questionsId
     * @return
     */
    public Mono<InteractRecordResp> findRecordSurvey(final String circleId, final String questionsId){
        return findSurveyRecord(circleId, questionsId)
                .flatMap(t -> {
                    if (t != null && t.getIndex() != null){
                        return interactRecordExecuteService.changeRecordResp(t.getSelectId(), t.getIndex(), t.getTime(),
                                t.getNumber(), t.getCategory(), t.getAnswerNumber(), t.getQuestionsId(), t.getAnswerRecordList());
                    }
                    return MyAssert.isNull(null, DefineCode.OK, "不存在相关记录");
                });
    }

    /**
     * 问卷调查记录
     * @param circleId
     * @param questionsId
     * @return
     */
    private Mono<SurveyInteractRecord> findSurveyRecord(final String circleId, final String questionsId){
        return repository.findRecordSurveysByIdAndQuestionsId(circleId, questionsId)
                .filter(Objects::nonNull)
                .map(SurveysDto::getSurveys)
                .filter(list -> list != null && list.size() > 0)
                .flatMapMany(Flux::fromIterable)
                .filter(surveyInteractRecord -> questionsId.equals(surveyInteractRecord.getQuestionsId()))
                .last()
                .onErrorReturn(new SurveyInteractRecord());
    }

    /**
     * 发布记录
     * @param selectId
     * @param circleId
     * @param questionId
     * @param number
     * @param category
     * @return
     */
    Mono<UpdateResult> pushInteractSurveys(final String selectId, final String circleId, final String questionId, final Long number, final String category) {
        Query query = Query.query(Criteria.where(MONGDB_ID).is(circleId));
        Update update = new Update();
        //学生编号id 进行,分割
        SurveyInteractRecord records = new SurveyInteractRecord(questionId, number + 1, category, Arrays.asList(selectId.split(",")));
        update.push(INTERACT_RECORD_SURVEYS, records);
        return mongoTemplate.updateMulti(query, update, InteractRecord.class);
    }
}

package com.forteach.quiz.interaction.execute.service.record;

import com.forteach.quiz.interaction.execute.domain.record.InteractQuestionsRecord;
import com.forteach.quiz.interaction.execute.domain.record.InteractRecord;
import com.forteach.quiz.interaction.execute.dto.QuestionsDto;
import com.forteach.quiz.interaction.execute.repository.InteractRecordRepository;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-3-14 11:50
 * @version: 1.0
 * @description:
 */
@Slf4j
@Service
public class InteractRecordQuestionsService {

    private final InteractRecordRepository repository;

    private final ReactiveMongoTemplate mongoTemplate;

    private final InteractRecordExecuteService interactRecordExecuteService;

    public InteractRecordQuestionsService(InteractRecordRepository repository, ReactiveMongoTemplate mongoTemplate, InteractRecordExecuteService interactRecordExecuteService) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
        this.interactRecordExecuteService = interactRecordExecuteService;
    }
    /**
     * 根据条件查询对应的questions 任务记录
     * @param circleId 课堂id
     * @return Flux<List<InteractQuestionsRecord>>
     */
    public Mono<InteractQuestionsRecord> findQuestionsRecord(final String circleId, final String questionsId) {
        return repository.findRecordByCircleIdAndQuestionsId(circleId, questionsId)
                .filter(Objects::nonNull)
                .map(QuestionsDto::getQuestions)
                .filter(list -> list != null && list.size() > 0)
                .flatMapMany(Flux::fromIterable)
                .filter(interactQuestionsRecord -> questionsId.equals(interactQuestionsRecord.getQuestionsId()))
                .last()
                .onErrorReturn(new InteractQuestionsRecord());
    }

    /**
     * push一条新的发布问题记录
     *
     * @param selectId
     * @param circleId
     * @param questionId
     * @param number
     * @param interactive
     * @param category
     * @return
     */
    Mono<UpdateResult> pushInteractQuestions(final String selectId, final String circleId, final String questionId, final Long number, final String interactive, final String category) {
        Query query = Query.query(Criteria.where("circleId").is(circleId));
        Update update = new Update();
        //学生编号id 进行,分割
        InteractQuestionsRecord records = new InteractQuestionsRecord(questionId, number + 1, interactive, category, Arrays.asList(selectId.split(",")));
        update.push("questions", records);
        return mongoTemplate.updateMulti(query, update, InteractRecord.class);
    }

    /**
     * 获取新的发布问题(指定问题id)
     *
     * @param circleId
     * @param questionId
     * @param category
     * @param interactive
     * @return
     */
    private Query buildLastQuestionsRecord(final String circleId, final String questionId, final String category, final String interactive) {

        final Query query = Query.query(
                Criteria.where("circleId").is(circleId)
                        .and("questions.questionsId").is(questionId)
                        .and("questions.interactive").is(interactive)
                        .and("questions.category").is(category)
        ).with(new Sort(Sort.Direction.DESC, "index")).limit(1);

        query.fields().include("questions");

        return query;
    }

    /**
     * 更新发布的问题
     *
     * @param selectId
     * @param tSelectId
     * @param circleId
     * @param questionId
     * @param category
     * @param interactive
     * @return
     */
    private Mono<UpdateResult> upInteractQuestions(final String selectId, final List<String> tSelectId, final String circleId, final String questionId, final String category, final String interactive) {
        Query query = buildLastQuestionsRecord(circleId, questionId, category, interactive);
        Update update = new Update();
        List<String> list = Arrays.asList(selectId.split(","));
        update.set("questions.$.selectId", list);
        if (!list.equals(tSelectId)) {
            update.inc("questions.$.number", 1);
        }
        return mongoTemplate.updateMulti(query, update, InteractRecord.class);
    }

    /**
     * 获得发布的问题
     *
     * @param circleId
     * @param questionId
     * @param category
     * @param interactive
     * @return
     */
    private Mono<InteractRecord> findInteractQuestionsRecord(final String circleId, final String questionId, final String category, final String interactive) {
        return mongoTemplate
                .findOne(buildLastQuestionsRecord(circleId, questionId, category, interactive), InteractRecord.class)
                .switchIfEmpty(Mono.just(new InteractRecord()));
    }

    /**
     * 发布问题时 加入记录
     * @param circleId
     * @param questionId
     * @param selectId
     * @param category
     * @return
     */
    public Mono<Boolean> releaseQuestion(final String circleId, final String questionId, final String selectId, final String category, final String interactive) {

        Mono<Long> number = interactRecordExecuteService.questionNumber(circleId);

        Mono<InteractRecord> recordMono = findInteractQuestionsRecord(circleId, questionId, category, interactive);

        return Mono.zip(number, recordMono).flatMap(tuple2 -> {

            if (tuple2.getT2().getQuestions() != null && tuple2.getT2().getQuestions().size() > 0) {
                return upInteractQuestions(selectId, tuple2.getT2().getQuestions().get(0).getSelectId(), circleId, questionId, category, interactive);
            } else {
                return pushInteractQuestions(selectId, circleId, questionId, tuple2.getT1(), interactive, category);
            }
        }).map(Objects::nonNull);
    }
}
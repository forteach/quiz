package com.forteach.quiz.interaction.execute.service.record;

import com.forteach.quiz.interaction.execute.domain.record.InteractQuestionsRecord;
import com.forteach.quiz.interaction.execute.domain.record.InteractRecord;
import com.forteach.quiz.interaction.execute.dto.ExerciseBooksDto;
import com.forteach.quiz.interaction.execute.repository.InteractRecordRepository;
import com.forteach.quiz.interaction.execute.web.vo.MoreGiveVo;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-3-14 11:45
 * @version: 1.0
 * @description: 习题册记录
 */
@Slf4j
@Service
public class InteractRecordExerciseBookService {
    private final InteractRecordRepository repository;

    private final ReactiveMongoTemplate mongoTemplate;

    private InteractRecordExecuteService interactRecordExecuteService;

    private UpdateInteractRecordService updateInteractRecordService;

    public InteractRecordExerciseBookService(InteractRecordRepository repository,
                                             ReactiveMongoTemplate mongoTemplate,
                                             InteractRecordExecuteService interactRecordExecuteService,
                                             UpdateInteractRecordService updateInteractRecordService) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
        this.interactRecordExecuteService = interactRecordExecuteService;
        this.updateInteractRecordService = updateInteractRecordService;
    }

    /**
     * 习题册查询记录
     * @param circleId
     * @param questionsId
     * @return
     */
    public Mono<InteractQuestionsRecord> findExerciseBookRecord(final String circleId, final String questionsId) {
        return repository.findExerciseBooksByCircleIdAndQuestionsId(circleId, questionsId)
                .filter(Objects::nonNull)
                .map(ExerciseBooksDto::getExerciseBooks)
                .filter(list -> list != null && list.size() > 0)
                .flatMapMany(Flux::fromIterable)
                .filter(interactQuestionsRecord -> questionsId.equals(interactQuestionsRecord.getQuestionsId()))
                .last()
                .onErrorReturn(new InteractQuestionsRecord());
    }

    /**
     * 发布记录
     * @param selectId
     * @param number
     * @param circleId
     * @param questionId
     * @return
     */
    private Mono<UpdateResult> pushExerciseBook(final String selectId, final Long number, final String circleId, final String questionId) {
        Query query = Query.query(Criteria.where("circleId").is(circleId));
        Update update = new Update();
        //学生编号id 进行,分割
        InteractQuestionsRecord records = new InteractQuestionsRecord(questionId, number + 1, Arrays.asList(selectId.split(",")));
        update.push("exerciseBooks", records);
        return mongoTemplate.updateMulti(query, update, InteractRecord.class);
    }

    private Query buildexerciseBooks(final String circleId, final String questionId) {
        final Query query = Query.query(
                Criteria.where("circleId").is(circleId)
                        .and("exerciseBooks.questionsId").is(questionId)
        ).with(new Sort(Sort.Direction.DESC, "index")).limit(1);
        query.fields().include("exerciseBooks");
        return query;
    }

    /**
     * 获取发布的问题
     * @param circleId
     * @param questionId
     * @return
     */
    private Mono<InteractRecord> findexerciseBooks(final String circleId, final String questionId) {
        return mongoTemplate
                .findOne(buildexerciseBooks(circleId, questionId), InteractRecord.class)
                .switchIfEmpty(Mono.just(new InteractRecord()));
    }

    /**
     * 记录习题册
     * @param giveVo
     * @return
     */
    public Publisher<Boolean> interactiveBook(final MoreGiveVo giveVo) {
        Mono<Long> number = interactRecordExecuteService.exerciseBookNumber(giveVo.getCircleId());
        Mono<InteractRecord> recordMono = findexerciseBooks(giveVo.getCircleId(), giveVo.getQuestionId());
        return Mono.zip(number, recordMono).flatMap(tuple2 -> {

            if (tuple2.getT2().getQuestions() != null && tuple2.getT2().getQuestions().size() > 0) {
                return updateInteractRecordService.upInteractInteractRecord(giveVo.getSelected(), tuple2.getT2().getQuestions().get(0).getSelectId(), giveVo.getCircleId(), giveVo.getQuestionId(), giveVo.getCategory(), "exerciseBooks");
            } else {
                return pushExerciseBook(giveVo.getSelected(), tuple2.getT1(), giveVo.getCircleId(), giveVo.getQuestionId());
            }
        }).map(Objects::nonNull);
    }
}

package com.forteach.quiz.interaction.execute.service;

import com.forteach.quiz.interaction.execute.domain.AskAnswer;
import com.forteach.quiz.interaction.execute.web.resp.InteractAnswerRecordResp;
import com.forteach.quiz.interaction.execute.web.resp.InteractRecordResp;
import com.forteach.quiz.service.StudentsService;
import org.reactivestreams.Publisher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

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

    public Mono<InteractRecordResp> findQuestionRecord(final String circleId, final String questionsId) {
        Query query = Query.query(Criteria.where("circleId").is(circleId)
                .and("questionId").is(questionsId));
        return reactiveMongoTemplate.find(query, AskAnswer.class, "askAnswer")
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

    public Flux<Object> findQuestionRecort(final String circleId, final String questionId) {
        return Flux.just();
    }
}

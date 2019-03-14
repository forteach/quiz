package com.forteach.quiz.interaction.execute.service.record;

import com.forteach.quiz.interaction.execute.domain.record.BrainstormInteractRecord;
import com.forteach.quiz.interaction.execute.domain.record.InteractRecord;
import com.forteach.quiz.interaction.execute.dto.BrainstormDto;
import com.forteach.quiz.interaction.execute.repository.InteractRecordRepository;
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

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-3-14 11:34
 * @version: 1.0
 * @description:　头脑风暴记录
 */
@Service
@Slf4j
public class InteractRecordBrainstormService {
    private final InteractRecordRepository repository;

    private final ReactiveMongoTemplate mongoTemplate;

    public InteractRecordBrainstormService(InteractRecordRepository repository, ReactiveMongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * 查询头脑风暴记录
     * @param circleId
     * @param questionsId
     * @return
     */
    public Mono<BrainstormInteractRecord> findBrainstorm(String circleId, String questionsId) {
        return repository.findBrainstormsByCircleIdAndQuestionsId(circleId, questionsId)
                .filter(Objects::nonNull)
                .map(BrainstormDto::getBrainstorms)
                .filter(list -> list != null && list.size() > 0)
                .flatMapMany(Flux::fromIterable)
                .filter(brainstormInteractRecord -> questionsId.equals(brainstormInteractRecord.getQuestionsId()))
                .last()
                .onErrorReturn(new BrainstormInteractRecord());
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
    Mono<UpdateResult> pushInteractBrainstorms(final String selectId, final String circleId, final String questionId, final Long number, final String category) {
        Query query = Query.query(Criteria.where("circleId").is(circleId));
        Update update = new Update();
        //学生编号id 进行,分割
        BrainstormInteractRecord records = new BrainstormInteractRecord(questionId, number + 1, category, Arrays.asList(selectId.split(",")));
        update.push("brainstorms", records);
        return mongoTemplate.updateMulti(query, update, InteractRecord.class);
    }
}
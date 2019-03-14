package com.forteach.quiz.interaction.execute.service.record;

import com.forteach.quiz.interaction.execute.domain.record.InteractRecord;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.Arrays;
import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-3-14 11:57
 * @version: 1.0
 * @description:
 */
@Slf4j
@Service
public class UpdateInteractRecordService {

    private final ReactiveMongoTemplate mongoTemplate;

    private final InteractRecordExecuteService interactRecordExecuteService;

    public UpdateInteractRecordService(InteractRecordExecuteService interactRecordExecuteService,
                                       ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.interactRecordExecuteService = interactRecordExecuteService;
    }

    Mono<UpdateResult> upInteractInteractRecord(final String selectId, final List<String> tSelectId, final String circleId, final String questionId, final String category, final String interactRecord) {
        Query query = interactRecordExecuteService.buildLastInteractRecord(circleId, questionId, category, interactRecord);
        Update update = new Update();
        List<String> list = Arrays.asList(selectId.split(","));
        update.set(interactRecord + ".$.selectId", list);
        if (!list.equals(tSelectId)) {
            update.inc(interactRecord + ".$.number", 1);
        }
        return mongoTemplate.updateMulti(query, update, InteractRecord.class);
    }
}

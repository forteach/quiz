package com.forteach.quiz.interaction.execute.service.record;

import com.forteach.quiz.interaction.execute.domain.record.InteractRecord;
import com.forteach.quiz.interaction.execute.domain.record.TaskInteractRecord;
import com.forteach.quiz.interaction.execute.dto.TaskInteractDto;
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
import static com.forteach.quiz.common.Dic.INTERACT_RECORD_INTERACTS;
import static com.forteach.quiz.common.Dic.MONGDB_ID;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-3-14 11:36
 * @version: 1.0
 * @description:
 */
@Slf4j
@Service
public class InteractRecordTaskService {
    private final InteractRecordRepository repository;

    private final ReactiveMongoTemplate mongoTemplate;

    public InteractRecordTaskService(InteractRecordRepository repository, ReactiveMongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * 任务记录
     * @param circleId
     * @param questionsId
     * @return
     */
    public Mono<TaskInteractRecord> findTaskRecord(final String circleId, final String questionsId) {
        return repository.findRecordTaskByIdAndQuestionsId(circleId, questionsId)
                .filter(Objects::nonNull)
                .map(TaskInteractDto::getInteracts)
                .filter(list -> list != null && list.size() > 0)
                .flatMapMany(Flux::fromIterable)
                .filter(taskInteractRecord -> questionsId.equals(taskInteractRecord.getQuestionsId()))
                .last()
                .onErrorReturn(new TaskInteractRecord());
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
    Mono<UpdateResult> pushInteractTask(final String selectId, final String circleId, final String questionId, final Long number, final String category) {
        Query query = Query.query(Criteria.where(MONGDB_ID).is(circleId));
        Update update = new Update();
        //学生编号id 进行,分割
        TaskInteractRecord records = new TaskInteractRecord(questionId, number + 1, category, Arrays.asList(selectId.split(",")));
        update.push(INTERACT_RECORD_INTERACTS, records);
        return mongoTemplate.updateMulti(query, update, InteractRecord.class);
    }
}

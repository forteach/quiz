package com.forteach.quiz.interaction.execute.service.record;

import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.interaction.execute.domain.record.InteractQuestionsRecord;
import com.forteach.quiz.interaction.execute.domain.record.InteractRecord;
import com.forteach.quiz.interaction.execute.dto.ExerciseBooksDto;
import com.forteach.quiz.interaction.execute.repository.InteractRecordRepository;
import com.forteach.quiz.interaction.execute.web.resp.InteractRecordResp;
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
import java.util.Objects;
import static com.forteach.quiz.common.Dic.INTERACT_RECORD_EXERCISEBOOKS;
import static com.forteach.quiz.common.Dic.MONGDB_ID;

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

    private UpdateInteractRecordService updateInteractRecordService;

    private InteractRecordExecuteService interactRecordExecuteService;

    public InteractRecordExerciseBookService(InteractRecordRepository repository,
                                             ReactiveMongoTemplate mongoTemplate,
                                             InteractRecordExecuteService interactRecordExecuteService,
                                             UpdateInteractRecordService updateInteractRecordService) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
        this.updateInteractRecordService = updateInteractRecordService;
        this.interactRecordExecuteService = interactRecordExecuteService;
    }

    /**
     * 查询问题记录
     * @param circleId
     * @param questionsId
     * @return
     */
    public Mono<InteractRecordResp> findRecordExerciseBook(final String circleId, final String questionsId){
        return findExerciseBookRecord(circleId, questionsId)
                .flatMap(t -> {
                    if (t != null && t.getAnswerRecordList() != null){
                        return interactRecordExecuteService.changeFindRecord(t.getAnswerRecordList(), null, t.getCategory());
                    }else if (t != null && t.getIndex() != null){
                        return interactRecordExecuteService.changeRecord(t.getSelectId(), null, t.getCategory());
                    }
                    return MyAssert.isNull(null, DefineCode.OK, "不存在相关记录");
                });
    }

    /**
     * 习题册查询记录
     * @param circleId
     * @param questionsId
     * @return
     */
    private Mono<InteractQuestionsRecord> findExerciseBookRecord(final String circleId, final String questionsId) {
        return repository.findExerciseBooksByIdAndQuestionsId(circleId, questionsId)
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
        Query query = Query.query(Criteria.where(MONGDB_ID).is(circleId));
        Update update = new Update();
        //学生编号id 进行,分割
        InteractQuestionsRecord records = new InteractQuestionsRecord(questionId, number + 1, Arrays.asList(selectId.split(",")));
        update.push(INTERACT_RECORD_EXERCISEBOOKS, records);
        return mongoTemplate.updateMulti(query, update, InteractRecord.class);
    }

    private Query buildexerciseBooks(final String circleId, final String questionId) {
        final Query query = Query.query(
                Criteria.where(MONGDB_ID).is(circleId)
                        .and(INTERACT_RECORD_EXERCISEBOOKS + ".questionsId").is(questionId)
        ).with(new Sort(Sort.Direction.DESC, "index")).limit(1);
        query.fields().include(INTERACT_RECORD_EXERCISEBOOKS);
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
     * 计算习题册发布的次数
     * @param circleId
     * @return
     */
    private Mono<Long> exerciseBookNumber(final String circleId){
        return mongoTemplate.count(Query.query(
                Criteria.where(MONGDB_ID).is(circleId)
                        .and(INTERACT_RECORD_EXERCISEBOOKS + ".questionsId").ne("").ne(null)),
                InteractRecord.class).switchIfEmpty(Mono.just(0L));
    }

    /**
     * 记录习题册
     * @param
     * @return
     */
    public Mono<Boolean> interactiveBook(final String circleId, final String questionId, final String selected, final String category) {
        Mono<Long> number = exerciseBookNumber(circleId);
        Mono<InteractRecord> recordMono = findexerciseBooks(circleId, questionId);
        return Mono.zip(number, recordMono).flatMap(tuple2 -> {

            if (tuple2.getT2().getQuestions() != null && tuple2.getT2().getQuestions().size() > 0) {
                return updateInteractRecordService.upInteractInteractRecord(selected,
                        tuple2.getT2().getQuestions().get(0).getSelectId(), circleId,
                        questionId, category, INTERACT_RECORD_EXERCISEBOOKS);
            } else {
                return pushExerciseBook(selected, tuple2.getT1(), circleId, questionId);
            }
        }).map(Objects::nonNull);
    }
}

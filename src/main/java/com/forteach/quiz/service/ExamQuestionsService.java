package com.forteach.quiz.service;

import com.forteach.quiz.domain.*;
import com.forteach.quiz.exceptions.ExamQuestionsExceptions;
import com.forteach.quiz.repository.BigQuestionRepository;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.forteach.quiz.common.Dic.MONGDB_COLUMN_QUESTION_BANK_TEACHER;
import static com.forteach.quiz.common.Dic.MONGDB_ID;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/13  11:45
 */
@Component
public class ExamQuestionsService {

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    private BigQuestionRepository bigQuestionRepository;

    public ExamQuestionsService(ReactiveMongoTemplate reactiveMongoTemplate,BigQuestionRepository bigQuestionRepository) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.bigQuestionRepository = bigQuestionRepository;
    }


    /**
     * 修改新增思考题
     *
     * @param bigQuestion
     * @return
     */
    public Mono<BigQuestion> editDesign(final BigQuestion<Design> bigQuestion) {
        return bigQuestionRepository.save(bigQuestion).flatMap(t -> {
            Mono<UpdateResult> questionBankMono = questionBankAssociation(t.getId(), t.getTeacherId());
            return questionBankMono.flatMap(
                    updateResult -> {
                        if (updateResult.isModifiedCountAvailable()) {
                            return Mono.just(t);
                        } else {
                            return Mono.error(new ExamQuestionsExceptions("保存 简答思考题 作者失败"));
                        }
                    }
            );
        });
    }


    /**
     * 修改新增判断题
     *
     * @param bigQuestion
     * @return
     */
    public Mono<BigQuestion> editTrueOrFalse(final BigQuestion<TrueOrFalse> bigQuestion) {
        return bigQuestionRepository.save(bigQuestion).flatMap(t -> {
            Mono<UpdateResult> questionBankMono = questionBankAssociation(t.getId(), t.getTeacherId());
            return questionBankMono.flatMap(
                    updateResult -> {
                        if (updateResult.isModifiedCountAvailable()) {
                            return Mono.just(t);
                        } else {
                            return Mono.error(new ExamQuestionsExceptions("保存 判断题 作者失败"));
                        }
                    }
            );
        });
    }

    /**
     * 修改新增选择题
     *
     * @param bigQuestion
     * @return
     */
    public Mono<BigQuestion> editChoiceQst(final BigQuestion<ChoiceQst> bigQuestion) {
        return bigQuestionRepository.save(bigQuestion).flatMap(t -> {
            Mono<UpdateResult> questionBankMono = questionBankAssociation(t.getId(), t.getTeacherId());
            return questionBankMono.flatMap(
                    updateResult -> {
                        if (updateResult.isModifiedCountAvailable()) {
                            return Mono.just(t);
                        } else {
                            return Mono.error(new ExamQuestionsExceptions("保存 选择题 作者失败"));
                        }
                    }
            );
        });
    }


    private Mono<UpdateResult> questionBankAssociation(final String questionBankId, final String teacherId) {
        return reactiveMongoTemplate.upsert(Query.query(Criteria.where(MONGDB_ID).is(questionBankId)), new Update().addToSet(MONGDB_COLUMN_QUESTION_BANK_TEACHER, teacherId), QuestionBank.class);
    }

    public Mono<Boolean> questionBankAssociationAdd(final String questionBankId, final String teacherId) {
        return questionBankAssociation(questionBankId, teacherId).map(UpdateResult::isModifiedCountAvailable);
    }


}

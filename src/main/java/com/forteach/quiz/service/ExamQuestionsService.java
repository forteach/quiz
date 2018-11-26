package com.forteach.quiz.service;

import com.forteach.quiz.domain.*;
import com.forteach.quiz.exceptions.ExamQuestionsExceptions;
import com.forteach.quiz.repository.BigQuestionRepository;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.forteach.quiz.common.Dic.MONGDB_COLUMN_QUESTION_BANK_TEACHER;
import static com.forteach.quiz.common.Dic.MONGDB_ID;
import static com.forteach.quiz.util.StringUtil.getRandomUUID;
import static com.forteach.quiz.util.StringUtil.isEmpty;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/13  11:45
 */
@Component
public class ExamQuestionsService {

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    private final BigQuestionRepository bigQuestionRepository;



    public ExamQuestionsService(ReactiveMongoTemplate reactiveMongoTemplate, BigQuestionRepository bigQuestionRepository) {
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

        return bigQuestionRepository.save(setExamDesignUUID(bigQuestion)).flatMap(t -> {
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
        return bigQuestionRepository.save(setExamTrueOrFalseUUID(bigQuestion)).flatMap(t -> {
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
        return bigQuestionRepository.save(setExamChoiceQstUUID(bigQuestion)).flatMap(t -> {
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

    public Flux<BigQuestion> editBigQuestion(final List<BigQuestion> questionList) {
        return bigQuestionRepository.saveAll(questionList);
    }

    /**
     * 删除单道题
     *
     * @param id
     * @return
     */
    public Mono<Void> delQuestions(final String id) {
        return bigQuestionRepository.deleteById(id).and(delBankAssociation(Collections.singletonList(id)));
    }

    private Mono<UpdateResult> questionBankAssociation(final String questionBankId, final String teacherId) {
        return reactiveMongoTemplate.upsert(Query.query(Criteria.where(MONGDB_ID).is(questionBankId)), new Update().addToSet(MONGDB_COLUMN_QUESTION_BANK_TEACHER, teacherId), QuestionBank.class);
    }

    private Mono<DeleteResult> delBankAssociation(final List<String> id) {
        return reactiveMongoTemplate.remove(Query.query(Criteria.where(MONGDB_ID).is(id)), BigQuestion.class);
    }

    public Mono<Boolean> questionBankAssociationAdd(final String questionBankId, final String teacherId) {
        return questionBankAssociation(questionBankId, teacherId).map(UpdateResult::isModifiedCountAvailable);
    }

    public Flux<BigQuestion> findBigQuestionInId(final List<String> ids) {
        return reactiveMongoTemplate.find(Query.query(Criteria.where(MONGDB_ID).in(ids)), BigQuestion.class);
    }

    /**
     * .parallel() 并行处理 （CPU）
     *
     * @param bigQuestion
     * @return
     */
    private BigQuestion<Design> setExamDesignUUID(final BigQuestion<Design> bigQuestion) {
        bigQuestion.setExamChildren(bigQuestion.getExamChildren()
                .stream()
                .parallel()
                .peek(design -> {
                    if (isEmpty(design.getId())) {
                        design.setId(getRandomUUID());
                    }
                })
                .collect(Collectors.toList()));
        return bigQuestion;
    }

    private BigQuestion<TrueOrFalse> setExamTrueOrFalseUUID(final BigQuestion<TrueOrFalse> bigQuestion) {
        bigQuestion.setExamChildren(bigQuestion.getExamChildren()
                .stream()
                .parallel()
                .peek(trueOrFalse -> {
                    if (isEmpty(trueOrFalse.getId())) {
                        trueOrFalse.setId(getRandomUUID());
                    }
                })
                .collect(Collectors.toList()));
        return bigQuestion;
    }

    private BigQuestion<ChoiceQst> setExamChoiceQstUUID(final BigQuestion<ChoiceQst> bigQuestion) {
        bigQuestion.setExamChildren(bigQuestion.getExamChildren()
                .stream()
                .parallel()
                .peek(choiceQst -> {
                    if (isEmpty(choiceQst.getId())) {
                        choiceQst.setId(getRandomUUID());
                        choiceQst.setOptChildren(choiceQst.getOptChildren()
                                .stream()
                                .parallel()
                                .peek(choiceQstOption -> {
                                    if (isEmpty(choiceQstOption.getId())) {
                                        choiceQstOption.setId(getRandomUUID());
                                    }
                                })
                                .collect(Collectors.toList()));
                    }
                })
                .collect(Collectors.toList()));
        return bigQuestion;
    }


}

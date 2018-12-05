package com.forteach.quiz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.forteach.quiz.domain.*;
import com.forteach.quiz.exceptions.CustomException;
import com.forteach.quiz.exceptions.ExamQuestionsException;
import com.forteach.quiz.exceptions.ProblemSetException;
import com.forteach.quiz.repository.BigQuestionRepository;
import com.forteach.quiz.web.vo.SortVo;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.forteach.quiz.common.Dic.*;
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

    private Mono<BigQuestion> editQuestionsCover(final BigQuestion bigQuestion) {

        Query query = Query.query(Criteria.where(QUESTION_CHILDREN + "." + MONGDB_ID).is(bigQuestion.getId()));
        Update update = new Update();
        update.set("questionChildren.$.paperInfo", bigQuestion.getPaperInfo());
        update.set("questionChildren.$.examChildren", bigQuestion.getExamChildren());
        update.set("questionChildren.$.type", bigQuestion.getType());
        update.set("questionChildren.$.index", bigQuestion.getIndex());
        update.set("questionChildren.$.score", bigQuestion.getScore());
        return reactiveMongoTemplate.updateMulti(query, update, ExerciseBook.class).map(UpdateResult::getMatchedCount).flatMap(obj -> {
            if (obj != -1) {
                return bigQuestionRepository.save(bigQuestion);
            } else {
                return Mono.error(new ProblemSetException("更新失败"));
            }
        });

    }


    private Mono<BigQuestion> editQuestions(final BigQuestion bigQuestion) {
        bigQuestion.setUDate(new Date());
        if (bigQuestion.getRelate() == COVER_QUESTION_BANK) {
            return editQuestionsCover(bigQuestion);
        }
        return bigQuestionRepository.save(bigQuestion);
    }

    private Flux<BigQuestion> editBigQuestion(final List<BigQuestion> questionList) {
        return bigQuestionRepository.saveAll(questionList);
    }

    public Flux<BigQuestion> editExerciseBookQuestion(int relate, final List<BigQuestion> questionList) {
        if (relate == COVER_QUESTION_BANK) {
            return editBigQuestion(questionList);
        }
        return Flux.fromIterable(questionList);
    }

    /**
     * 修改新增思考题
     *
     * @param bigQuestion
     * @return
     */
    public Mono<BigQuestion> editDesign(final BigQuestion<Design> bigQuestion) {

        return editQuestions(setExamDesignUUID(bigQuestion)).flatMap(t -> {
            Mono<UpdateResult> questionBankMono = questionBankAssociation(t.getId(), t.getTeacherId());
            return questionBankMono.flatMap(
                    updateResult -> {
                        if (updateResult.isModifiedCountAvailable()) {
                            return Mono.just(t);
                        } else {
                            return Mono.error(new ExamQuestionsException("保存 简答思考题 作者失败"));
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
        return editQuestions(setExamTrueOrFalseUUID(bigQuestion)).flatMap(t -> {
            Mono<UpdateResult> questionBankMono = questionBankAssociation(t.getId(), t.getTeacherId());
            return questionBankMono.flatMap(
                    updateResult -> {
                        if (updateResult.isModifiedCountAvailable()) {
                            return Mono.just(t);
                        } else {
                            return Mono.error(new ExamQuestionsException("保存 判断题 作者失败"));
                        }
                    }
            );
        });
    }

    /**
     * 修改新增大题
     *
     * @param bigQuestion
     * @return
     */
    public Mono<BigQuestion> editBigQuestion(final BigQuestion bigQuestion) {
        return editQuestions(setBigQuestionUUID(bigQuestion)).flatMap(t -> {
            Mono<UpdateResult> questionBankMono = questionBankAssociation(t.getId(), t.getTeacherId());
            return questionBankMono.flatMap(
                    updateResult -> {
                        if (updateResult.isModifiedCountAvailable()) {
                            return Mono.just(t);
                        } else {
                            return Mono.error(new ExamQuestionsException("保存 大题 作者失败"));
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
        return editQuestions(setExamChoiceQstUUID(bigQuestion)).flatMap(t -> {
            Mono<UpdateResult> questionBankMono = questionBankAssociation(t.getId(), t.getTeacherId());
            return questionBankMono.flatMap(
                    updateResult -> {
                        if (updateResult.isModifiedCountAvailable()) {
                            return Mono.just(t);
                        } else {
                            return Mono.error(new ExamQuestionsException("保存 选择题 作者失败"));
                        }
                    }
            );
        });
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

    public Flux<BigQuestion> findAllDetailed(final SortVo sortVo) {

        Sort sort = new Sort(Sort.Direction.DESC, sortVo.getSorting());

        return bigQuestionRepository.findAllDetailedPage(sortVo.getOperatorId(), PageRequest.of(sortVo.getPage(), sortVo.getSize(), sort));
    }

    public Mono<BigQuestion> findOneDetailed(final String id) {
        return bigQuestionRepository.findById(id).switchIfEmpty(Mono.error(new CustomException("没有找到考题")));
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
                .peek(trueOrFalse -> {
                    if (isEmpty(trueOrFalse.getId())) {
                        trueOrFalse.setId(getRandomUUID());
                    }
                })
                .collect(Collectors.toList()));
        return bigQuestion;
    }

    private BigQuestion setBigQuestionUUID(final BigQuestion bigQuestion) {
        bigQuestion.setExamChildren((List) bigQuestion.getExamChildren()
                .stream()
                .map(obj -> {
                    JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString((LinkedHashMap) obj));
                    String type = jsonObject.getString(BIG_QUESTION_EXAM_CHILDREN_TYPE);
                    switch (type) {
                        case BIG_QUESTION_EXAM_CHILDREN_TYPE_CHOICE:
                            ChoiceQst choiceQst = JSON.parseObject(jsonObject.toJSONString(), ChoiceQst.class);
                            if (isEmpty(choiceQst.getId())) {
                                choiceQst.setId(getRandomUUID());
                            }
                            return choiceQst;
                        case BIG_QUESTION_EXAM_CHILDREN_TYPE_TRUEORFALSE:
                            TrueOrFalse trueOrFalse = JSON.parseObject(jsonObject.toJSONString(), TrueOrFalse.class);
                            if (isEmpty(trueOrFalse.getId())) {
                                trueOrFalse.setId(getRandomUUID());
                            }
                            return trueOrFalse;
                        case BIG_QUESTION_EXAM_CHILDREN_TYPE_DESIGN:
                            Design design = JSON.parseObject(jsonObject.toJSONString(), Design.class);
                            if (isEmpty(design.getId())) {
                                design.setId(getRandomUUID());
                            }
                            return design;
                        default:
                            throw new ExamQuestionsException("非法参数 错误的题目类型");
                    }

                })
                .collect(Collectors.toList()));
        return bigQuestion;
    }

    private BigQuestion<ChoiceQst> setExamChoiceQstUUID(final BigQuestion<ChoiceQst> bigQuestion) {
        bigQuestion.setExamChildren(bigQuestion.getExamChildren()
                .stream()
                .peek(choiceQst -> {
                    if (isEmpty(choiceQst.getId())) {
                        choiceQst.setId(getRandomUUID());
                        choiceQst.setOptChildren(choiceQst.getOptChildren()
                                .stream()
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

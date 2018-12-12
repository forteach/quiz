package com.forteach.quiz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.forteach.quiz.domain.*;
import com.forteach.quiz.exceptions.CustomException;
import com.forteach.quiz.exceptions.ExamQuestionsException;
import com.forteach.quiz.exceptions.ProblemSetException;
import com.forteach.quiz.repository.BigQuestionRepository;
import com.forteach.quiz.web.req.QuestionBankReq;
import com.mongodb.BasicDBObject;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.forteach.quiz.common.Dic.*;
import static com.forteach.quiz.util.StringUtil.*;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/13  11:45
 */
@Service
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

    public Flux<BigQuestion> findAllDetailed(final QuestionBankReq sortVo) {

        if (PARAMETER_PART.equals(sortVo.getAllOrPart())) {
            return findPartQuestion(sortVo);
        } else if (PARAMETER_ALL.equals(sortVo.getAllOrPart())) {
            return findAllQuestion(sortVo);
        }
        return Flux.error(new ExamQuestionsException("错误的查询条件"));
    }

    private Flux<BigQuestion> findPartQuestion(final QuestionBankReq sortVo) {
        //返回指定字段
        BasicDBObject fieldsObject = new BasicDBObject();
        fieldsObject.put("id", 1);
        fieldsObject.put("chapter", 1);
        fieldsObject.put("levelId", 1);
        fieldsObject.put("knowledgeId", 1);
        fieldsObject.put("examType", 1);
        fieldsObject.put("teacherId", 1);
        fieldsObject.put("uDate", 1);
        //查询条件
        BasicDBObject dbObject = new BasicDBObject();
        if (isNotEmpty(sortVo.getLevelId())) {
            dbObject.put("levelId", sortVo.getLevelId());
        }
        if (isNotEmpty(sortVo.getChapter())) {
            dbObject.put("chapter", sortVo.getChapter());
        }
        if (isNotEmpty(sortVo.getKnowledgeId())) {
            dbObject.put("knowledgeId", sortVo.getKnowledgeId());
        }
        if (isNotEmpty(sortVo.getQuestionType())) {
            dbObject.put("examChildren.$.examType", sortVo.getQuestionType());
        }
        //创建查询条件
        Query query = new BasicQuery(dbObject.toJson(), fieldsObject.toJson());
        //分页及排序等
        sortVo.queryPaging(query);
        //执行查询
        return reactiveMongoTemplate.find(query, BigQuestion.class);
    }

    private Flux<BigQuestion> findAllQuestion(final QuestionBankReq sortVo) {

        Criteria criteria = Criteria.where("teacherId").is(sortVo.getOperatorId());

        Query query = new Query(criteria);

        if (isNotEmpty(sortVo.getLevelId())) {
            criteria.and("levelId").in(sortVo.getLevelId());
        }
        if (isNotEmpty(sortVo.getChapter())) {
            criteria.and("chapter").in(sortVo.getChapter());
        }
        if (isNotEmpty(sortVo.getKnowledgeId())) {
            criteria.and("knowledgeId").in(sortVo.getKnowledgeId());
        }
        if (isNotEmpty(sortVo.getQuestionType())) {
            criteria.and("examChildren.$.examType").in(sortVo.getQuestionType());
        }

        sortVo.queryPaging(query);

        return reactiveMongoTemplate.find(query, BigQuestion.class);
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
                    design.setExamType("design");
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
                    trueOrFalse.setExamType("bigQuestion");
                })
                .collect(Collectors.toList()));
        return bigQuestion;
    }

    private BigQuestion setBigQuestionUUID(final BigQuestion bigQuestion) {
        bigQuestion.setExamChildren((List) bigQuestion.getExamChildren()
                .stream()
                .map(obj -> {
                    JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(obj));
                    String type = jsonObject.getString(BIG_QUESTION_EXAM_CHILDREN_TYPE);
                    switch (type) {
                        case BIG_QUESTION_EXAM_CHILDREN_TYPE_CHOICE:
                            ChoiceQst choiceQst = JSON.parseObject(jsonObject.toJSONString(), ChoiceQst.class);
                            if (isEmpty(choiceQst.getId())) {
                                choiceQst.setId(getRandomUUID());
                            }
                            choiceQst.setExamType("bigQuestion");
                            return choiceQst;
                        case BIG_QUESTION_EXAM_CHILDREN_TYPE_TRUEORFALSE:
                            TrueOrFalse trueOrFalse = JSON.parseObject(jsonObject.toJSONString(), TrueOrFalse.class);
                            if (isEmpty(trueOrFalse.getId())) {
                                trueOrFalse.setId(getRandomUUID());
                            }
                            trueOrFalse.setExamType("bigQuestion");
                            return trueOrFalse;
                        case BIG_QUESTION_EXAM_CHILDREN_TYPE_DESIGN:
                            Design design = JSON.parseObject(jsonObject.toJSONString(), Design.class);
                            if (isEmpty(design.getId())) {
                                design.setId(getRandomUUID());
                            }
                            design.setExamType("bigQuestion");
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
                        choiceQst.setExamType(choiceQst.getChoiceType());
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

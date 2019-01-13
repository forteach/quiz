package com.forteach.quiz.questionlibrary.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.forteach.quiz.domain.QuestionIds;
import com.forteach.quiz.exceptions.ExamQuestionsException;
import com.forteach.quiz.problemsetlibrary.domain.BigQuestionProblemSet;
import com.forteach.quiz.problemsetlibrary.repository.BigQuestionProblemSetRepository;
import com.forteach.quiz.questionlibrary.domain.BigQuestion;
import com.forteach.quiz.questionlibrary.domain.question.ChoiceQst;
import com.forteach.quiz.questionlibrary.domain.question.Design;
import com.forteach.quiz.questionlibrary.domain.question.TrueOrFalse;
import com.forteach.quiz.questionlibrary.reflect.QuestionReflect;
import com.forteach.quiz.questionlibrary.repository.BigQuestionRepository;
import com.forteach.quiz.questionlibrary.repository.base.QuestionMongoRepository;
import com.forteach.quiz.questionlibrary.service.base.BaseQuestionServiceImpl;
import com.forteach.quiz.questionlibrary.web.req.QuestionBankReq;
import com.forteach.quiz.questionlibrary.web.req.QuestionProblemSetReq;
import com.forteach.quiz.web.vo.QuestionProblemSetVo;
import com.mongodb.BasicDBObject;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
@Service
public class BigQuestionService extends BaseQuestionServiceImpl<BigQuestion> {


    private final BigQuestionRepository bigQuestionRepository;

    private final BigQuestionProblemSetRepository bigQuestionProblemSetRepository;

    public BigQuestionService(QuestionMongoRepository<BigQuestion> repository,
                              ReactiveMongoTemplate reactiveMongoTemplate,
                              QuestionReflect questionReflect,
                              BigQuestionRepository bigQuestionRepository,
                              BigQuestionProblemSetRepository bigQuestionProblemSetRepository) {

        super(repository, reactiveMongoTemplate, questionReflect);
        this.bigQuestionRepository = bigQuestionRepository;
        this.bigQuestionProblemSetRepository = bigQuestionProblemSetRepository;
    }

    /**
     * 批量保存修改
     * @param questionList
     * @return
     */
    private Flux<BigQuestion> editBigQuestion(final List<BigQuestion> questionList) {
        return bigQuestionRepository.saveAll(questionList);
    }

    /**
     * 更改问题 (根据需要是否修改至练习册)
     * @param relate
     * @param questionList
     * @return
     */
    public Flux<BigQuestion> editExerciseBookQuestion(int relate, final List<BigQuestion> questionList) {
        if (relate == COVER_QUESTION_BANK) {
            return editBigQuestion(questionList);
        }
        return Flux.fromIterable(questionList);
    }

    /**
     * 通过id查找题集及包含的题目全部信息
     * @param questionProblemSetReq
     * @return
     */
    public Mono<QuestionProblemSetVo> questionProblemSet(final QuestionProblemSetReq questionProblemSetReq) {

        QuestionBankReq questionBankReq = new QuestionProblemSetReq();
        BeanUtils.copyProperties(questionProblemSetReq, questionBankReq);

        Mono<List<BigQuestion>> questionFlux = findAllDetailed(questionBankReq).collectList();

        return questionFlux.zipWith(findProblemSet(questionProblemSetReq.getProblemSetId()), (questionList, problemSet) -> {

            List<String> target = problemSet.getQuestionIds().stream().map(QuestionIds::getBigQuestionId).collect(Collectors.toList());
            List<String> origin = questionList.stream().map(BigQuestion::getId).collect(Collectors.toList());
            //交集
            List<String> intersection = origin.stream().filter(target::contains).collect(Collectors.toList());
            //差集
            List<String> difference = origin.stream().filter(item -> !target.contains(item)).collect(Collectors.toList());
            return QuestionProblemSetVo.builder().bigQuestionList(questionList).problemSet(problemSet).intersection(intersection).difference(difference).build();
        });
    }

    /**
     * 设置大题的id及属性
     * @param bigQuestion
     * @return
     */
    private BigQuestion setBigQuestionUUID(final BigQuestion bigQuestion) {
        bigQuestion.setExamChildren((List) bigQuestion.getExamChildren()
                .stream()
                .map(obj -> {
                    JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(obj));
                    String type = jsonObject.getString(BIG_QUESTION_EXAM_CHILDREN_TYPE);
                    switch (type) {
                        case QUESTION_CHOICE_OPTIONS_SINGLE:
                        case QUESTION_CHOICE_MULTIPLE_SINGLE:
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

    /**
     * 根据id 获取练习册 基本信息
     *
     * @param exerciseBookId
     * @return
     */
    public Mono<BigQuestionProblemSet> findProblemSet(final String exerciseBookId) {
        return bigQuestionProblemSetRepository.findById(exerciseBookId);
    }

    /**
     * 更新大题中的某项
     *
     * @param childrenId
     * @param json
     * @return
     */
    public Mono<Boolean> updateChildren(final String childrenId, final String json, final String teacherId) {

        JSONObject jsonObject = JSON.parseObject(json);

        String type = jsonObject.getString(BIG_QUESTION_EXAM_CHILDREN_TYPE);

        Query query = Query.query(Criteria.where(BIG_QUESTION_EXAM_CHILDREN + "." + MONGDB_ID).is(childrenId));
        Update update = new Update();

        switch (type) {
            case QUESTION_CHOICE_OPTIONS_SINGLE:
            case QUESTION_CHOICE_MULTIPLE_SINGLE:
                ChoiceQst choiceQst = JSON.parseObject(jsonObject.toJSONString(), ChoiceQst.class);
                choiceQst.setId(childrenId);

                update.set("examChildren.$.score", choiceQst.getScore());
                update.set("examChildren.$.teacherId", teacherId);
                update.set("examChildren.$.examType", "bigQuestion");
                update.set("examChildren.$.choiceQstTxt", choiceQst.getChoiceQstTxt());
                update.set("examChildren.$.choiceQstAnsw", choiceQst.getChoiceQstAnsw());
                update.set("examChildren.$.choiceQstAnalysis", choiceQst.getChoiceQstAnalysis());
                update.set("examChildren.$.choiceType", choiceQst.getChoiceType());
                update.set("examChildren.$.optChildren", choiceQst.getOptChildren());

                break;
            case BIG_QUESTION_EXAM_CHILDREN_TYPE_TRUEORFALSE:
                TrueOrFalse trueOrFalse = JSON.parseObject(jsonObject.toJSONString(), TrueOrFalse.class);
                trueOrFalse.setId(childrenId);

                update.set("examChildren.$.score", trueOrFalse.getScore());
                update.set("examChildren.$.teacherId", teacherId);
                update.set("examChildren.$.examType", "bigQuestion");
                update.set("examChildren.$.trueOrFalseInfo", trueOrFalse.getTrueOrFalseInfo());
                update.set("examChildren.$.trueOrFalseAnsw", trueOrFalse.getTrueOrFalseAnsw());
                update.set("examChildren.$.trueOrFalseAnalysis", trueOrFalse.getTrueOrFalseAnalysis());
                break;
            case BIG_QUESTION_EXAM_CHILDREN_TYPE_DESIGN:
                Design design = JSON.parseObject(jsonObject.toJSONString(), Design.class);
                design.setId(childrenId);

                update.set("examChildren.$.score", design.getScore());
                update.set("examChildren.$.teacherId", teacherId);
                update.set("examChildren.$.examType", "bigQuestion");
                update.set("examChildren.$.designQuestion", design.getDesignQuestion());
                update.set("examChildren.$.designAnsw", design.getDesignAnsw());
                update.set("examChildren.$.designAnalysis", design.getDesignAnalysis());
                break;
            default:
                throw new ExamQuestionsException("非法参数 错误的题目类型");
        }

        return reactiveMongoTemplate.updateFirst(query, update, BigQuestion.class).map(UpdateResult::wasAcknowledged);


    }

    /**
     * 删除大题中的某项
     *
     * @param childrenId
     * @return
     */
    public Mono<Boolean> deleteChildren(final String childrenId) {

        Query query = Query.query(Criteria.where(BIG_QUESTION_EXAM_CHILDREN + "." + MONGDB_ID).is(childrenId));

        Update update = new Update();
        update.pull("examChildren", new BasicDBObject(MONGDB_ID, childrenId));
        return reactiveMongoTemplate.updateFirst(query, update, BigQuestion.class).map(Objects::nonNull);
    }

    /**
     * 新增大题子题目一项
     *
     * @param questionId
     * @return
     */
    public Mono<Boolean> addChildren(final String questionId, final String json, final String teacherId) {

        JSONObject jsonObject = JSON.parseObject(json);

        Query query = Query.query(Criteria.where(MONGDB_ID).is(questionId));

        Update update = new Update();

        String type = jsonObject.getString(BIG_QUESTION_EXAM_CHILDREN_TYPE);

        BigQuestion bigQuestion = new BigQuestion();

        switch (type) {
            case QUESTION_CHOICE_OPTIONS_SINGLE:
            case QUESTION_CHOICE_MULTIPLE_SINGLE:
                ChoiceQst choiceQst = JSON.parseObject(jsonObject.toJSONString(), ChoiceQst.class);
                choiceQst.setTeacherId(teacherId);
                bigQuestion.setExamChildren(Collections.singletonList(choiceQst));
                break;
            case BIG_QUESTION_EXAM_CHILDREN_TYPE_TRUEORFALSE:
                TrueOrFalse trueOrFalse = JSON.parseObject(jsonObject.toJSONString(), TrueOrFalse.class);
                trueOrFalse.setTeacherId(teacherId);
                bigQuestion.setExamChildren(Collections.singletonList(trueOrFalse));
                break;
            case BIG_QUESTION_EXAM_CHILDREN_TYPE_DESIGN:
                Design design = JSON.parseObject(jsonObject.toJSONString(), Design.class);
                design.setTeacherId(teacherId);
                bigQuestion.setExamChildren(Collections.singletonList(design));
                break;
            default:
                throw new ExamQuestionsException("非法参数 错误的题目类型");
        }
        setBigQuestionUUID(bigQuestion);
        update.push("examChildren", bigQuestion.getExamChildren().get(0));
        return reactiveMongoTemplate.updateFirst(query, update, BigQuestion.class).map(Objects::nonNull);
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

}

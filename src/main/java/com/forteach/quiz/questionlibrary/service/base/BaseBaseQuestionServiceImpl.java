package com.forteach.quiz.questionlibrary.service.base;

import com.alibaba.fastjson.JSON;
import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.domain.ExerciseBook;
import com.forteach.quiz.domain.QuestionBank;
import com.forteach.quiz.exceptions.CustomException;
import com.forteach.quiz.exceptions.ExamQuestionsException;
import com.forteach.quiz.exceptions.ProblemSetException;
import com.forteach.quiz.questionlibrary.domain.QuestionExamEntity;
import com.forteach.quiz.questionlibrary.reflect.QuestionReflect;
import com.forteach.quiz.questionlibrary.repository.base.QuestionMongoRepository;
import com.forteach.quiz.questionlibrary.service.KeywordService;
import com.forteach.quiz.web.req.QuestionBankReq;
import com.forteach.quiz.web.vo.KeywordIncreaseVo;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.forteach.quiz.common.Dic.*;
import static com.forteach.quiz.util.StringUtil.isNotEmpty;

/**
 * @Description: Question 结构的通用实现
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/10  11:53
 */
public abstract class BaseBaseQuestionServiceImpl<T extends QuestionExamEntity> implements BaseQuestionService<T> {

    protected final ReactiveMongoTemplate reactiveMongoTemplate;
    private final QuestionMongoRepository<T> repository;
    private final KeywordService<T> keywordService;
    private final QuestionReflect questionReflect;

    public BaseBaseQuestionServiceImpl(QuestionMongoRepository<T> repository, KeywordService<T> keywordService,
                                       ReactiveMongoTemplate reactiveMongoTemplate, QuestionReflect questionReflect) {
        this.repository = repository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.questionReflect = questionReflect;
        this.keywordService = keywordService;
    }

    /**
     * 修改题目
     *
     * @param bigQuestion
     * @return
     */
    @Override
    public Mono<T> editQuestion(final T bigQuestion, final Class obj) {
        return editQuestions(setQuestionUUID(bigQuestion, obj)).flatMap(t -> {
            Mono<UpdateResult> questionBankMono = questionBankAssociation(t.getId(), t.getTeacherId());
            return questionBankMono.flatMap(
                    updateResult -> {
                        if (updateResult.isModifiedCountAvailable()) {
                            return Mono.just(t);
                        } else {
                            return Mono.error(new ExamQuestionsException("保存 题目 作者失败"));
                        }
                    }
            );
        });
    }

    /**
     * 修改是是否更新到课后练习册
     *
     * @param bigQuestion
     * @return
     */
    @Override
    public Mono<T> editQuestions(final T bigQuestion) {
        bigQuestion.setUDate(new Date());
        if (bigQuestion.getRelate() == COVER_QUESTION_BANK) {
            return editQuestionsCover(bigQuestion);
        }
        return repository.save(bigQuestion);
    }

    /**
     * 设置问题id
     *
     * @param bigQuestion
     * @return
     */
    private T setQuestionUUID(final T bigQuestion, final Class obj) {
        bigQuestion.setExamChildren((List) bigQuestion.getExamChildren()
                .stream()
                .peek(question -> {
                    questionReflect.buildAttribute(JSON.parseObject(JSON.toJSONString(question), obj));
                })
                .collect(Collectors.toList()));
        return bigQuestion;
    }

    /**
     * 修改课后练习册
     *
     * @param bigQuestion
     * @return
     */
    private Mono<T> editQuestionsCover(final T bigQuestion) {

        Query query = Query.query(Criteria.where(QUESTION_CHILDREN + "." + MONGDB_ID).is(bigQuestion.getId()));
        Update update = new Update();
        update.set("questionChildren.$.paperInfo", bigQuestion.getPaperInfo());
        update.set("questionChildren.$.examChildren", bigQuestion.getExamChildren());
        update.set("questionChildren.$.type", bigQuestion.getType());
        update.set("questionChildren.$.score", bigQuestion.getScore());
        return reactiveMongoTemplate.updateMulti(query, update, ExerciseBook.class).map(UpdateResult::getMatchedCount).flatMap(obj -> {
            if (obj != -1) {
                return repository.save(bigQuestion);
            } else {
                return Mono.error(new ProblemSetException("更新失败"));
            }
        });

    }

    /**
     * 更新题目与教师关系信息
     *
     * @param questionBankId
     * @param teacherId
     * @return
     */
    @Override
    public Mono<UpdateResult> questionBankAssociation(final String questionBankId, final String teacherId) {
        return reactiveMongoTemplate.upsert(Query.query(Criteria.where(MONGDB_ID).is(questionBankId)), new Update().addToSet(MONGDB_COLUMN_QUESTION_BANK_TEACHER, teacherId), QuestionBank.class);
    }

    /**
     * 题目分享
     *
     * @param questionBankId
     * @param teacherId
     * @return
     */
    @Override
    public Mono<Boolean> questionBankAssociationAdd(final String questionBankId, final String teacherId) {
        return questionBankAssociation(questionBankId, teacherId).map(UpdateResult::isModifiedCountAvailable);
    }

    /**
     * 获取泛型的class
     *
     * @return
     */
    private Class<T> entityClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * 删除题目关系
     *
     * @param id
     * @return
     */
    private Mono<DeleteResult> delBankAssociation(final List<String> id) {
        return reactiveMongoTemplate.remove(Query.query(Criteria.where(MONGDB_ID).is(id)), entityClass());
    }

    /**
     * 删除单道题
     *
     * @param id
     * @return
     */
    @Override
    public Mono<Void> delQuestions(final String id) {
        return repository.deleteById(id)
                .and(delBankAssociation(Collections.singletonList(id)));
    }

    /**
     * 查询详细或全部字段的问题
     *
     * @param sortVo
     * @return
     */
    @Override
    public Flux<T> findAllDetailed(final QuestionBankReq sortVo) {

        if (PARAMETER_PART.equals(sortVo.getAllOrPart())) {
            return findPartQuestion(sortVo);
        } else if (PARAMETER_ALL.equals(sortVo.getAllOrPart())) {
            return findAllQuestion(sortVo);
        }
        return Flux.error(new ExamQuestionsException("错误的查询条件"));
    }

    /**
     * 查询问题,只返回部分字段
     *
     * @param sortVo
     * @return
     */
    private Flux<T> findPartQuestion(final QuestionBankReq sortVo) {

        Query query = buildFindQuestion(sortVo);

        query.fields()
                .include(MONGDB_ID)
                .include("chapterId")
                .include("levelId")
                .include("knowledgeId")
                .include("examType")
                .include("teacherId")
                .include("uDate");

        sortVo.queryPaging(query);

        return reactiveMongoTemplate.find(query, entityClass());
    }

    /**
     * 查询所有详细的问题
     *
     * @param sortVo
     * @return
     */
    private Flux<T> findAllQuestion(final QuestionBankReq sortVo) {

        Query query = buildFindQuestion(sortVo);

        sortVo.queryPaging(query);

        return reactiveMongoTemplate.find(query, entityClass());
    }

    /**
     * 构建查询问题条件
     *
     * @param sortVo
     * @return
     */
    private Query buildFindQuestion(final QuestionBankReq sortVo) {

        Criteria criteria = Criteria.where("teacherId").is(sortVo.getOperatorId());

        if (isNotEmpty(sortVo.getLevelId())) {
            criteria.and("levelId").in(sortVo.getLevelId());
        }
        if (isNotEmpty(sortVo.getChapterId())) {
            criteria.and("chapterId").in(sortVo.getChapterId());
        }
        if (isNotEmpty(sortVo.getKnowledgeId())) {
            criteria.and("knowledgeId").in(sortVo.getKnowledgeId());
        }
        if (isNotEmpty(sortVo.getQuestionType())) {
            criteria.and("examChildren.examType").in(sortVo.getQuestionType());
        }
        if (sortVo.getKeyword() != null && sortVo.getKeyword().length > 0) {
            criteria.and("keyword").all(sortVo.getKeyword());
        }

        return new Query(criteria);
    }

    /**
     * 根据id查询详细
     *
     * @param id
     * @return
     */
    @Override
    public Mono<T> findOneDetailed(final String id) {
        return repository.findById(id).switchIfEmpty(Mono.error(new CustomException("没有找到考题")));
    }

    /**
     * 增加关键字关联关系
     *
     * @return
     */
    @ApiOperation(value = "增加关键字关联关系", notes = "增加关键字关联关系")
    @PostMapping("/keyword/increase")
    public Mono<WebResult> increase(@ApiParam(value = "增加关键字关联关系", required = true) @RequestBody KeywordIncreaseVo increaseVo) {
        return keywordService.increase(increaseVo.getValue(), increaseVo.getBigQuestionId()).map(WebResult::okResult);
    }

    /**
     * 删除关键字
     *
     * @return
     */
    @ApiOperation(value = "删除关键字", notes = "删除关键字")
    @PostMapping("/keyword/undock")
    public Mono<WebResult> undock(@ApiParam(value = "删除关键字", required = true) @RequestBody KeywordIncreaseVo increaseVo) {
        return keywordService.undock(increaseVo.getValue(), increaseVo.getBigQuestionId()).map(WebResult::okResult);
    }

    /**
     * 查询关键字下id问题
     *
     * @return
     */
    @ApiOperation(value = "查询关键字下问题id", notes = "查询关键字下问题id")
    @PostMapping("/keyword/associated")
    public Mono<WebResult> associated(@ApiParam(value = "查询知识点下问题id", required = true) @RequestBody KeywordIncreaseVo increaseVo) {
        return keywordService.keywordQuestion(increaseVo.getValue()).collectList().map(WebResult::okResult);
    }
}

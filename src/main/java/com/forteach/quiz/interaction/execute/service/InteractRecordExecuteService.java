package com.forteach.quiz.interaction.execute.service;

import com.forteach.quiz.interaction.execute.domain.*;
import com.forteach.quiz.interaction.execute.dto.*;
import com.forteach.quiz.interaction.execute.repository.InteractRecordRepository;
import com.forteach.quiz.interaction.execute.web.vo.InteractiveSheetVo;
import com.forteach.quiz.interaction.execute.web.vo.MoreGiveVo;
import com.forteach.quiz.service.StudentsService;
import com.mongodb.client.result.UpdateResult;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.forteach.quiz.common.Dic.QUESTION_ACCURACY_FALSE;
import static com.forteach.quiz.common.Dic.QUESTION_ACCURACY_TRUE;
import static com.forteach.quiz.util.DateUtil.getEndTime;
import static com.forteach.quiz.util.DateUtil.getStartTime;

/**
 * @Description: 课堂交互记录数据相关
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/4  15:33
 */
@Service
public class InteractRecordExecuteService {

    private final InteractRecordRepository repository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final StudentsService studentsService;
    private Mono<Boolean> executeSuccessfully = Mono.just(true);

    public InteractRecordExecuteService(InteractRecordRepository repository, ReactiveMongoTemplate mongoTemplate, StudentsService studentsService) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
        this.studentsService = studentsService;
    }

    /*------------------------------对回答的记录进行保存--------------------------------*/
    /**
     * 学生回答问题时 加入记录
     *
     * @param circleId
     * @param questionId
     * @param studentId
     * @param answer
     * @param right
     * @return
     */
    public Mono<Boolean> answer(final String circleId, final String questionId, final String studentId, final String answer, final String right) {

        final Query query = Query.query(Criteria.where("circleId").is(circleId)
                .and("questions.questionsId").is(questionId)
                .and("questions.answerRecordList.examineeId").ne(studentId))
                .with(new Sort(Sort.Direction.DESC, "index")).limit(1);

        Update update = new Update();

        update.inc("questions.$.answerNumber", 1);

        if (QUESTION_ACCURACY_TRUE.equals(right)) {
            update.inc("questions.$.correctNumber", 1);
        } else if (QUESTION_ACCURACY_FALSE.equals(right)) {
            update.inc("questions.$.errorNumber", 1);
        }

        update.push("questions.$.answerRecordList", new InteractAnswerRecord(studentId, answer, right));

        return mongoTemplate.updateMulti(query, update, InteractRecord.class).map(Objects::nonNull);
    }

    public Mono<Boolean> pushMongo(final InteractiveSheetVo sheetVo, final String interactRecordType){
        final Query query = Query.query(Criteria.where("circleId").is(sheetVo.getCircleId())
                .and(interactRecordType + ".questionsId").is(sheetVo.getAnsw().getQuestionId())
                .and(interactRecordType + ".answerRecordList.examineeId").ne(sheetVo.getExamineeId()))
                .with(new Sort(Sort.Direction.DESC, "index")).limit(1);
        Update update = new Update();
        update.push(interactRecordType + ".$.answerRecordList", new InteractAnswerRecord(sheetVo.getExamineeId(), sheetVo.getAnsw().getAnswer()));
        return mongoTemplate.updateMulti(query, update, InteractRecord.class).thenReturn(true);
    }

    /*------------------------发布问题时加入记录，有就用原来，没有经新建一条记录进行保存-------------------------------*/

    /**
     * 记录习题册
     * @param giveVo
     * @return
     */
    public Publisher<Boolean> interactiveBook(final MoreGiveVo giveVo) {
        Mono<Long> number = exerciseBookNumber(giveVo.getCircleId());
        Mono<InteractRecord> recordMono = findexerciseBooks(giveVo.getCircleId(), giveVo.getQuestionId());
        return Mono.zip(number, recordMono).flatMap(tuple2 -> {

            if (tuple2.getT2().getQuestions() != null && tuple2.getT2().getQuestions().size() > 0) {
                return upInteractInteractRecord(giveVo.getSelected(), tuple2.getT2().getQuestions().get(0).getSelectId(), giveVo.getCircleId(), giveVo.getQuestionId(), giveVo.getCategory(), "exerciseBooks");
            } else {
                return pushExerciseBook(giveVo.getSelected(), tuple2.getT1(), giveVo.getCircleId(), giveVo.getQuestionId());
            }
        }).map(Objects::nonNull);
    }

    /**
     * 发布问题时 加入记录
     * @param circleId
     * @param questionId
     * @param selectId
     * @param category
     * @return
     */
    public Mono<Boolean> releaseQuestion(final String circleId, final String questionId, final String selectId, final String category, final String interactive) {

        Mono<Long> number = questionNumber(circleId);

        Mono<InteractRecord> recordMono = findInteractQuestionsRecord(circleId, questionId, category, interactive);

        return Mono.zip(number, recordMono).flatMap(tuple2 -> {

            if (tuple2.getT2().getQuestions() != null && tuple2.getT2().getQuestions().size() > 0) {
                return upInteractQuestions(selectId, tuple2.getT2().getQuestions().get(0).getSelectId(), circleId, questionId, category, interactive);
            } else {
                return pushInteractQuestions(selectId, circleId, questionId, tuple2.getT1(), interactive, category);
            }
        }).map(Objects::nonNull);
    }

    public Mono<Boolean> releaseInteractRecord(final String circleId, final String questionId, final String selectId, final String category, final String interactRecordType) {

        Mono<Long> number = questionNumber(circleId);

        Mono<InteractRecord> recordMono = findInteractInteractRecord(circleId, questionId, category, interactRecordType);

        return Mono.zip(number, recordMono).flatMap(tuple2 -> {

            if (tuple2.getT2().getQuestions() != null && tuple2.getT2().getQuestions().size() > 0
                    || tuple2.getT2().getSurveys() != null && tuple2.getT2().getSurveys().size() > 0
                    || tuple2.getT2().getBrainstorms() != null && tuple2.getT2().getBrainstorms().size() > 0
                    || tuple2.getT2().getInteracts() != null && tuple2.getT2().getInteracts().size() > 0) {
                return upInteractInteractRecord(selectId, tuple2.getT2().getQuestions().get(0).getSelectId(), circleId, questionId, category, interactRecordType);
            } else if ("surveys".equals(interactRecordType)) {
                return pushInteractSurveys(selectId, circleId, questionId, tuple2.getT1(), category);
            } else if ("interacts".equals(interactRecordType)) {
                return pushInteractTask(selectId, circleId, questionId, tuple2.getT1(), category);
            } else if ("brainstorms".equals(interactRecordType)) {
                return pushInteractBrainstorms(selectId, circleId, questionId, tuple2.getT1(), category);
            } else {
                return pushInteractQuestions(selectId, circleId, questionId, tuple2.getT1(), "", category);
            }
        }).map(Objects::nonNull);
    }
    /*-----------------------------------------------------------*/
    /**
     * 学生加入互动课堂时存入记录
     *
     * @param circleId
     * @param student
     * @return
     */
    public Mono<Boolean> join(final String circleId, final String student) {

        final Query query = Query.query(Criteria.where("circleId").is(circleId).and("students").ne(student));

        Update update = new Update();
        update.addToSet("students", student);
        update.inc("participate", 1);

        return mongoTemplate.findAndModify(query, update, InteractRecord.class).switchIfEmpty(Mono.just(new InteractRecord())).map(Objects::nonNull);
    }

    /**
     * 学生举手时记录
     *
     * @param circleId
     * @param student
     * @param questionId
     * @return
     */
    public Mono<Boolean> raiseHand(final String circleId, final String student, final String questionId) {

        final Query query = Query.query(
                Criteria.where("circleId").is(circleId).and("questions.raiseHandsId").ne(student).and("questions.questionsId").is(questionId)
        ).with(new Sort(Sort.Direction.DESC, "index")).limit(1);

        Update update = new Update();
        update.addToSet("questions.$.raiseHandsId", student);
        update.inc("questions.$.raiseHandsNumber", 1);

        return mongoTemplate.findAndModify(query, update, InteractRecord.class).switchIfEmpty(Mono.just(new InteractRecord())).map(Objects::nonNull);
    }


    /**
     * 创建课堂时,进行初始化记录创建记录
     *
     * @return
     */
    public Mono<Boolean> init(final String circleId, final String teacherId) {

        return repository.findByCircleIdAndTeacherId(circleId, teacherId).collectList()
                .flatMap(list -> {
                    if (list != null && list.size() != 0) {
                        return executeSuccessfully;
                    } else {
                        return build(circleId, teacherId).map(Objects::nonNull);
                    }
                });
    }

    /**
     * 构建上课信息时默认见天上课次数为 1， 并进行记录
     * @param circleId
     * @param teacherId
     * @return
     */
    private Mono<InteractRecord> build(final String circleId, final String teacherId) {
        return todayNumber(teacherId)
                .flatMap(number ->
                        repository.save(new InteractRecord(circleId, teacherId, number + 1L)));
    }

    /**
     * 获取今日上课次数
     *
     * @return
     */
    private Mono<Long> todayNumber(final String teacherId) {
        return mongoTemplate.count(Query.query(nowRecord(teacherId)), InteractRecord.class).switchIfEmpty(Mono.just(0L));
    }

    /*-----------------------------获取发布次数-----------------------------------*/
    /**
     * 获取本次课堂发布问题次数
     *
     * @return
     */
    private Mono<Long> questionNumber(final String circleId) {
        return mongoTemplate.count(Query.query(Criteria.where("circleId").is(circleId).and("questions.questionsId").ne("").ne(null)), InteractRecord.class).switchIfEmpty(Mono.just(0L));
    }

    /**
     * 计算习题册发布的次数
     * @param circleId
     * @return
     */
    private Mono<Long> exerciseBookNumber(final String circleId){
        return mongoTemplate.count(Query.query(
                Criteria.where("circleId").is(circleId)
                        .and("exerciseBooks.questionsId").ne("").ne(null)),
                InteractRecord.class).switchIfEmpty(Mono.just(0L));
    }

    /**
     * 获取今天最新的课堂记录
     *
     * @param teacherId
     * @return
     */
    private Criteria nowRecord(final String teacherId) {
        return Criteria.where("teacherId").is(teacherId).and("time").gte(getStartTime()).lte(getEndTime());
    }

    /**
     * 获得发布的问题
     *
     * @param circleId
     * @param questionId
     * @param category
     * @param interactive
     * @return
     */
    private Mono<InteractRecord> findInteractQuestionsRecord(final String circleId, final String questionId, final String category, final String interactive) {
        return mongoTemplate
                .findOne(buildLastQuestionsRecord(circleId, questionId, category, interactive), InteractRecord.class)
                .switchIfEmpty(Mono.just(new InteractRecord()));
    }

    private Mono<InteractRecord> findexerciseBooks(final String circleId, final String questionId) {
        return mongoTemplate
                .findOne(buildexerciseBooks(circleId, questionId), InteractRecord.class)
                .switchIfEmpty(Mono.just(new InteractRecord()));
    }

    private Mono<InteractRecord> findInteractInteractRecord(final String circleId, final String questionId, final String category, final String interactRecordType) {
        return mongoTemplate
                .findOne(buildLastInteractRecord(circleId, questionId, category, interactRecordType), InteractRecord.class)
                .switchIfEmpty(Mono.just(new InteractRecord()));
    }

    /*---------------------------构建回答的记录对像--------------------------------*/
    /**
     * 获取新的发布问题(指定问题id)
     *
     * @param circleId
     * @param questionId
     * @param category
     * @param interactive
     * @return
     */
    private Query buildLastQuestionsRecord(final String circleId, final String questionId, final String category, final String interactive) {

        final Query query = Query.query(
                Criteria.where("circleId").is(circleId)
                        .and("questions.questionsId").is(questionId)
                        .and("questions.interactive").is(interactive)
                        .and("questions.category").is(category)
        ).with(new Sort(Sort.Direction.DESC, "index")).limit(1);

        query.fields().include("questions");

        return query;
    }

    private Query buildexerciseBooks(final String circleId, final String questionId) {
        final Query query = Query.query(
                Criteria.where("circleId").is(circleId)
                        .and("exerciseBooks.questionsId").is(questionId)
        ).with(new Sort(Sort.Direction.DESC, "index")).limit(1);
        query.fields().include("exerciseBooks");
        return query;
    }

    /**
     * 获取新的发布问题(指定问题id)
     *
     * @param circleId
     * @param questionId
     * @param category
     * @return
     */
    private Query buildLastInteractRecord(final String circleId, final String questionId, final String category, String interactType) {

        final Query query = Query.query(Criteria.where("circleId")
                        .is(circleId).and(interactType + ".questionsId").is(questionId)
                        .and(interactType + ".category").is(category)
        ).with(new Sort(Sort.Direction.DESC, "index")).limit(1);
        query.fields().include(interactType);
        return query;
    }

    /*------------------------- 更新发布的问题--------------------------*/
    /**
     * 更新发布的问题
     *
     * @param selectId
     * @param tSelectId
     * @param circleId
     * @param questionId
     * @param category
     * @param interactive
     * @return
     */
    private Mono<UpdateResult> upInteractQuestions(final String selectId, final List<String> tSelectId, final String circleId, final String questionId, final String category, final String interactive) {
        Query query = buildLastQuestionsRecord(circleId, questionId, category, interactive);
        Update update = new Update();
        List<String> list = Arrays.asList(selectId.split(","));
        update.set("questions.$.selectId", list);
        if (!list.equals(tSelectId)) {
            update.inc("questions.$.number", 1);
        }
        return mongoTemplate.updateMulti(query, update, InteractRecord.class);
    }

    private Mono<UpdateResult> upInteractInteractRecord(final String selectId, final List<String> tSelectId, final String circleId, final String questionId, final String category,final String interactRecord) {
        Query query = buildLastInteractRecord(circleId, questionId, category, interactRecord);
        Update update = new Update();
        List<String> list = Arrays.asList(selectId.split(","));
        update.set(interactRecord + ".$.selectId", list);
        if (!list.equals(tSelectId)) {
            update.inc(interactRecord + ".$.number", 1);
        }
        return mongoTemplate.updateMulti(query, update, InteractRecord.class);
    }

    /*---------------------------- 发布记录 -----------------------*/
    /**
     * push一条新的发布问题记录
     *
     * @param selectId
     * @param circleId
     * @param questionId
     * @param number
     * @param interactive
     * @param category
     * @return
     */
    private Mono<UpdateResult> pushInteractQuestions(final String selectId, final String circleId, final String questionId, final Long number, final String interactive, final String category) {
        Query query = Query.query(Criteria.where("circleId").is(circleId));
        Update update = new Update();
        //学生编号id 进行,分割
        InteractQuestionsRecord records = new InteractQuestionsRecord(questionId, number + 1, interactive, category, Arrays.asList(selectId.split(",")));
        update.push("questions", records);
        return mongoTemplate.updateMulti(query, update, InteractRecord.class);
    }

    private Mono<UpdateResult> pushExerciseBook(final String selectId, final Long number, final String circleId, final String questionId) {
        Query query = Query.query(Criteria.where("circleId").is(circleId));
        Update update = new Update();
        //学生编号id 进行,分割
        InteractQuestionsRecord records = new InteractQuestionsRecord(questionId, number + 1, Arrays.asList(selectId.split(",")));
        update.push("exerciseBooks", records);
        return mongoTemplate.updateMulti(query, update, InteractRecord.class);
    }

    private Mono<UpdateResult> pushInteractBrainstorms(final String selectId, final String circleId, final String questionId, final Long number, final String category) {
        Query query = Query.query(Criteria.where("circleId").is(circleId));
        Update update = new Update();
        //学生编号id 进行,分割
        BrainstormInteractRecord records = new BrainstormInteractRecord(questionId, number + 1, category, Arrays.asList(selectId.split(",")));
        update.push("brainstorms", records);
        return mongoTemplate.updateMulti(query, update, InteractRecord.class);
    }

    private Mono<UpdateResult> pushInteractSurveys(final String selectId, final String circleId, final String questionId, final Long number, final String category) {
        Query query = Query.query(Criteria.where("circleId").is(circleId));
        Update update = new Update();
        //学生编号id 进行,分割
        SurveyInteractRecord records = new SurveyInteractRecord(questionId, number + 1, category, Arrays.asList(selectId.split(",")));
        update.push("surveys", records);
        return mongoTemplate.updateMulti(query, update, InteractRecord.class);
    }

    private Mono<UpdateResult> pushInteractTask(final String selectId, final String circleId, final String questionId, final Long number, final String category) {
        Query query = Query.query(Criteria.where("circleId").is(circleId));
        Update update = new Update();
        //学生编号id 进行,分割
        TaskInteractRecord records = new TaskInteractRecord(questionId, number + 1, category, Arrays.asList(selectId.split(",")));
        update.push("interacts", records);
        return mongoTemplate.updateMulti(query, update, InteractRecord.class);
    }

    /*---------------------------------- 查询记录 ------------------------------------------*/
    /**
     * 根据条件查询对应的questions 任务记录
     * @param circleId 课堂id
     * @return Flux<List<InteractQuestionsRecord>>
     */
    public Mono<InteractQuestionsRecord> findQuestionsRecord(final String circleId, final String questionsId) {
        return repository.findRecordByCircleIdAndQuestionsId(circleId, questionsId)
                .filter(Objects::nonNull)
                .map(QuestionsDto::getQuestions)
                .filter(list -> list != null && list.size() > 0)
                .flatMapMany(Flux::fromIterable)
                .filter(interactQuestionsRecord -> questionsId.equals(interactQuestionsRecord.getQuestionsId()))
                .last()
                .onErrorReturn(new InteractQuestionsRecord());
    }

    /**
     * 问卷调查记录
     * @param circleId
     * @param questionsId
     * @return
     */
    public Mono<SurveyInteractRecord> findSurveyRecord(final String circleId, final String questionsId){
        return repository.findRecordSurveysByCircleIdAndQuestionsId(circleId, questionsId)
                .filter(Objects::nonNull)
                .map(SurveysDto::getSurveys)
                .filter(list -> list != null && list.size() > 0)
                .flatMapMany(Flux::fromIterable)
                .filter(surveyInteractRecord -> questionsId.equals(surveyInteractRecord.getQuestionsId()))
                .last()
                .onErrorReturn(new SurveyInteractRecord());
    }

    /**
     * 任务记录
     * @param circleId
     * @param questionsId
     * @return
     */
    public Mono<TaskInteractRecord> findTaskRecord(final String circleId, final String questionsId) {
        return repository.findRecordTaskByCircleIdAndQuestionsId(circleId, questionsId)
                .filter(Objects::nonNull)
                .map(TaskInteractDto::getInteracts)
                .filter(list -> list != null && list.size() > 0)
                .flatMapMany(Flux::fromIterable)
                .filter(taskInteractRecord -> questionsId.equals(taskInteractRecord.getQuestionsId()))
                .last()
                .onErrorReturn(new TaskInteractRecord());
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

    public Mono<InteractQuestionsRecord> findExerciseBookRecord(final String circleId, final String questionsId) {
        return repository.findExerciseBooksByCircleIdAndQuestionsId(circleId, questionsId)
                .filter(Objects::nonNull)
                .map(ExerciseBooksDto::getExerciseBooks)
                .filter(list -> list != null && list.size() > 0)
                .flatMapMany(Flux::fromIterable)
                .filter(interactQuestionsRecord -> questionsId.equals(interactQuestionsRecord.getQuestionsId()))
                .last()
                .onErrorReturn(new InteractQuestionsRecord());
    }
}

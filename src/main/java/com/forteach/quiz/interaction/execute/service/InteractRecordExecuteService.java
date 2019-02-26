package com.forteach.quiz.interaction.execute.service;

import com.forteach.quiz.interaction.execute.domain.InteractAnswerRecord;
import com.forteach.quiz.interaction.execute.domain.InteractQuestionsRecord;
import com.forteach.quiz.interaction.execute.domain.InteractRecord;
import com.forteach.quiz.interaction.execute.dto.QuestionsDto;
import com.forteach.quiz.interaction.execute.repository.InteractRecordRepository;
import com.forteach.quiz.interaction.execute.web.resp.InteractAnswerRecordResp;
import com.forteach.quiz.service.StudentsService;
import com.forteach.quiz.util.StringUtil;
import com.forteach.quiz.web.pojo.Students;
import com.mongodb.client.result.UpdateResult;
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

        final Query query = Query.query(Criteria.where("circleId").is(circleId).and("questions.questionsId").is(questionId).and("questions.answerRecordList.examineeId").ne(studentId)).with(new Sort(Sort.Direction.DESC, "index")).limit(1);

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

    /**
     * 发布问题时 加入记录
     *
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

    private Mono<InteractRecord> build(final String circleId, final String teacherId) {

        return todayNumber(teacherId).flatMap(number -> repository.save(new InteractRecord(circleId, teacherId, number + 1L)));
    }

    /**
     * 获取今日上课次数
     *
     * @return
     */
    private Mono<Long> todayNumber(final String teacherId) {
        return mongoTemplate.count(Query.query(nowRecord(teacherId)), InteractRecord.class).switchIfEmpty(Mono.just(0L));
    }

    /**
     * 获取本次课堂发布问题次数
     *
     * @return
     */
    private Mono<Long> questionNumber(final String circleId) {
        return mongoTemplate.count(Query.query(Criteria.where("circleId").is(circleId).and("questions.questionsId").ne("").ne(null)), InteractRecord.class).switchIfEmpty(Mono.just(0L));
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
        return mongoTemplate.findOne(buildLastQuestionsRecord(circleId, questionId, category, interactive), InteractRecord.class).switchIfEmpty(Mono.just(new InteractRecord()));
    }

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
        InteractQuestionsRecord records = new InteractQuestionsRecord(questionId, number + 1, interactive, category, Arrays.asList(selectId.split(",")));
        update.push("questions", records);
        return mongoTemplate.updateMulti(query, update, InteractRecord.class);
    }

    /**
     * 根据条件查询对应的questions 任务记录
     * @param circleId 课堂id
     * @return Flux<List<InteractQuestionsRecord>>
     */
    public Mono<InteractQuestionsRecord> findQuestionsRecord(final String circleId, final String questionsId) {
        if(StringUtil.isNotEmpty(circleId) && StringUtil.isNotEmpty(questionsId)) {
            return repository.findRecordByCircleIdAndQuestionsId(circleId, questionsId)
                    .filter(Objects::nonNull)
                    .map(QuestionsDto::getQuestions)
                    .filter(list -> list != null && list.size() > 0)
                    .flatMapMany(Flux::fromIterable)
                    .filter(interactQuestionsRecord -> questionsId.equals(interactQuestionsRecord.getQuestionsId()))
                    .last();
        }
        return Mono.empty();
    }
}

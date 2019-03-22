package com.forteach.quiz.interaction.execute.service.record;

import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.interaction.execute.domain.record.InteractAnswerRecord;
import com.forteach.quiz.interaction.execute.domain.record.InteractRecord;
import com.forteach.quiz.interaction.execute.repository.InteractRecordRepository;
import com.forteach.quiz.interaction.execute.web.resp.InteractAnswerRecordResp;
import com.forteach.quiz.interaction.execute.web.resp.InteractRecordResp;
import com.forteach.quiz.service.StudentsService;
import com.forteach.quiz.web.pojo.Students;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static com.forteach.quiz.common.Dic.INTERACT_RECORD_QUESTIONS;
import static com.forteach.quiz.common.Dic.MONGDB_ID;
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

    public InteractRecordExecuteService(InteractRecordRepository repository, ReactiveMongoTemplate mongoTemplate,
                                        StudentsService studentsService) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
        this.studentsService = studentsService;
    }
    /**
     * 学生加入互动课堂时存入记录
     *
     * @param circleId
     * @param student
     * @return
     */
    public Mono<Boolean> join(final String circleId, final String student) {

        final Query query = Query.query(Criteria.where(MONGDB_ID).is(circleId).and("students").ne(student));

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
                Criteria.where(MONGDB_ID).is(circleId).and(INTERACT_RECORD_QUESTIONS.concat(".raiseHandsId")).ne(student).and(INTERACT_RECORD_QUESTIONS.concat(".questionsId")).is(questionId)
        ).with(new Sort(Sort.Direction.DESC, "index")).limit(1);

        Update update = new Update();
        update.addToSet(INTERACT_RECORD_QUESTIONS.concat(".$.raiseHandsId"), student);
        update.inc(INTERACT_RECORD_QUESTIONS.concat(".$.raiseHandsNumber"), 1);

        return mongoTemplate.findAndModify(query, update, InteractRecord.class).switchIfEmpty(Mono.just(new InteractRecord())).map(Objects::nonNull);
    }

    public Mono<String> init(final String teacherId) {
        //获得课堂的交互情况 学生回答情况，如果存在返回true，否则创建mongo的课堂信息
        return Mono.just(teacherId).flatMap(id ->build(id).flatMap(item -> {
            MyAssert.isNull(item, DefineCode.ERR0012, "创建互动课堂失败");
            MyAssert.blank(item.getId(), DefineCode.ERR0012, "创建互动课堂失败");
            return Mono.just(item.getId());
        }));
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
     * 创建Mongo课堂信息
     * @param teacherId
     * @return
     */
    private Mono<InteractRecord> build(final String teacherId) {
        return todayNumber(teacherId).flatMap(number -> repository.save(new InteractRecord(teacherId, number + 1L)));
    }

    /**
     * 获取今日上课次数
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
    Mono<Long> questionNumber(final String circleId) {
        return mongoTemplate.count(Query.query(Criteria.where(MONGDB_ID).is(circleId).and(INTERACT_RECORD_QUESTIONS.concat(".questionsId")).ne("").ne(null)), InteractRecord.class).switchIfEmpty(Mono.just(0L));
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
     * 查询或者新建个记录对象
     * @param circleId
     * @param questionId
     * @param category
     * @param interactRecordType
     * @return
     */
    Mono<InteractRecord> findInteractInteractRecord(final String circleId, final String questionId, final String category, final String interactRecordType) {
        return mongoTemplate
                .findOne(buildLastInteractRecord(circleId, questionId, category, interactRecordType), InteractRecord.class)
                .switchIfEmpty(Mono.just(new InteractRecord()));
    }

    /**
     * 获取新的发布问题(指定问题id)
     *
     * @param circleId
     * @param questionId
     * @param category
     * @return
     */
    Query buildLastInteractRecord(final String circleId, final String questionId, final String category, final String interactType) {
        final Query query = Query.query(Criteria.where(MONGDB_ID)
                .is(circleId).and(interactType + ".questionsId").is(questionId)
                .and(interactType + ".category").is(category)
        ).with(new Sort(Sort.Direction.DESC, "index")).limit(1);
        query.fields().include(interactType);
        return query;
    }

    /**
     * 将问题记录转换为对应的对象
     * @param answerRecord
     * @param students
     * @return
     */
    private Mono<InteractAnswerRecordResp> builderResp(final InteractAnswerRecord answerRecord, final Students students){
        return Mono.just(InteractAnswerRecordResp.builder()
                .student(students)
                .time(answerRecord.getTime())
                .right(answerRecord.getRight())
                .answer(answerRecord.getAnswer())
                .build());
    }

    /**
     * 查询对应的记录列表
     * @param answerRecordList
     * @return
     */
    Mono<List<InteractAnswerRecordResp>> answerRecordList(final List<InteractAnswerRecord> answerRecordList){
        if (answerRecordList != null) {
            return Mono.just(answerRecordList)
                    .filter(Objects::nonNull)
                    .flatMapMany(Flux::fromIterable)
                    .filter(Objects::nonNull)
                    .concatMap(a -> {
                        return studentsService.findStudentsBrief(a.getExamineeId())
                                .flatMap(s -> builderResp(a, s));
                    }).collectList();
        }else {
            return Mono.just(new ArrayList<>());
        }
    }

    /**
     * 转换请求对像
     * @param selectId
     * @param index
     * @param time
     * @param number
     * @param category
     * @param answerNumber
     * @param questionId
     * @param answerRecordList
     * @return
     */
    public Mono<InteractRecordResp> changeRecordResp(List<String> selectId, Integer index, String time, Integer number, String category, Integer answerNumber, String questionId, List<InteractAnswerRecord> answerRecordList){
        return Mono.just(selectId)
                .zipWith(filterStudents(selectId), (s, studentsList) ->
                        InteractRecordResp.builder()
                                .index(index)
                                .time(time)
                                .number(number)
                                .category(category)
                                .answerNumber(answerNumber)
                                .questionsId(questionId)
                                .students(studentsList)
                                .build())
                .zipWith(answerRecordList(answerRecordList), (interactRecordResp, interactAnswerRecordRespList) -> {
                    if (interactAnswerRecordRespList != null && interactAnswerRecordRespList.size() > 0) {
                        interactRecordResp.setAnswerRecordList(interactAnswerRecordRespList);
                    }
                    return interactRecordResp;
                });
    }

    /**
     * 过滤学生信息
     * @param strings
     * @return
     */
    Mono<List<Students>> filterStudents(final List<String> strings){
        if (strings != null && strings.size() > 0) {
            return studentsService.exchangeStudents(strings);
        }
        return Mono.just(new ArrayList<>());
    }
}

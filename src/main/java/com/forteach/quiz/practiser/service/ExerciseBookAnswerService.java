package com.forteach.quiz.practiser.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.interaction.execute.service.Key.SingleQueKey;
import com.forteach.quiz.practiser.domain.AnswerGrade;
import com.forteach.quiz.practiser.domain.AskAnswerExerciseBook;
import com.forteach.quiz.practiser.domain.AskAnswerGrade;
import com.forteach.quiz.practiser.domain.AskAnswerStudents;
import com.forteach.quiz.practiser.web.req.AnswerReq;
import com.forteach.quiz.practiser.web.req.FindAnswerGradeReq;
import com.forteach.quiz.practiser.web.req.FindAnswerStudentReq;
import com.forteach.quiz.practiser.web.req.GradeAnswerReq;
import com.forteach.quiz.practiser.web.resp.AnswerGradeListResp;
import com.forteach.quiz.practiser.web.resp.AnswerGradeResp;
import com.forteach.quiz.practiser.web.resp.AnswerStudentResp;
import com.forteach.quiz.problemsetlibrary.service.BigQuestionExerciseBookService;
import com.forteach.quiz.service.CorrectService;
import com.forteach.quiz.service.StudentsService;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.forteach.quiz.practiser.constant.Dic.IS_ANSWER_COMPLETED_N;
import static com.forteach.quiz.practiser.constant.Dic.IS_ANSWER_COMPLETED_Y;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-3 13:49
 * @version: 1.0
 * @description:
 */
@Service
public class ExerciseBookAnswerService {

    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final CorrectService correctService;
    private final StudentsService studentsService;
    private final BigQuestionExerciseBookService bigQuestionExerciseBookService;

    @Autowired
    public ExerciseBookAnswerService(ReactiveMongoTemplate reactiveMongoTemplate, BigQuestionExerciseBookService bigQuestionExerciseBookService, CorrectService correctService, StudentsService studentsService) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.bigQuestionExerciseBookService = bigQuestionExerciseBookService;
        this.studentsService = studentsService;
        this.correctService = correctService;
    }

    /**
     * 设置查询条件
     *
     * @param exeBookType
     * @param chapterId
     * @param courseId
     * @return
     */
    private Criteria buildExerciseBook(final Integer exeBookType, final String chapterId, final String courseId, final String preview) {

        Criteria criteria = new Criteria();

        if (StrUtil.isNotBlank(exeBookType.toString())) {
            criteria.and("exeBookType").is(exeBookType);
        }
        if (StrUtil.isNotBlank(chapterId)) {
            criteria.and("chapterId").is(chapterId);
        }
        if (StrUtil.isNotBlank(courseId)) {
            criteria.and("courseId").is(courseId);
        }
        if (StrUtil.isNotBlank(preview)) {
            criteria.and("preview").is(preview);
        }
        return criteria;
    }

    private Criteria queryCriteria(final Integer exeBookType, final String chapterId, final String courseId, final String preview, final String studentId, final String questionId) {
        //设置查询条件
        Criteria criteria = buildExerciseBook(exeBookType, chapterId, courseId, preview);

        if (StrUtil.isNotBlank(studentId)) {
            criteria.and("answerList.studentId").is(studentId);
        }

        if (StrUtil.isNotBlank(questionId)) {
            criteria.and("answerList.questionId").is(questionId);
        }
        return criteria;
    }


    /**
     * 保存学生回答答题记录, 并修改回答过的记录信息
     *
     * @param answerReq
     * @return
     */
    public Mono<Boolean> saveAnswer(final AnswerReq answerReq) {

        Criteria criteria = queryCriteria(answerReq.getExeBookType(), answerReq.getChapterId(), answerReq.getCourseId(), answerReq.getPreview(), answerReq.getStudentId(), answerReq.getQuestionId());

        Query query = Query.query(criteria);

        // 修改答题记录
        Update update = Update.update("uDate", DateUtil.formatDateTime(new Date()));

        if (StrUtil.isNotBlank(answerReq.getAnswer())) {
            update.set("answerList.$.answer", answerReq.getAnswer());
        }

        if (answerReq.getAnswerImageList() != null && !answerReq.getAnswerImageList().isEmpty()) {
            update.set("answerList.$.answerImageList", answerReq.getAnswerImageList());
        }
        if (answerReq.getFileList() != null && !answerReq.getFileList().isEmpty()) {
            update.set("answerList.$.fileList", answerReq.getFileList());
        }

        Mono<Boolean> right = correctService.correcting(SingleQueKey.questionsNow(answerReq.getQuestionId()), answerReq.getQuestionId(), answerReq.getAnswer());

        return right.flatMap(r -> {
            update.set("answerList.$.right", r);
            return reactiveMongoTemplate
                    .updateMulti(query, update, AskAnswerExerciseBook.class)
                    .map(UpdateResult::wasAcknowledged);
        }).flatMap(b -> saveUpdateAskAnswer(b, answerReq))
                .flatMap(b -> MyAssert.isFalse(b, DefineCode.ERR0010, "保存失败"));
    }

    /**
     * @param wasAcknowledged
     * @param answerReq
     * @return
     */
    private Mono<Boolean> saveUpdateAskAnswer(final Boolean wasAcknowledged, final AnswerReq answerReq) {
        //判断已经所有题目已经回答完毕 todo
        return Mono.just(true)
                .flatMap(isAnswerCompleted -> {
                    if (wasAcknowledged) {
                        return isAnswerCompleted ? updateAskAnswer(answerReq, IS_ANSWER_COMPLETED_Y) : updateAskAnswer(answerReq, IS_ANSWER_COMPLETED_N);
                    } else {
                        return Mono.just(false);
                    }
                });
    }


    /**
     * 修改答题记录信息
     *
     * @param answerReq
     * @param isAnswerCompleted
     * @return
     */
    private Mono<Boolean> updateAskAnswer(final AnswerReq answerReq, final String isAnswerCompleted) {
        //查询
        Criteria criteria = buildExerciseBook(answerReq.getExeBookType(), answerReq.getChapterId(), answerReq.getCourseId(), answerReq.getPreview());
        if (StrUtil.isNotBlank(answerReq.getStudentId())) {
            criteria.and("studentId").is(answerReq.getStudentId());
        }

        Query query = Query.query(criteria);

        // 修改答题记录
        Update update = Update.update("uDate", DateUtil.formatDateTime(new Date()));
        if (StrUtil.isNotBlank(isAnswerCompleted)) {
            update.set("isAnswerCompleted", isAnswerCompleted);
        }
        if (StrUtil.isNotBlank(answerReq.getQuestionId())) {
            update.addToSet("questions", answerReq.getQuestionId());
        }

        return reactiveMongoTemplate.updateMulti(query, update, AskAnswerStudents.class)
                .map(UpdateResult::wasAcknowledged);
    }


    /**
     * 老师批改学生回答的题
     *
     * @param gradeAnswerReq
     * @return
     */
    public Mono<Boolean> gradeAnswer(final GradeAnswerReq gradeAnswerReq) {
        //设置查询条件
        Criteria criteria = queryCriteria(gradeAnswerReq.getExeBookType(), gradeAnswerReq.getChapterId(), gradeAnswerReq.getCourseId(), gradeAnswerReq.getPreview(), gradeAnswerReq.getStudentId(), gradeAnswerReq.getQuestionId());

        Query query = Query.query(criteria);

        // 修改答题记录
        Update update = Update.update("uDate", DateUtil.formatDateTime(new Date()));

        if (StrUtil.isNotBlank(gradeAnswerReq.getEvaluation())) {
            update.set("answerList.$.evaluation", gradeAnswerReq.getEvaluation());
        }
        if (StrUtil.isNotBlank(gradeAnswerReq.getScore())) {
            update.set("answerList.$.score", gradeAnswerReq.getScore());
        }
        if (StrUtil.isNotBlank(gradeAnswerReq.getTeacherId())) {
            update.set("answerList.$.teacherId", gradeAnswerReq.getTeacherId());
        }

        return reactiveMongoTemplate
                .updateMulti(query, update, AskAnswerExerciseBook.class)
                .map(UpdateResult::wasAcknowledged)
                .flatMap(b -> {
                    return updateAskGrade(gradeAnswerReq);
                }).flatMap(b -> MyAssert.isFalse(b, DefineCode.ERR0010, "保存失败"));
    }

    private Mono<Boolean> updateAskGrade(final GradeAnswerReq gradeAnswerReq) {
        Criteria criteria = buildExerciseBook(gradeAnswerReq.getExeBookType(), gradeAnswerReq.getChapterId(), gradeAnswerReq.getCourseId(), gradeAnswerReq.getPreview());

        if (StrUtil.isNotBlank(gradeAnswerReq.getTeacherId())) {
            criteria.and("teacherId").is(gradeAnswerReq.getTeacherId());
        }

        Query query = Query.query(criteria);

        // 修改答题记录
        Update update = Update.update("uDate", DateUtil.formatDateTime(new Date()));
        update.addToSet("gradeList", Query.query(Criteria.where("studentId").is(gradeAnswerReq.getStudentId()).and("questionId").is(gradeAnswerReq.getQuestionId())));
        return reactiveMongoTemplate.updateMulti(query, update, AskAnswerGrade.class).map(UpdateResult::wasAcknowledged);
    }

    public Mono<List<AnswerStudentResp>> findAnswerStudent(final FindAnswerStudentReq findAnswerStudentReq) {
        Criteria criteria = buildExerciseBook(findAnswerStudentReq.getExeBookType(), findAnswerStudentReq.getChapterId(), findAnswerStudentReq.getCourseId(), findAnswerStudentReq.getPreview());
        if (StrUtil.isNotBlank(findAnswerStudentReq.getStudentId())) {
            criteria.and("studentId").is(findAnswerStudentReq.getStudentId());
        }

        if (StrUtil.isNotBlank(findAnswerStudentReq.getIsAnswerCompleted())) {
            criteria.and("isAnswerCompleted").is(findAnswerStudentReq.getIsAnswerCompleted());
        }
        Query query = Query.query(criteria);
        return reactiveMongoTemplate.find(query, AskAnswerStudents.class).collectList()
                .flatMap(answerStudentsList -> {
                    switch (findAnswerStudentReq.getIsAnswerCompleted()) {
                        case IS_ANSWER_COMPLETED_Y:
                            //是查询回答过的
                            return changeFindAnswer(answerStudentsList);
                        case IS_ANSWER_COMPLETED_N:
                            //没有回答的
                            return changeFindAnswer(answerStudentsList);
                        default:
                            MyAssert.isNull(null, DefineCode.ERR0010, "是否答题完毕参数错误");
                            return Mono.error(new Throwable("是否回答完毕, 参数错误"));
                    }
                });
    }

    private Mono<List<AnswerStudentResp>> changeFindAnswer(final List<AskAnswerStudents> answerStudentsList) {
        return Mono.just(answerStudentsList)
                .flatMapMany(Flux::fromIterable)
                .flatMap(this::answerStudentResp)
                .collectList();
    }

    private Mono<AnswerStudentResp> answerStudentResp(final AskAnswerStudents askAnswerStudents) {
        return studentsService.findStudentsBrief(askAnswerStudents.getStudentId())
                .map(students -> {
                    return new AnswerStudentResp(students.getId(), students.getName(), students.getPortrait(),
                            askAnswerStudents.getQuestions(), askAnswerStudents.getIsAnswerCompleted());
                });
    }

    public Mono<List<AnswerGradeListResp>>  findAnswerGradeList(final FindAnswerGradeReq findAnswerGradeReq) {
//    public Mono<List<AnswerGradeResp>>  findAnswerGradeList(final FindAnswerGradeReq findAnswerGradeReq) {
        Criteria criteria = buildExerciseBook(findAnswerGradeReq.getExeBookType(), findAnswerGradeReq.getChapterId(), findAnswerGradeReq.getCourseId(), findAnswerGradeReq.getPreview());

        if (StrUtil.isNotBlank(findAnswerGradeReq.getTeacherId())) {
            criteria.and("teacherId").is(findAnswerGradeReq.getTeacherId());
        }

        Query query = Query.query(criteria);

        Mono<List<AskAnswerGrade>> collectList = reactiveMongoTemplate.find(query, AskAnswerGrade.class).collectList();
                return collectList
                        .flatMapMany(Flux::fromIterable)
                        .filter(Objects::nonNull)
                        .flatMap(this::findAnswerGrade)
//                        .map(ArrayList::new)
//                        .collectList();
                        .map(AnswerGradeListResp::new)
                        .collectList();
    }

    private Mono<List<AnswerGradeResp>> findAnswerGrade(final AskAnswerGrade askAnswerGrade) {
        return Mono.just(askAnswerGrade.getGradeList())
                .flatMapMany(Flux::fromIterable)
                .flatMap(this::findStudentQuestionId)
                .collectList();
    }

    private Mono<AnswerGradeResp> findStudentQuestionId(final AnswerGrade answerGrade){
        return studentsService.findStudentsBrief(answerGrade.getStudentId())
                .map(students -> new AnswerGradeResp(students.getId(), students.getName(), students.getPortrait(),
                        answerGrade.getQuestionId()));
    }
}

package com.forteach.quiz.practiser.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.domain.BaseEntity;
import com.forteach.quiz.evaluate.domain.QuestionExerciseReward;
import com.forteach.quiz.evaluate.service.RewardService;
import com.forteach.quiz.evaluate.web.control.res.CumulativeRes;
import com.forteach.quiz.interaction.execute.service.Key.SingleQueKey;
import com.forteach.quiz.practiser.domain.AnswerLists;
import com.forteach.quiz.practiser.domain.AskAnswerExercise;
import com.forteach.quiz.practiser.web.req.*;
import com.forteach.quiz.practiser.web.resp.AnswerStudentResp;
import com.forteach.quiz.problemsetlibrary.domain.BigQuestionExerciseBook;
import com.forteach.quiz.problemsetlibrary.domain.base.ExerciseBook;
import com.forteach.quiz.problemsetlibrary.repository.BigQuestionExerciseBookRepository;
import com.forteach.quiz.problemsetlibrary.service.BigQuestionExerciseBookService;
import com.forteach.quiz.problemsetlibrary.web.req.ExerciseBookReq;
import com.forteach.quiz.questionlibrary.domain.BigQuestion;
import com.forteach.quiz.service.CorrectService;
import com.forteach.quiz.service.StudentsService;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.forteach.quiz.practiser.constant.Dic.*;
import static com.forteach.quiz.util.StringUtil.isNotEmpty;
import static java.util.stream.Collectors.toList;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-3 13:49
 * @version: 1.0
 * @description:
 */
@Service
public class ExerciseAnswerService {

    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final ReactiveRedisTemplate reactiveRedisTemplate;
    private final CorrectService correctService;
    private final StudentsService studentsService;
    private final RewardService rewardService;
    private final BigQuestionExerciseBookService bigQuestionExerciseBookService;
    private final BigQuestionExerciseBookRepository bigQuestionExerciseBookRepository;

    @Autowired
    public ExerciseAnswerService(ReactiveMongoTemplate reactiveMongoTemplate,
                                 BigQuestionExerciseBookService bigQuestionExerciseBookService,
                                 ReactiveRedisTemplate reactiveRedisTemplate,
                                 RewardService rewardService,
                                 BigQuestionExerciseBookRepository bigQuestionExerciseBookRepository,
                                 CorrectService correctService, StudentsService studentsService) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.studentsService = studentsService;
        this.correctService = correctService;
        this.rewardService = rewardService;
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.bigQuestionExerciseBookService = bigQuestionExerciseBookService;
        this.bigQuestionExerciseBookRepository = bigQuestionExerciseBookRepository;
    }

    /**
     * 设置查询条件
     *
     * @param exeBookType
     * @param chapterId
     * @param courseId
     * @return
     */
    private Criteria buildExerciseBook(final String exeBookType, final String chapterId, final String courseId, final String preview, final String classId, final String studentId) {

        Criteria criteria = new Criteria();

        if (StrUtil.isNotBlank(exeBookType)) {
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
        if (StrUtil.isNotBlank(classId)) {
            criteria.and("classId").is(classId);
        }
        if (StrUtil.isNotBlank(studentId)) {
            criteria.and("studentId").is(studentId);
        }
        return criteria;
    }

    private Criteria queryCriteria(final String exeBookType, final String chapterId, final String courseId, final String preview, final String studentId, final String questionId, final String classId) {
        //设置查询条件
        Criteria criteria = buildExerciseBook(exeBookType, chapterId, courseId, preview, classId, studentId);

        if (StrUtil.isNotBlank(questionId)) {
            criteria.and("questionId").is(questionId);
        }
        return criteria;
    }

    private Update updateQuery(final String exeBookType, final String chapterId, final String courseId, final String preview, final String studentId, final String classId) {
        // 修改答题记录
        Update update = Update.update("uDate", DateUtil.formatDateTime(new Date()));
        if (StrUtil.isNotBlank(exeBookType)) {
            update.set("exeBookType", exeBookType);
        }
        if (StrUtil.isNotBlank(chapterId)) {
            update.set("chapterId", chapterId);
        }
        if (StrUtil.isNotBlank(courseId)) {
            update.set("courseId", courseId);
        }
        if (StrUtil.isNotBlank(preview)) {
            update.set("preview", preview);
        }
        if (StrUtil.isNotBlank(classId)) {
            update.set("classId", classId);
        }
        if (StrUtil.isNotBlank(studentId)) {
            update.set("studentId", studentId);
        }
        return update;
    }


    /**
     * 保存学生回答答题记录, 并修改回答过的记录信息
     *
     * @param answerReq
     * @return
     */
    public Mono<Boolean> saveAnswer(final AnswerReq answerReq) {

        Criteria criteria = buildExerciseBook(answerReq.getExeBookType(), answerReq.getChapterId(), answerReq.getCourseId(), answerReq.getPreview(), answerReq.getClassId(), answerReq.getStudentId());

        Query query = Query.query(criteria);

        // 修改答题记录
        Update update = updateQuery(answerReq.getExeBookType(), answerReq.getChapterId(), answerReq.getCourseId(), answerReq.getPreview(), answerReq.getStudentId(), answerReq.getClassId());

        if (StrUtil.isNotBlank(answerReq.getQuestionId())) {
            update.set("questionId", answerReq.getQuestionId());
        }

        if (StrUtil.isNotBlank(answerReq.getAnswer())) {
            update.set("answer", answerReq.getAnswer());
        }

        if (answerReq.getAnswerImageList() != null && !answerReq.getAnswerImageList().isEmpty()) {
            update.set("answerImageList", answerReq.getAnswerImageList());
        }
        if (answerReq.getFileList() != null && !answerReq.getFileList().isEmpty()) {
            update.set("fileList", answerReq.getFileList());
        }

        return correctService.correcting(SingleQueKey.questionsNow(answerReq.getQuestionId()), answerReq.getQuestionId(), answerReq.getAnswer())
                .flatMap(r -> {
                    update.set("right", r);
                    return reactiveMongoTemplate
                            .upsert(query, update, AskAnswerExercise.class)
                            .map(UpdateResult::wasAcknowledged)
                            .filterWhen(b -> {
                                if (!b) {
                                    return Mono.just(false);
                                } else {
                                    return updateAnswerLists(answerReq);
                                }
                            });
                })
                .flatMap(b -> MyAssert.isFalse(b, DefineCode.ERR0010, "保存失败"));
    }


    private Mono<Boolean> updateAnswerLists(final AnswerReq answerReq) {
        Criteria criteria = buildExerciseBook(answerReq.getExeBookType(), answerReq.getChapterId(), answerReq.getCourseId(), answerReq.getPreview(), answerReq.getClassId(), answerReq.getStudentId());

        Query query = Query.query(criteria);

        // 修改答题记录
        Update update = updateQuery(answerReq.getExeBookType(), answerReq.getChapterId(), answerReq.getCourseId(), answerReq.getPreview(), answerReq.getStudentId(), answerReq.getClassId());

        //保存回答记录
        if (StrUtil.isNotBlank(answerReq.getQuestionId())) {
            update.addToSet("questions", answerReq.getQuestionId());
        }
        // todo 判断是否回答完
        update.set("isAnswerCompleted", IS_ANSWER_COMPLETED_N);
        return reactiveMongoTemplate.upsert(query, update, AnswerLists.class)
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
        Criteria criteria = queryCriteria(gradeAnswerReq.getExeBookType(), gradeAnswerReq.getChapterId(), gradeAnswerReq.getCourseId(), gradeAnswerReq.getPreview(), gradeAnswerReq.getStudentId(), gradeAnswerReq.getQuestionId(), gradeAnswerReq.getClassId());

        Query query = Query.query(criteria);

        // 修改答题记录
        Update update = Update.update("uDate", DateUtil.formatDateTime(new Date()));

        if (StrUtil.isNotBlank(gradeAnswerReq.getEvaluation())) {
            update.set("evaluation", gradeAnswerReq.getEvaluation());
        }
        if (StrUtil.isNotBlank(gradeAnswerReq.getScore())) {
            update.set("score", gradeAnswerReq.getScore());
        }
        if (StrUtil.isNotBlank(gradeAnswerReq.getTeacherId())) {
            update.set("teacherId", gradeAnswerReq.getTeacherId());
        }

        return reactiveMongoTemplate
                .updateMulti(query, update, AskAnswerExercise.class)
                .map(UpdateResult::wasAcknowledged)
                .flatMap(b -> MyAssert.isFalse(b, DefineCode.ERR0010, "保存失败"));
    }

    /**
     * 查询学生的答题记录
     *
     * @param findAnswerStudentReq
     * @return
     */
    public Mono<List<AnswerStudentResp>> findAnswerStudent(final FindAnswerStudentReq findAnswerStudentReq) {
        Criteria criteria = buildExerciseBook(findAnswerStudentReq.getExeBookType(), findAnswerStudentReq.getChapterId(), findAnswerStudentReq.getCourseId(), findAnswerStudentReq.getPreview(), findAnswerStudentReq.getClassId(), findAnswerStudentReq.getStudentId());

        if (StrUtil.isNotBlank(findAnswerStudentReq.getQuestionId())) {
            criteria.and("questionId").is(findAnswerStudentReq.getQuestionId());
        }

        Query query = Query.query(criteria);

        return reactiveMongoTemplate.find(query, AskAnswerExercise.class).collectList()
                .flatMap(askAnswerExercises -> {
                    switch (findAnswerStudentReq.getIsAnswerCompleted()) {
                        case IS_ANSWER_COMPLETED_Y:
                            //是查询回答过的
                            return changeFindAnswer(askAnswerExercises, findAnswerStudentReq.getExeBookType(), findAnswerStudentReq.getCourseId(), findAnswerStudentReq.getChapterId());
                        case IS_ANSWER_COMPLETED_N:
                            //没有回答的
                            return findNoReplyAnswer(askAnswerExercises, findAnswerStudentReq.getExeBookType(), findAnswerStudentReq.getCourseId(), findAnswerStudentReq.getChapterId(), findAnswerStudentReq.getClassId());
                        default:
                            MyAssert.isNull(null, DefineCode.ERR0010, "是否答题完毕参数错误");
                            return Mono.error(new Throwable("是否回答完毕, 参数错误"));
                    }
                });
    }

    /**
     * 查询已经回答过的
     *
     * @param askAnswerExercises
     * @return
     */
    private Mono<List<AnswerStudentResp>> changeFindAnswer(final List<AskAnswerExercise> askAnswerExercises, final String exeBookType, final String courseId, final String chapterId) {
        //查询回答过的题目id
        Mono<List<String>> answerQuestionIds = findAnswerQuestionIds(askAnswerExercises);
        //查询全部题目信息
        Mono<List<String>> allQuestionIds = findAllQuestionIds(exeBookType, courseId, chapterId);

        return Mono.zip(answerQuestionIds, allQuestionIds)
                .flatMap(tuple2 -> {
                    //判断是否回答完毕
                    return isCompletedSuccess(tuple2.getT1(), tuple2.getT2())
                            .flatMap(b -> {
                                return Mono.just(askAnswerExercises)
                                        .flatMapMany(Flux::fromIterable)
                                        .flatMap(askAnswerExercise -> answerStudentResp(askAnswerExercise.getStudentId(), askAnswerExercise.getQuestionId(), b))
                                        .collectList();
                            });
                });
    }

    /**
     * 学生没有回答的
     *
     * @param askAnswerExercises
     * @param courseId
     * @param chapterId
     * @return
     */
    private Mono<List<AnswerStudentResp>> findNoReplyAnswer(final List<AskAnswerExercise> askAnswerExercises, final String exeBookType,
                                                            final String courseId, final String chapterId, final String classId) {
        //查询回答过的题目id
        Mono<List<String>> answerQuestionIds = findAnswerQuestionIds(askAnswerExercises);
        //查询全部题目信息
        Mono<List<String>> allQuestionIds = findAllQuestionIds(exeBookType, courseId, chapterId);
        //查询所有题id

        return Mono.zip(answerQuestionIds, allQuestionIds)
                .flatMap(tuple2 -> {
                    return Mono.just(tuple2.getT1()
                            .stream()
                            .filter(i -> !tuple2.getT2().contains(i))
                            .collect(toList()));
                })
                .flatMap(stringList -> {
                    return findAnswerStudentIds(askAnswerExercises).flatMap(strings -> {
                        return findNoAnswerStudentIds(classId, strings)
                                .flatMap(q -> findNoAnswerQuestionId(stringList, q));
                    });
                });
    }

    private Mono<List<AnswerStudentResp>> findNoAnswerQuestionId(final List<String> studentIds, final List<String> questionIds) {
        return Mono.just(studentIds)
                .flatMapMany(Flux::fromIterable)
                .flatMap(s -> {
                    return Mono.just(questionIds)
                            .flatMapMany(Flux::fromIterable)
                            .flatMap(q -> answerStudentResp(s, q, IS_ANSWER_COMPLETED_N));
                })
                .collectList();
    }

    private Mono<List<String>> findAnswerStudentIds(final List<AskAnswerExercise> askAnswerExercises) {
        return Mono.just(askAnswerExercises)
                .flatMapMany(Flux::fromIterable)
                .map(AskAnswerExercise::getStudentId)
                .collectList();
    }

    private Mono<List<String>> findNoAnswerStudentIds(final String classId, final List<String> studentIds) {
        Mono<List<String>> listMono = reactiveRedisTemplate
                .opsForHash()
                .keys(CLASS_ROOM.concat(classId))
                .collectList()
                .switchIfEmpty(MyAssert.isNull(null, DefineCode.ERR0010, "不存在对应的班级学生"));
        return listMono
                .filter(Objects::nonNull)
                .flatMap(s -> {
                    return Mono.just(studentIds.stream().filter(i -> !s.contains(i)).collect(toList()));
                });
    }


    /**
     * 判断是否回答完成
     *
     * @return
     */
    private Mono<String> isCompletedSuccess(final List<String> answerQuestionIds, final List<String> questionIds) {
        if (answerQuestionIds.size() != questionIds.size()) {
            return Mono.just("N");
        } else {
            for (String questionId : questionIds) {
                if (!answerQuestionIds.contains(questionId)) {
                    return Mono.just("N");
                }
            }
            return Mono.just("Y");
        }
    }

    /**
     * 查询所有题id信息
     *
     * @param exeBookType
     * @param courseId
     * @param chapterId
     * @return
     */
    private Mono<List<String>> findAllQuestionIds(final String exeBookType, final String courseId, final String chapterId) {
        return bigQuestionExerciseBookService
                .findExerciseBook(ExerciseBookReq.builder().exeBookType(exeBookType).chapterId(chapterId).courseId(courseId).build())
                .filter(Objects::nonNull)
                .flatMapMany(Flux::fromIterable)
                .filter(Objects::nonNull)
                .map(BaseEntity::getId)
                .collectList();
    }

    /**
     * 回答的题目信息id
     *
     * @param askAnswerExercises
     * @return
     */
    private Mono<List<String>> findAnswerQuestionIds(final List<AskAnswerExercise> askAnswerExercises) {
        return Mono.just(askAnswerExercises)
                .flatMapMany(Flux::fromIterable)
                .filter(Objects::nonNull)
                .map(AskAnswerExercise::getQuestionId)
                .collectList();
    }

    private Mono<AnswerStudentResp> answerStudentResp(final String studentId, final String questionId, final String isAnswerCompleted) {
        return studentsService.findStudentsBrief(studentId)
                .map(students -> {
                    return new AnswerStudentResp(students.getId(), students.getName(), students.getPortrait(),
                            questionId, isAnswerCompleted);
                });
    }


    public Mono<List<AskAnswerExercise>> findAnswerGradeList(final FindAnswerGradeReq findAnswerGradeReq) {
        Criteria criteria = queryCriteria(findAnswerGradeReq.getExeBookType(), findAnswerGradeReq.getChapterId(), findAnswerGradeReq.getCourseId(), findAnswerGradeReq.getPreview(), findAnswerGradeReq.getStudentId(), findAnswerGradeReq.getQuestionId(), findAnswerGradeReq.getClassId());

        if (StrUtil.isNotBlank(findAnswerGradeReq.getTeacherId())) {
            criteria.and("teacherId").is(findAnswerGradeReq.getTeacherId());
        }

        Query query = Query.query(criteria);

        return reactiveMongoTemplate.find(query, AskAnswerExercise.class)
                .collectList();
    }

    /**
     * 添加奖励(小红花)
     *
     * @param addRewardReq
     * @return
     */
    public Mono<String> addReward(final AddRewardReq addRewardReq) {
        Criteria criteria = new Criteria();

        if (StrUtil.isNotBlank(addRewardReq.getExeBookType())) {
            criteria.and("exeBookType").is(addRewardReq.getExeBookType());
        }
        if (StrUtil.isNotBlank(addRewardReq.getChapterId())) {
            criteria.and("chapterId").is(addRewardReq.getChapterId());
        }
        if (StrUtil.isNotBlank(addRewardReq.getCourseId())) {
            criteria.and("courseId").is(addRewardReq.getCourseId());
        }
        if (StrUtil.isNotBlank(addRewardReq.getPreview())) {
            criteria.and("preview").is(addRewardReq.getPreview());
        }
        if (StrUtil.isNotBlank(addRewardReq.getStudentId())) {
            criteria.and("studentId").is(addRewardReq.getStudentId());
        }
        return reactiveMongoTemplate.findOne(Query.query(criteria), QuestionExerciseReward.class)
                .switchIfEmpty(Mono.just(new QuestionExerciseReward()))
                .flatMap(questionExerciseReward -> {
                    if (StrUtil.isNotEmpty(questionExerciseReward.getNum())) {
                        MyAssert.notNull(questionExerciseReward, DefineCode.ERR0010, "您已经添加过奖励了");
                    }
                    return saveReward(addRewardReq);
                });

    }

    /**
     * 记录奖励
     *
     * @param addRewardReq
     * @return
     */
    private Mono<String> saveReward(final AddRewardReq addRewardReq) {
        QuestionExerciseReward questionExerciseReward = new QuestionExerciseReward();
        BeanUtils.copyProperties(addRewardReq, questionExerciseReward);
        return rewardService.cumulativeResMono(addRewardReq.getQuestionId(), addRewardReq.getStudentId(), Integer.valueOf(addRewardReq.getNum()))
                .map(CumulativeRes::getCount)
                .filterWhen(c -> reactiveMongoTemplate.save(questionExerciseReward)
                        .map(Objects::nonNull)
                        .flatMap(q -> MyAssert.isNull(q, DefineCode.ERR0010, "记录失败")));
    }


    //    public Mono<List<AnswerStudentResp>> findAnswer(final FindAnswerStudentReq findAnswerStudentReq) {
//        Criteria criteria = buildExerciseBook(findAnswerStudentReq.getExeBookType(), findAnswerStudentReq.getChapterId(), findAnswerStudentReq.getCourseId(), findAnswerStudentReq.getPreview(), findAnswerStudentReq.getClassId(), findAnswerStudentReq.getStudentId());
//
//        if (StrUtil.isNotBlank(findAnswerStudentReq.getIsAnswerCompleted())) {
//            criteria.and("isAnswerCompleted").is(findAnswerStudentReq.getIsAnswerCompleted());
//        }
//
//        if (IS_ANSWER_COMPLETED_N.equals(findAnswerStudentReq.getIsAnswerCompleted())) {
//            //查询没有回答完的记录
//            return reactiveMongoTemplate.find(Query.query(criteria), AnswerLists.class)
//                    .collectList()
//                    .filter(Objects::nonNull)
//                    .flatMapMany(Flux::fromIterable)
//                    .flatMap(answerLists -> {
//                        return studentsService.findStudentsBrief(answerLists.getStudentId())
//                                .flatMap(students -> {
//                                    return Mono.just(new AnswerStudentResp(students.getId(), students.getName(), students.getPortrait(),
//                                            answerLists.getQuestions(), answerLists.getIsAnswerCompleted()));
//                                });
//                    }).collectList();
//        } else if (IS_ANSWER_COMPLETED_Y.equals(findAnswerStudentReq.getIsAnswerCompleted())) {
//            //查询回答完的记录
//            return Mono.just(null);
//        } else {
//            return MyAssert.isNull(null, DefineCode.ERR0010, "是否回答完参数不正确");
//        }
//    }
    public Mono<List<String>> findAnswer(final FindAnswerStudentReq findAnswerStudentReq) {
//        return bigQuestionExerciseBookRepository.findByCourseIdAndChapterId(findAnswerStudentReq.getExeBookType(), findAnswerStudentReq.getCourseId(), findAnswerStudentReq.getChapterId());
        Criteria criteria = new Criteria();

        if (isNotEmpty(findAnswerStudentReq.getExeBookType())) {
            criteria.and("exeBookType").in(Integer.parseInt(findAnswerStudentReq.getExeBookType()));
        }
        if (isNotEmpty(findAnswerStudentReq.getChapterId())) {
            criteria.and("chapterId").in(findAnswerStudentReq.getChapterId());
        }
        if (isNotEmpty(findAnswerStudentReq.getCourseId())) {
            criteria.and("courseId").in(findAnswerStudentReq.getCourseId());
        }

        Query query = new Query(criteria);

        Mono<List<BigQuestion>> bigQuestions = reactiveMongoTemplate.findOne(query, BigQuestionExerciseBook.class).defaultIfEmpty(new BigQuestionExerciseBook())
                .map(ExerciseBook::getQuestionChildren);

        return bigQuestions
                .flatMapMany(Flux::fromIterable)
                .map(BigQuestion::getId)
                .collectList();

//        return bigQuestions.flatMapMany(Flux::fromIterable)
//                .map(BaseEntity::getId).collectList();
//                .filter(Objects::nonNull)
//                .flatMapMany(Flux::fromIterable)
//                .flatMap(BaseEntity::getId)
//                .collectList();
//        return bigQuestionExerciseBookService
//                .findExerciseBook(ExerciseBookReq.builder()
//                        .exeBookType(findAnswerStudentReq.getExeBookType())
//                        .chapterId(findAnswerStudentReq.getChapterId())
//                        .courseId(findAnswerStudentReq.getCourseId())
//                        .build())
//                .findExerciseBook(findAnswerStudentReq.getExeBookType(), findAnswerStudentReq.getChapterId(), findAnswerStudentReq.getCourseId())
//                .map(ExerciseBook::getQuestionChildren)
//                .flatMapMany(Flux::fromIterable)
//                .map(BaseEntity::getId)
//                .collectList();
//        .findQuestionIds(findAnswerStudentReq.getExeBookType(), findAnswerStudentReq.getChapterId(), findAnswerStudentReq.getCourseId());
    }
}

package com.forteach.quiz.practiser.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.domain.BaseEntity;
import com.forteach.quiz.evaluate.domain.QuestionExerciseReward;
import com.forteach.quiz.evaluate.service.RewardService;
import com.forteach.quiz.evaluate.web.control.res.CumulativeRes;
import com.forteach.quiz.practiser.domain.AnswerLists;
import com.forteach.quiz.practiser.domain.AskAnswerExercise;
import com.forteach.quiz.practiser.web.req.AddRewardReq;
import com.forteach.quiz.practiser.web.req.AnswerReq;
import com.forteach.quiz.practiser.web.resp.AskAnswerExerciseResp;
import com.forteach.quiz.problemsetlibrary.domain.BigQuestionExerciseBook;
import com.forteach.quiz.problemsetlibrary.domain.base.ExerciseBook;
import com.forteach.quiz.questionlibrary.repository.BigQuestionRepository;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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

import static com.forteach.quiz.common.Dic.BIG_QUESTION_EXAM_CHILDREN_TYPE_DESIGN;
import static com.forteach.quiz.practiser.constant.Dic.IS_REWARD_N;
import static com.forteach.quiz.practiser.constant.Dic.IS_REWARD_Y;
import static com.forteach.quiz.util.StringUtil.isNotEmpty;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-3 13:49
 * @version: 1.0
 * @description:
 */
@Slf4j
@Service
public class ExerciseAnswerService {

    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final RewardService rewardService;
    private final BigQuestionRepository bigQuestionRepository;

    @Autowired
    public ExerciseAnswerService(ReactiveMongoTemplate reactiveMongoTemplate,
                                 RewardService rewardService, BigQuestionRepository bigQuestionRepository) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.rewardService = rewardService;
        this.bigQuestionRepository = bigQuestionRepository;
    }

    /**
     * 设置查询条件
     *
     * @param exeBookType
     * @param chapterId
     * @param courseId
     * @return
     */
    Criteria buildExerciseBook(final String exeBookType, final String chapterId, final String courseId, final String preview, final String classId, final String studentId) {

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

    private Update updateQuery(final String exeBookType, final String chapterId, final String courseId, final String preview, final String studentId, final String classId, final String chapterName) {
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
        if (StrUtil.isNotBlank(chapterName)){
            update.set("chapterName", chapterName);
        }
        return update;
    }


    /**
     * 保存学生回答答题记录, 并修改回答过的记录信息
     *
     * @param answerReq
     * @return
     */
//    public Mono<Boolean> saveAnswer(final AnswerReq answerReq) {
//
//        Mono<Boolean> isReward = isCheckoutReward(answerReq);
//
//        Criteria criteria = queryCriteria(answerReq.getExeBookType(), answerReq.getChapterId(), answerReq.getCourseId(),
//                answerReq.getPreview(), answerReq.getStudentId(), answerReq.getQuestionId(), answerReq.getClassId());
//
//        Query query = Query.query(criteria);
//
//        // 修改答题记录
//        Update update = updateQuery(answerReq.getExeBookType(), answerReq.getChapterId(), answerReq.getCourseId(),
//                answerReq.getPreview(), answerReq.getStudentId(), answerReq.getClassId(), answerReq.getChapterName());
//
//        if (StrUtil.isNotBlank(answerReq.getQuestionId())) {
//            update.set("questionId", answerReq.getQuestionId());
//        }
//
//        if (StrUtil.isNotBlank(answerReq.getAnswer())) {
//            update.set("answer", answerReq.getAnswer());
//        }
//
//        if (answerReq.getAnswerImageList() != null && !answerReq.getAnswerImageList().isEmpty()) {
//            update.set("answerImageList", answerReq.getAnswerImageList());
//        }
//        if (answerReq.getFileList() != null && !answerReq.getFileList().isEmpty()) {
//            update.set("fileList", answerReq.getFileList());
//        }
//
//        //判断答案是否正确
//        return isReward.flatMap(is -> {
//            if (is) {
//                return correctService.correcting(SingleQueKey.questionsNow(answerReq.getQuestionId()), answerReq.getQuestionId(), answerReq.getAnswer())
//                        .flatMap(r -> {
//                            update.set("right", r);
//                            return reactiveMongoTemplate
//                                    .upsert(query, update, AskAnswerExercise.class)
//                                    .map(UpdateResult::wasAcknowledged)
//                                    .filterWhen(b -> {
//                                        if (!b) {
//                                            return Mono.just(false);
//                                        } else {
//                                            //保存答题记录信息习题id
//                                            return updateAnswerLists(answerReq);
//                                        }
//                                    });
//                        })
//                        .flatMap(b -> MyAssert.isFalse(b, DefineCode.ERR0010, "保存失败"));
//            }
//            return MyAssert.isNull(null, DefineCode.ERR0010, "老师已经批改过不能回答了");
//        });
//    }

    Mono<Boolean> isCheckoutReward(final AnswerReq answerReq) {
        Criteria criteria = buildExerciseBook(answerReq.getExeBookType(), answerReq.getChapterId(), answerReq.getCourseId(), answerReq.getPreview(), answerReq.getClassId(), answerReq.getStudentId());
        return reactiveMongoTemplate.findOne(Query.query(criteria), AnswerLists.class)
                .switchIfEmpty(Mono.just(new AnswerLists()))
                .filter(Objects::nonNull)
                .flatMap(AnswerLists -> {
                    if (StrUtil.isBlank(AnswerLists.getIsReward())) {
                        return Mono.just(true);
                    } else if (IS_REWARD_Y.equals(AnswerLists.getIsReward())) {
                        return Mono.just(false);
                    }
                    return Mono.just(true);
                });
    }


    Mono<Boolean> updateAnswerLists(final AnswerReq answerReq) {
        Criteria criteria = buildExerciseBook(answerReq.getExeBookType(), answerReq.getChapterId(), answerReq.getCourseId(), answerReq.getPreview(), answerReq.getClassId(), answerReq.getStudentId());

        // 修改答题记录
        Update update = updateQuery(answerReq.getExeBookType(), answerReq.getChapterId(), answerReq.getCourseId(),
                answerReq.getPreview(), answerReq.getStudentId(), answerReq.getClassId(), answerReq.getChapterName());

        //保存回答记录
        if (StrUtil.isNotBlank(answerReq.getQuestionId())) {
            update.addToSet("questions", answerReq.getQuestionId());
        }
        update.set("isReward", IS_REWARD_N);
        return reactiveMongoTemplate
                .upsert(Query.query(criteria), update, AnswerLists.class)
                .map(UpdateResult::wasAcknowledged)
                .filterWhen(b -> isAnswerCompleted(answerReq));
    }

    private Mono<Boolean> isAnswerCompleted(final AnswerReq answerReq) {

        Criteria criteria = buildExerciseBook(answerReq.getExeBookType(), answerReq.getChapterId(), answerReq.getCourseId(), answerReq.getPreview(), answerReq.getClassId(), answerReq.getStudentId());

        Query query = Query.query(criteria);
        Mono<List<String>> answerQuestionIds = reactiveMongoTemplate.findOne(query, AnswerLists.class)
                .filter(Objects::nonNull).map(AnswerLists::getQuestions);
        //查询全部题目信息
        Mono<List<String>> allQuestionIds = findAllQuestionIds(answerReq.getExeBookType(), answerReq.getCourseId(), answerReq.getChapterId());
        return Mono.zip(answerQuestionIds, allQuestionIds)
                .flatMap(tuple2 -> {
                    return isCompletedSuccess(tuple2.getT1(), tuple2.getT2())
                            .flatMap(s -> {
                                return reactiveMongoTemplate.upsert(query, Update.update("isAnswerCompleted", s), AnswerLists.class)
                                        .map(UpdateResult::wasAcknowledged)
                                        .filterWhen(b -> {
                                            if (b) {
                                                return setIsCorrectCompleted(answerReq.getExeBookType(), answerReq.getChapterId(), answerReq.getCourseId(),
                                                        answerReq.getPreview(), answerReq.getClassId(), answerReq.getStudentId(), answerReq.getQuestionId());
                                            } else {
                                                return Mono.just(false);
                                            }
                                        });
                            });
                });
    }

    Mono<Boolean> setIsCorrectCompleted(final String exeBookType, final String chapterId, final String courseId, final String preview, final String classId, final String studentId, final String questionId) {
        return findExamType(questionId).flatMap(type -> {
            if (BIG_QUESTION_EXAM_CHILDREN_TYPE_DESIGN.equals(type)) {
                return Mono.just(true);
            } else {
                return addCorrect(exeBookType, chapterId, courseId, preview, classId, studentId, questionId);
            }
        });
    }

    /**
     * 查找习题类型
     * @param questionId
     * @return
     */
    private Mono<String> findExamType(final String questionId){
        return bigQuestionRepository.findById(questionId)
                .flatMap(bigQuestion -> {
                    final JSONObject json = JSON.parseObject(JSON.toJSONString(bigQuestion));
                    log.debug("查找题类型 : questionId : [], bigQuestion : []", questionId, bigQuestion);
                    return Mono.just(String.valueOf(JSONPath.eval(json, "$.examChildren[0].examType")));
                });
    }

    private Mono<Boolean> isCorrectCompleted(final String exeBookType, final String chapterId, final String courseId, final String preview, final String classId, final String studentId) {

        Criteria criteria = buildExerciseBook(exeBookType, chapterId, courseId, preview, classId, studentId);

        Query query = Query.query(criteria);

        Mono<List<String>> answerQuestionIds = reactiveMongoTemplate.findOne(query, AnswerLists.class)
                .map(AnswerLists::getCorrectQuestionIds);
        //查询全部题目信息
        Mono<List<String>> allQuestionIds = findAllQuestionIds(exeBookType, courseId, chapterId);
        return Mono.zip(answerQuestionIds, allQuestionIds)
                .flatMap(tuple2 -> {
                    return isCompletedSuccess(tuple2.getT1(), tuple2.getT2())
                            .flatMap(s -> {
                                return reactiveMongoTemplate.upsert(query, Update.update("isCorrectCompleted", s), AnswerLists.class)
                                        .map(UpdateResult::wasAcknowledged);
                            });
                });
    }

    /**
     * 老师批改学生回答的题
     *
//     * @param gradeAnswerReq
//     * @return
     */
//    public Mono<Boolean> gradeAnswer(final GradeAnswerReq gradeAnswerReq) {
//        //设置查询条件
//        Criteria criteria = queryCriteria(gradeAnswerReq.getExeBookType(), gradeAnswerReq.getChapterId(), gradeAnswerReq.getCourseId(), gradeAnswerReq.getPreview(), gradeAnswerReq.getStudentId(), gradeAnswerReq.getQuestionId(), gradeAnswerReq.getClassId());
//
//        Query query = Query.query(criteria);
//
//        // 修改答题记录
//        Update update = Update.update("uDate", DateUtil.formatDateTime(new Date()));
//
//        if (StrUtil.isNotBlank(gradeAnswerReq.getEvaluation())) {
//            update.set("evaluation", gradeAnswerReq.getEvaluation());
//        }
//        if (StrUtil.isNotBlank(gradeAnswerReq.getScore())) {
//            update.set("score", gradeAnswerReq.getScore());
//        }
//        if (StrUtil.isNotBlank(gradeAnswerReq.getTeacherId())) {
//            update.set("teacherId", gradeAnswerReq.getTeacherId());
//        }
//
//        return reactiveMongoTemplate
//                .updateMulti(query, update, AskAnswerExercise.class)
//                .map(UpdateResult::wasAcknowledged)
//                .filterWhen(b -> {
//                    if (b) {
//                        return addCorrect(gradeAnswerReq.getExeBookType(), gradeAnswerReq.getChapterId(), gradeAnswerReq.getCourseId(),
//                                gradeAnswerReq.getPreview(), gradeAnswerReq.getClassId(), gradeAnswerReq.getStudentId(), gradeAnswerReq.getQuestionId());
//                    } else {
//                        return Mono.just(false);
//                    }
//                })
//                .flatMap(b -> MyAssert.isFalse(b, DefineCode.ERR0010, "保存失败"));
//    }

    Mono<Boolean> addCorrect(final String exeBookType, final String chapterId, final String courseId, final String preview, final String classId, final String studentId, final String questionId) {
        //设置查询条件
        Criteria criteria = buildExerciseBook(exeBookType, chapterId, courseId, preview, classId, studentId);

        // 修改答题记录
        Update update = Update.update("uDate", DateUtil.formatDateTime(new Date()));
        //保存回答记录
        if (StrUtil.isNotBlank(questionId)) {
            update.addToSet("correctQuestionIds", questionId);
        }
        return reactiveMongoTemplate
                .upsert(Query.query(criteria), update, AnswerLists.class)
                .map(UpdateResult::wasAcknowledged)
                .filterWhen(b -> isCorrectCompleted(exeBookType, chapterId, courseId, preview, classId, studentId));
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
     * 添加奖励(小红花)
     *
     * @param addRewardReq
     * @return
     */
    public Mono<String> addReward(final AddRewardReq addRewardReq) {
        Criteria criteria = buildExerciseBook(addRewardReq.getExeBookType(), addRewardReq.getChapterId(),
                addRewardReq.getCourseId(), addRewardReq.getPreview(), addRewardReq.getClassId(), addRewardReq.getStudentId());
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
                        .flatMap(q -> MyAssert.isNull(q, DefineCode.ERR0010, "记录失败")))
                .filterWhen(b -> saveAnswerList(addRewardReq));
    }

    private Mono<Boolean> saveAnswerList(final AddRewardReq addRewardReq) {
        Criteria criteria = buildExerciseBook(addRewardReq.getExeBookType(), addRewardReq.getChapterId(), addRewardReq.getCourseId(), addRewardReq.getPreview(), addRewardReq.getClassId(), addRewardReq.getStudentId());

        // 修改答题记录
        Update update = Update.update("uDate", DateUtil.formatDateTime(new Date()));
        //保存回答记录
        update.set("isReward", IS_REWARD_Y);
        return reactiveMongoTemplate.upsert(Query.query(criteria), update, AnswerLists.class)
                .map(UpdateResult::wasAcknowledged);
    }


//    public Mono<List<AnswerStudentResp>> findAnswer(final FindAnswerStudentReq findAnswerStudentReq) {
//
//        Criteria criteria = buildExerciseBook(findAnswerStudentReq.getExeBookType(), findAnswerStudentReq.getChapterId(), findAnswerStudentReq.getCourseId(), findAnswerStudentReq.getPreview(), findAnswerStudentReq.getClassId(), findAnswerStudentReq.getStudentId());
//
//        if (StrUtil.isNotBlank(findAnswerStudentReq.getIsAnswerCompleted())) {
//            criteria.and("isAnswerCompleted").is(findAnswerStudentReq.getIsAnswerCompleted());
//        }
//
//        if (IS_ANSWER_COMPLETED_N.equals(findAnswerStudentReq.getIsAnswerCompleted())) {
//            Query query = Query.query(criteria);
//            //查询没有回答完的记录
//            return reactiveMongoTemplate.find(query, AnswerLists.class)
//                    .collectList()
//                    .filter(Objects::nonNull)
//                    .flatMap(this::findAnswerNoReply);
//        } else if (IS_ANSWER_COMPLETED_Y.equals(findAnswerStudentReq.getIsAnswerCompleted())) {
//            if (StrUtil.isNotBlank(findAnswerStudentReq.getIsCorrectCompleted())) {
//                criteria.and("isCorrectCompleted").is(findAnswerStudentReq.getIsCorrectCompleted());
//            }
//
//            if (StrUtil.isNotBlank(findAnswerStudentReq.getIsReward())) {
//                criteria.and("isReward").is(findAnswerStudentReq.getIsReward());
//            }
//
//            Query query = Query.query(criteria);
//            //查询回答完的记录
//            return reactiveMongoTemplate.find(query, AnswerLists.class)
//                    .collectList()
//                    .filter(Objects::nonNull)
//                    .flatMapMany(Flux::fromIterable)
//                    .flatMap(answerLists -> this.findAnswerReply(answerLists, findAnswerStudentReq))
//                    .collectList();
//        } else {
//            return MyAssert.isNull(null, DefineCode.ERR0010, "是否回答完参数不正确");
//        }
//    }

//    private Mono<AnswerStudentResp> findAnswerReply(final AnswerLists answerLists, final FindAnswerStudentReq findAnswerStudentReq) {
//
////        Mono<List<String>> answerQuestionIds = Mono.just(answerLists.getQuestions());
//        //查询所有题的集合
//        if (IS_CORRECT_COMPLETED_N.equals(findAnswerStudentReq.getIsCorrectCompleted())) {
//            Mono<List<String>> allQuestionIds = Mono.just(answerLists.getQuestions());
//            Mono<List<String>> correctList = Mono.just(answerLists.getCorrectQuestionIds());
//            return Mono.zip(correctList, allQuestionIds)
//                    .flatMap(tuple2 -> {
//                        return Mono.just(tuple2.getT2().stream().filter(i -> !tuple2.getT1().contains(i)).collect(toList()));
//                    })
//                    .flatMap(questionIds -> {
//                        return findAnswerStudentResp(questionIds, answerLists.getStudentId(), answerLists.getIsAnswerCompleted(), answerLists.getIsCorrectCompleted(), answerLists.getIsReward());
//                    });
//        } else if (IS_CORRECT_COMPLETED_Y.equals(findAnswerStudentReq.getIsCorrectCompleted())) {
//            Mono<List<String>> correctList = Mono.just(answerLists.getCorrectQuestionIds());
//            return correctList
//                    .flatMap(questionIds -> {
//                        return findAnswerStudentResp(questionIds, answerLists.getStudentId(), answerLists.getIsAnswerCompleted(), answerLists.getIsCorrectCompleted(), answerLists.getIsReward());
//                    });
//        } else {
//            return buildAnswerStudentResp(answerLists);
//        }
//    }

//    private Mono<AnswerStudentResp> buildAnswerStudentResp(final AnswerLists answerLists){
//        return Mono.just(answerLists.getQuestions())
//                .filter(Objects::nonNull)
//                .flatMapMany(Flux::fromIterable)
//                .flatMap(s -> {
//                    final Criteria criteria = queryCriteria(answerLists.getExeBookType(), answerLists.getChapterId(), answerLists.getCourseId(), answerLists.getPreview(), answerLists.getStudentId(), s, answerLists.getClassId());
//                    return reactiveMongoTemplate.findOne(Query.query(criteria), AskAnswerExercise.class);
//                }).collectList()
//                .filter(Objects::nonNull)
//                .flatMap(askAnswerExercises -> {
//                    return studentsService.findStudentsBrief(answerLists.getStudentId())
//                            .flatMap(students -> {
//                                return Mono.just(new AnswerStudentResp(askAnswerExercises, answerLists.getIsAnswerCompleted(), answerLists.getIsCorrectCompleted(), answerLists.getIsReward(),
//                                        students.getId(), students.getName(), students.getPortrait()));
//                            });
//                });
//    }

//    private Mono<AnswerStudentResp> findAnswerStudentResp(final List<String> answerList, final String studentId, final String isAnswerCompleted, final String isCorrectCompleted, final String isReward) {
//        return studentsService.findStudentsBrief(studentId)
//                .flatMap(students -> {
//                    return Mono.just(new AnswerStudentResp(students.getId(), students.getName(), students.getPortrait(),
//                            answerList, isAnswerCompleted, isCorrectCompleted, isReward));
//                });
//    }

    /**
     * 查询回答记录没有回答完毕的
     *
     * @param answerLists
     * @return
     */
//    private Mono<List<AnswerStudentResp>> findAnswerNoReply(final List<AnswerLists> answerLists) {
//        return Mono.just(answerLists)
//                .flatMapMany(Flux::fromIterable)
//                .flatMap(answerList -> {
//                    return studentsService.findStudentsBrief(answerList.getStudentId())
//                            .flatMap(students -> {
//                                return Mono.just(new AnswerStudentResp(students.getId(), students.getName(), students.getPortrait(),
//                                                answerList.getQuestions(), answerList.getIsAnswerCompleted(), answerList.getIsCorrectCompleted(), answerList.getIsReward())
//                                );
//                            });
//                })
//                .collectList();
//    }

    /**
     * 查询所有题id信息
     *
     * @param exeBookType
     * @param courseId
     * @param chapterId
     * @return
     */
    private Mono<List<String>> findAllQuestionIds(final String exeBookType, final String courseId, final String chapterId) {

        Criteria criteria = new Criteria();

        if (isNotEmpty(exeBookType)) {
            criteria.and("exeBookType").in(Integer.parseInt(exeBookType));
        }
        if (isNotEmpty(chapterId)) {
            criteria.and("chapterId").in(chapterId);
        }
        if (isNotEmpty(courseId)) {
            criteria.and("courseId").in(courseId);
        }

        Mono<List<BaseEntity>> questionExamEntitylist = reactiveMongoTemplate
                .findOne(Query.query(criteria), BigQuestionExerciseBook.class)
                .defaultIfEmpty(new BigQuestionExerciseBook())
                .map(ExerciseBook::getQuestionChildren);

        return questionExamEntitylist
                .flatMapMany(Flux::fromIterable)
                .map(BaseEntity::getId)
                .collectList();
    }

    public Mono<List<AskAnswerExerciseResp>> findAnswerStudent(final AnswerReq answerReq) {
        Criteria criteria = queryCriteria(answerReq.getExeBookType(), answerReq.getChapterId(), answerReq.getCourseId(),
                answerReq.getPreview(), answerReq.getStudentId(), answerReq.getQuestionId(), answerReq.getClassId());

        Query query = Query.query(criteria);
        return reactiveMongoTemplate.find(query, AskAnswerExercise.class)
                .collectList()
                .flatMapMany(Flux::fromIterable)
                .flatMap(askAnswerExercise -> {
                    return Mono.just(new AskAnswerExerciseResp(askAnswerExercise.getQuestionId(),
                            askAnswerExercise.getAnswer(), askAnswerExercise.getFileList(), answerReq.getAnswerImageList()));
                }).collectList();
    }
}

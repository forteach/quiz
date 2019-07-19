package com.forteach.quiz.practiser.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.practiser.domain.AnswerLists;
import com.forteach.quiz.practiser.domain.ExerciseAnswerQuestionBook;
import com.forteach.quiz.practiser.web.req.AnswerReq;
import com.forteach.quiz.practiser.web.req.FindAnswerStudentReq;
import com.forteach.quiz.practiser.web.req.GradeAnswerReq;
import com.forteach.quiz.practiser.web.req.findExerciseBookReq;
import com.forteach.quiz.practiser.web.resp.AnswerResp;
import com.forteach.quiz.practiser.web.vo.AnswerVo;
import com.forteach.quiz.practiser.web.vo.UnwindedExerciseAnswerQuestionBook;
import com.forteach.quiz.problemsetlibrary.domain.BigQuestionExerciseBook;
import com.forteach.quiz.problemsetlibrary.service.BigQuestionExerciseBookService;
import com.forteach.quiz.problemsetlibrary.web.req.ExerciseBookReq;
import com.forteach.quiz.problemsetlibrary.web.vo.UnwindedBigQuestionexerciseBook;
import com.forteach.quiz.questionlibrary.domain.BigQuestion;
import com.forteach.quiz.questionlibrary.domain.base.QuestionExamEntity;
import com.forteach.quiz.service.CorrectService;
import com.forteach.quiz.service.StudentsService;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.forteach.quiz.practiser.constant.Dic.IS_ANSWER_COMPLETED_N;
import static com.forteach.quiz.practiser.constant.Dic.IS_ANSWER_COMPLETED_Y;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-12 11:00
 * @version: 1.0
 * @description:
 */
@Slf4j
@Service
public class ExerciseBookSnapshotService {

    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final CorrectService correctService;
    private final StudentsService studentsService;
    private final BaseExerciseAnswerService baseExerciseAnswerService;
    private final ExerciseAnswerService exerciseAnswerService;
    private final BigQuestionExerciseBookService bigQuestionExerciseBookService;

    @Autowired
    public ExerciseBookSnapshotService(ReactiveMongoTemplate reactiveMongoTemplate, ExerciseAnswerService exerciseAnswerService,
                                       BaseExerciseAnswerService baseExerciseAnswerService,
                                       BigQuestionExerciseBookService bigQuestionExerciseBookService,
                                       CorrectService correctService, StudentsService studentsService) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.studentsService = studentsService;
        this.correctService = correctService;
        this.baseExerciseAnswerService = baseExerciseAnswerService;
        this.exerciseAnswerService = exerciseAnswerService;
        this.bigQuestionExerciseBookService = bigQuestionExerciseBookService;
    }

    /**
     * 判断是否存在习题作业的快照
     *
     * @return
     */
    private Mono<Boolean> isExistSnapshot(final AnswerVo answerVo) {
        return reactiveMongoTemplate.exists(Query.query(baseExerciseAnswerService.buildExerciseBook(answerVo)),
                ExerciseAnswerQuestionBook.class);
    }

    /**
     * 作业练习回答记录步骤
     * 1、查询是否保存有快照 exerciseAnswerQuestionBook
     * 1) 有快照直接查询出对应的习题、作业信息
     * 2) 没有保存快照，直接拉取快照保存后， 将拉取的信息返回 bigQuestionexerciseBook
     * 2、比对回答信息，客观题直接进行批改，主观题只记录回答情况，等候教师批改
     * 3、将客观题批改过的记录下 questions
     * 4、判断是否批改完成　并修改对应的字段值 isAnswerCompleted Y/N
     *
     * @param answerVo
     * @param answerReq
     * @return
     */
    public Mono<Boolean> saveSnapshot(final AnswerVo answerVo, final AnswerReq answerReq) {

        //查询是否已经批改过，批改过不能继续提交答案
        Mono<Boolean> isReward = exerciseAnswerService.isCheckoutReward(answerReq);

        Criteria criteria = baseExerciseAnswerService.queryCriteria(answerVo, answerReq.getQuestionId());

        Query query = Query.query(criteria);


        Update update = baseExerciseAnswerService.updateQuery(answerVo);

        if (StrUtil.isNotBlank(answerReq.getChapterName())) {
            update.set("chapterName", answerReq.getChapterName());
        }
        if (StrUtil.isNotBlank(answerReq.getAnswer())) {
            update.set("bigQuestionExerciseBook.questionChildren.$.examChildren.0.stuAnswer", answerReq.getAnswer());
        }

        if (answerReq.getAnswerImageList() != null && !answerReq.getAnswerImageList().isEmpty()) {
            update.set("bigQuestionExerciseBook.questionChildren.$.examChildren.0.answerImageList", answerReq.getAnswerImageList());
        }
        if (answerReq.getFileList() != null && !answerReq.getFileList().isEmpty()) {
            update.set("bigQuestionExerciseBook.questionChildren.$.examChildren.0.fileList", answerReq.getFileList());
        }

        //判断学生答题结果
        return isReward.flatMap(reward -> {
            if (reward) {
                return isExistSnapshot(answerVo)
                        .flatMap(b -> {
                            if (b) {
                                return saveAnswer(answerVo, answerReq, query, update);
                            } else {
                                //查询当前的题库并将当前的题库快照进行保存
                                return bigQuestionExerciseBookService.findExerciseBook(answerReq.getExeBookType(), answerReq.getChapterId(), answerReq.getCourseId())
                                        .flatMap(bigQuestionExerciseBook -> {
                                            return reactiveMongoTemplate.save(new ExerciseAnswerQuestionBook(bigQuestionExerciseBook, answerVo))
                                                    .filter(Objects::nonNull)
                                                    .flatMap(exerciseAnswerQuestionBookClass -> {
                                                        return saveAnswer(answerVo, answerReq, query, update);
                                                    });
                                        });
                            }
                        });
            }
            return MyAssert.isNull(null, DefineCode.ERR0011, "老师已经批改过不能回答了");
        });
    }

    /**
     * 保存学生提交的作业答案并计算客观题的答题结果
     * @param answerVo
     * @param answerReq
     * @param query
     * @param update
     * @return
     */
    private Mono<Boolean> saveAnswer(final AnswerVo answerVo, final AnswerReq answerReq, final Query query, Update update) {
        return judgedResult(answerVo, answerReq.getQuestionId(), answerReq.getAnswer())
                .flatMap(r -> {
                    update.set("bigQuestionExerciseBook.questionChildren.$.examChildren.0.right", r);
                    return reactiveMongoTemplate.upsert(query, update, ExerciseAnswerQuestionBook.class)
                            .map(UpdateResult::wasAcknowledged)
                            .filterWhen(b -> {
                                if (!b) {
                                    return Mono.just(false);
                                } else {
                                    //保存答题记录信息习题id
                                    return exerciseAnswerService.updateAnswerLists(answerReq);
                                }
                            });
                });
    }

    /**
     * 获取回答结果
     *
     * @param answerVo
     * @param questionId
     * @return
     */
    private Mono<Boolean> judgedResult(final AnswerVo answerVo, final String questionId, final String answer) {
        Criteria criteria = baseExerciseAnswerService.buildExerciseBook(answerVo);

        // 查找题目类型
        Mono<List<QuestionExamEntity>> bigQuestionVos = reactiveMongoTemplate.findOne(Query.query(criteria), ExerciseAnswerQuestionBook.class)
                .filter(Objects::nonNull)
                .map(ExerciseAnswerQuestionBook::getBigQuestionExerciseBook)
                .map(BigQuestionExerciseBook::getQuestionChildren);

        return bigQuestionVos.flatMapMany(Flux::fromIterable)
                .filter(questionExamEntity -> questionExamEntity.getId().equals(questionId))
                .next()
                .flatMap(questionExamEntity -> {
                    BigQuestion bigQuestion = new BigQuestion();
                    BeanUtils.copyProperties(questionExamEntity, bigQuestion);
                    //计算回答的客观题回答正确与否，选择题和判断题需要全部回答正确，主观题不判断批改直接返回
                    return correctService.result(bigQuestion, answer);
                });
    }

    /**
     * 批改步骤
     * 1、将批改内容添加进答题批改记录表快照 exerciseAnswerQuestionBook
     * 2、将批改记录保存到记录表 answerLists correctQuestionIds
     * 3、判断批改完成，修改批改完成字段 isCorrectCompleted　'N' 改为　'Y'
     *
     * @param gradeAnswerReq
     * @param answerVo
     * @return
     */
    public Mono<Boolean> gradeAnswer(final GradeAnswerReq gradeAnswerReq, final AnswerVo answerVo) {
        Criteria criteria = baseExerciseAnswerService.queryCriteria(answerVo, gradeAnswerReq.getQuestionId());

        Query query = Query.query(criteria);

        Update update = Update.update("uDate", DateUtil.formatDateTime(new Date()));


        if (StrUtil.isNotBlank(gradeAnswerReq.getTeacherId())) {
            update.set("teacherId", gradeAnswerReq.getTeacherId());
        }

        if (StrUtil.isNotBlank(gradeAnswerReq.getEvaluation())) {
            update.set("bigQuestionExerciseBook.questionChildren.$.examChildren.0.evaluation", gradeAnswerReq.getEvaluation());
        }
        if (StrUtil.isNotBlank(gradeAnswerReq.getScore())) {
            update.set("bigQuestionExerciseBook.questionChildren.$.examChildren.0.score", gradeAnswerReq.getScore());
        }

        return reactiveMongoTemplate.upsert(query, update, ExerciseAnswerQuestionBook.class)
                .map(UpdateResult::wasAcknowledged)
                .filterWhen(b -> {
                    if (b) {
                        return exerciseAnswerService.addCorrect(gradeAnswerReq.getExeBookType(), gradeAnswerReq.getChapterId(), gradeAnswerReq.getCourseId(),
                                gradeAnswerReq.getPreview(), gradeAnswerReq.getClassId(), gradeAnswerReq.getStudentId(), gradeAnswerReq.getQuestionId());
                    } else {
                        return Mono.just(false);
                    }
                })
                .flatMap(b -> MyAssert.isFalse(b, DefineCode.ERR0010, "保存失败"));
    }

    public Mono<List<AnswerResp>> findAnswer(final FindAnswerStudentReq findAnswerStudentReq) {

        Criteria criteria = exerciseAnswerService.buildExerciseBook(findAnswerStudentReq.getExeBookType(), findAnswerStudentReq.getChapterId(), findAnswerStudentReq.getCourseId(), findAnswerStudentReq.getPreview(), findAnswerStudentReq.getClassId(), findAnswerStudentReq.getStudentId());

        if (StrUtil.isNotBlank(findAnswerStudentReq.getIsAnswerCompleted())) {
            criteria.and("isAnswerCompleted").is(findAnswerStudentReq.getIsAnswerCompleted());
        }

        if (IS_ANSWER_COMPLETED_N.equals(findAnswerStudentReq.getIsAnswerCompleted())) {

            //查询没有回答完的记录
            return findAnswerListByAnswerLists(Query.query(criteria));
        } else if (IS_ANSWER_COMPLETED_Y.equals(findAnswerStudentReq.getIsAnswerCompleted())) {
            if (StrUtil.isNotBlank(findAnswerStudentReq.getIsCorrectCompleted())) {
                criteria.and("isCorrectCompleted").is(findAnswerStudentReq.getIsCorrectCompleted());
            }

            if (StrUtil.isNotBlank(findAnswerStudentReq.getIsReward())) {
                criteria.and("isReward").is(findAnswerStudentReq.getIsReward());
            }

            //查询回答完的记录
            return findAnswerListByAnswerLists(Query.query(criteria));
        } else {
            return MyAssert.isNull(null, DefineCode.ERR0010, "是否回答完参数不正确");
        }
    }

    /**
     * 通过查询记录表信息转化查询结果集
     * @param query
     * @return
     */
    private Mono<List<AnswerResp>> findAnswerListByAnswerLists(final Query query){
        return reactiveMongoTemplate.find(query, AnswerLists.class)
                .collectList()
                .filter(Objects::nonNull)
                .flatMapMany(Flux::fromIterable)
                .flatMap(this::findExerciseAnswerQuestionBook)
                .filter(Objects::nonNull)
                .collectList()
                .flatMap(this::findAnswerRespList);
    }

    private Mono<List<AnswerResp>> findAnswerRespList(final List<ExerciseAnswerQuestionBook> exerciseAnswerQuestionBooks) {
        return Mono.just(exerciseAnswerQuestionBooks)
                .flatMapMany(Flux::fromIterable)
                .filter(Objects::nonNull)
                .flatMap(exerciseAnswerQuestionBook -> {
                    AnswerResp answerResp = new AnswerResp();
                    List<ExerciseAnswerQuestionBook> list = new ArrayList<>();
                    BeanUtils.copyProperties(exerciseAnswerQuestionBook, answerResp);
                    if (answerResp.getStudentId().equals(exerciseAnswerQuestionBook.getStudentId())) {
                        return studentsService.findStudentsBrief(exerciseAnswerQuestionBook.getStudentId())
                                .flatMap(students -> {
                                    answerResp.setPortrait(students.getPortrait());
                                    answerResp.setStudentId(students.getId());
                                    answerResp.setStudentName(students.getName());
                                    list.add(exerciseAnswerQuestionBook);
                                    answerResp.setExerciseAnswerQuestionBooks(list);
                                    return Mono.just(answerResp);
                                });
                    }
                    return Mono.just(answerResp);
                }).collectList();
    }


    /**
     * 查询学生答题时生成的快照作业或试卷
     * @param answerLists
     * @return
     */
    private Mono<ExerciseAnswerQuestionBook> findExerciseAnswerQuestionBook(final AnswerLists answerLists) {

        final Criteria criteria = exerciseAnswerService.buildExerciseBook(answerLists.getExeBookType(),
                answerLists.getChapterId(), answerLists.getCourseId(),
                answerLists.getPreview(), answerLists.getClassId(), answerLists.getStudentId());
        return reactiveMongoTemplate.findOne(Query.query(criteria), ExerciseAnswerQuestionBook.class)
                .switchIfEmpty(Mono.empty());
    }

    public Mono<List<BigQuestion>> findExerciseBook(final findExerciseBookReq req) {
        Criteria criteria = baseExerciseAnswerService
                .buildExerciseBook(new AnswerVo(req.getExeBookType(), req.getChapterId(), req.getCourseId(), req.getPreview(), req.getStudentId()));
        return reactiveMongoTemplate.findOne(Query.query(criteria), ExerciseAnswerQuestionBook.class)
                .defaultIfEmpty(new ExerciseAnswerQuestionBook())
                .flatMap(exerciseAnswerQuestionBook -> {
                    if(exerciseAnswerQuestionBook.getBigQuestionExerciseBook() == null){
                        return bigQuestionExerciseBookService.findExerciseBook(new ExerciseBookReq(req.getExeBookType(), req.getChapterId(), req.getCourseId(), req.getPreview()));
                    }else {
//                        return Mono.just(exerciseAnswerQuestionBook.getBigQuestionExerciseBook())
//                                .map(ExerciseBook::getQuestionChildren)
//                                .flatMapMany(Flux::fromIterable)
//                                .collectList();
                        return findQuestion(criteria, req.getPreview());
                    }
                });
    }
    private Mono<List<BigQuestion>> findQuestion(Criteria criteria, String preview){
        if (StrUtil.isNotBlank(preview)){
            criteria.and("bigQuestionExerciseBook.questionChildren.preview").is(preview);
        }

        Aggregation agg = newAggregation(
                unwind("bigQuestionExerciseBook.questionChildren"),
                match(criteria)
        );
        return reactiveMongoTemplate.aggregate(agg, "exerciseAnswerQuestionBook", UnwindedExerciseAnswerQuestionBook.class)
                .map(UnwindedExerciseAnswerQuestionBook::getBigQuestionExerciseBook)
                .map(UnwindedBigQuestionexerciseBook::getQuestionChildren)
                .collectList();
    }
}
package com.forteach.quiz.practiser.service;

import cn.hutool.core.util.StrUtil;
import com.forteach.quiz.evaluate.service.RewardService;
import com.forteach.quiz.practiser.domain.ExerciseAnswerQuestionBook;
import com.forteach.quiz.practiser.web.req.AnswerReq;
import com.forteach.quiz.practiser.web.vo.AnswerVo;
import com.forteach.quiz.problemsetlibrary.domain.BigQuestionExerciseBook;
import com.forteach.quiz.problemsetlibrary.service.BigQuestionExerciseBookService;
import com.forteach.quiz.questionlibrary.domain.BigQuestion;
import com.forteach.quiz.questionlibrary.repository.BigQuestionRepository;
import com.forteach.quiz.service.CorrectService;
import com.forteach.quiz.service.StudentsService;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.Objects;

import static com.forteach.quiz.common.Dic.BIG_QUESTION_EXAM_CHILDREN_TYPE_DESIGN;
import static com.forteach.quiz.common.Dic.MONGDB_ID;

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
    private final RewardService rewardService;
    private final BigQuestionRepository bigQuestionRepository;
    private final BaseExerciseAnswerService baseExerciseAnswerService;
    private final ExerciseAnswerService exerciseAnswerService;
    private final BigQuestionExerciseBookService bigQuestionExerciseBookService;

    @Autowired
    public ExerciseBookSnapshotService(ReactiveMongoTemplate reactiveMongoTemplate, ExerciseAnswerService exerciseAnswerService,
                                       BaseExerciseAnswerService baseExerciseAnswerService,
                                       BigQuestionExerciseBookService bigQuestionExerciseBookService,
                                       RewardService rewardService, BigQuestionRepository bigQuestionRepository,
                                       CorrectService correctService, StudentsService studentsService) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.studentsService = studentsService;
        this.correctService = correctService;
        this.rewardService = rewardService;
        this.baseExerciseAnswerService = baseExerciseAnswerService;
        this.bigQuestionRepository = bigQuestionRepository;
        this.exerciseAnswerService = exerciseAnswerService;
        this.bigQuestionExerciseBookService = bigQuestionExerciseBookService;
    }

    /**
     * 判断存在相关快照不
     *
     * @return
     */
    private Mono<Boolean> isExistSnapshot(final AnswerVo answerVo) {
        return reactiveMongoTemplate.exists(Query.query(baseExerciseAnswerService.buildExerciseBook(answerVo)),
                ExerciseAnswerQuestionBook.class);
    }

    public Mono<Boolean> saveSnapshot(final AnswerVo answerVo, final AnswerReq answerReq) {
        Criteria criteria = baseExerciseAnswerService.buildExerciseBook(answerVo);
        if (StrUtil.isNotBlank(answerReq.getQuestionId())) {
            criteria.and("bigQuestionExerciseBook.questionChildren.".concat(MONGDB_ID)).is(answerReq.getQuestionId());
        }

        Query query = Query.query(criteria);


        Update update = baseExerciseAnswerService.updateQuery(answerVo);

        if (StrUtil.isNotBlank(answerReq.getChapterName())) {
            update.set("chapterName", answerReq.getChapterName());
        }
        if (StrUtil.isNotBlank(answerReq.getAnswer())) {
            update.addToSet("bigQuestionExerciseBook.$.questionChildren", answerReq.getAnswer());
        }

        if (answerReq.getAnswerImageList() != null && !answerReq.getAnswerImageList().isEmpty()) {
            update.addToSet("bigQuestionExerciseBook.$.questionChildren.$.answerImageList", answerReq.getAnswerImageList());
        }
        if (answerReq.getFileList() != null && !answerReq.getFileList().isEmpty()) {
            update.addToSet("bigQuestionExerciseBook.$.questionChildren.$.fileList", answerReq.getFileList());
        }

        //判断学生答题结果　todo
        return isExistSnapshot(answerVo)
                .flatMap(b -> {
                    if (b) {
                        return saveAnswer(answerVo, answerReq, query, update);
                    }else {
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

    private Mono<Boolean> saveAnswer(final AnswerVo answerVo, final AnswerReq answerReq, final Query query, Update update){
        return judgedResult(answerVo, answerReq.getQuestionId(), answerReq.getAnswer())
                .flatMap(r -> {
                    update.addToSet("bigQuestionExerciseBook.$.questionChildren.$.right", r);
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
        if (StrUtil.isNotBlank(questionId)) {
            criteria.and("bigQuestionExerciseBook.questionChildren.".concat(MONGDB_ID)).is(questionId);
        }
        // 查找题目类型
        Mono<List<BigQuestion>> bigQuestions = reactiveMongoTemplate.findOne(Query.query(criteria), ExerciseAnswerQuestionBook.class)
                .filter(Objects::nonNull)
                .map(ExerciseAnswerQuestionBook::getBigQuestionExerciseBook)
                .map(BigQuestionExerciseBook::getQuestionChildren);
//                .log();

        Mono<BigQuestion> bigQuestionMono = bigQuestions
                .flatMapMany(Flux::fromIterable)
                .filter(Objects::nonNull)
                .filter(bigQuestion -> bigQuestion.getId().equals(questionId))
                .last();
        return Mono.zip(bigQuestions, bigQuestionMono)
                .map(Tuple2::getT2)
                .flatMap(bigQuestion -> {
                    return correctService.result(bigQuestion, answer);
                });
    }


    Mono<Boolean> setCorrectCompleted(final String exeBookType, final String chapterId, final String courseId,
                                      final String preview,
                                      final String classId, final String studentId, final String questionId, final String examType) {
        return Mono.just(examType).flatMap(type -> {
            if (BIG_QUESTION_EXAM_CHILDREN_TYPE_DESIGN.equals(type)) {
                return Mono.just(true);
            } else {
                return exerciseAnswerService.addCorrect(exeBookType, chapterId, courseId, preview, classId, studentId, questionId);
            }
        });
    }
}
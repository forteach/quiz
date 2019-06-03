package com.forteach.quiz.practiser.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.forteach.quiz.practiser.domain.AskAnswerExerciseBook;
import com.forteach.quiz.practiser.domain.AskAnswerStudents;
import com.forteach.quiz.practiser.web.req.AnswerReq;
import com.forteach.quiz.problemsetlibrary.service.BigQuestionExerciseBookService;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;

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
    private final BigQuestionExerciseBookService bigQuestionExerciseBookService;

    @Autowired
    public ExerciseBookAnswerService(ReactiveMongoTemplate reactiveMongoTemplate, BigQuestionExerciseBookService bigQuestionExerciseBookService) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.bigQuestionExerciseBookService = bigQuestionExerciseBookService;
    }


    public Mono<Boolean> saveAnswer(final AnswerReq answerReq) {
        //设置查询条件
        Criteria criteria = buildExerciseBook(answerReq.getExeBookType(), answerReq.getChapterId(), answerReq.getCourseId());

        if (StrUtil.isNotBlank(answerReq.getStudentId())){
            criteria.and("answerList.studentId").is(answerReq.getStudentId());
        }

        if (StrUtil.isNotBlank(answerReq.getQuestionId())) {
            criteria.and("answerList.questionId").is(answerReq.getQuestionId());
        }


        Query query = Query.query(criteria);

        // 修改答题记录
        Update update = Update.update("uDate", DateUtil.formatDateTime(new Date()));

        if (StrUtil.isNotBlank(answerReq.getAnswer())){
            update.set("answerList.$.answer", answerReq.getAnswer());
        }

        if(answerReq.getAnswerImageList() != null && !answerReq.getAnswerImageList().isEmpty()){
            update.set("answerList.$.answerImageList", answerReq.getAnswerImageList());
        }
        if (answerReq.getFileList() != null && !answerReq.getFileList().isEmpty()){
            update.set("answerList.$.fileList", answerReq.getFileList());
        }

        return reactiveMongoTemplate.updateMulti(query, update, AskAnswerExerciseBook.class)
                .map(UpdateResult::wasAcknowledged)
                .filterWhen(b -> {
                    //判断是否全部答题完毕 todo
                    return updateAskAnswer(answerReq, "");
                });
    }

    /**
     * 修改答题记录信息
     * @param answerReq
     * @param isAnswerCompleted
     * @return
     */
    private Mono<Boolean> updateAskAnswer(final AnswerReq answerReq, final String isAnswerCompleted){
        //查询
        Criteria criteria = buildExerciseBook(answerReq.getExeBookType(), answerReq.getChapterId(), answerReq.getCourseId());
        if (StrUtil.isNotBlank(answerReq.getStudentId())){
            criteria.and("studentId").is(answerReq.getStudentId());
        }

        Query query = Query.query(criteria);

        // 修改答题记录
        Update update = Update.update("uDate", DateUtil.formatDateTime(new Date()));
        if (StrUtil.isNotBlank(isAnswerCompleted)){
            update.set("isAnswerCompleted", isAnswerCompleted);
        }
        if (StrUtil.isNotBlank(answerReq.getQuestionId())){
            update.addToSet("questions", answerReq.getQuestionId());
        }

        return reactiveMongoTemplate.updateMulti(query, update, AskAnswerStudents.class)
                .map(UpdateResult::wasAcknowledged);
    }

    /**
     * 设置查询条件
     * @param exeBookType
     * @param chapterId
     * @param courseId
     * @return
     */
    private Criteria buildExerciseBook(final Integer exeBookType, final String chapterId, final String courseId) {

        Criteria criteria = new Criteria();

        if (StrUtil.isNotBlank(exeBookType.toString())) {
            criteria.and("exeBookType").in(exeBookType);
        }
        if (StrUtil.isNotBlank(chapterId)) {
            criteria.and("chapterId").in(chapterId);
        }
        if (StrUtil.isNotBlank(courseId)) {
            criteria.and("courseId").in(courseId);
        }
        return criteria;
    }
}

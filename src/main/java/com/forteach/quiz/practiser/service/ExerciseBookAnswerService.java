package com.forteach.quiz.practiser.service;

import cn.hutool.core.util.StrUtil;
import com.forteach.quiz.practiser.domain.AskAnswerExerciseBook;
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

        if (StrUtil.isNotBlank(answerReq.getQuestionId())) {
            criteria.and("answerList.questionId").is(answerReq.getQuestionId());
        }

        Query query = Query.query(criteria);

        Update update = new Update();
//        update.

        return reactiveMongoTemplate.updateMulti(query, update, AskAnswerExerciseBook.class).map(UpdateResult::wasAcknowledged);
    }

    public Criteria buildExerciseBook(final Integer exeBookType, final String chapterId, final String courseId) {

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

package com.forteach.quiz.problemsetlibrary.service.base;

import com.forteach.quiz.problemsetlibrary.domain.DelExerciseBookPartVo;
import com.forteach.quiz.problemsetlibrary.domain.base.ExerciseBook;
import com.forteach.quiz.problemsetlibrary.web.req.ExerciseBookReq;
import com.forteach.quiz.problemsetlibrary.web.vo.ProblemSetVo;
import com.forteach.quiz.questionlibrary.domain.base.QuestionExamEntity;
import com.mongodb.client.result.UpdateResult;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/13  21:49
 */
public interface BaseExerciseBookService<T extends ExerciseBook, R extends QuestionExamEntity> {

    /**
     * 按照顺序 保存练习册
     *
     * @param problemSetVo
     * @return
     */
    Mono<T> buildBook(final ProblemSetVo problemSetVo);

    /**
     * 查找挂接的课堂练习题
     *
     * @param sortVo
     * @return
     */
    Mono<List> findExerciseBook(final ExerciseBookReq sortVo);

    /**
     * 删除课堂练习题部分子文档
     *
     * @param delVo
     * @return
     */
    Mono<UpdateResult> delExerciseBookPart(final DelExerciseBookPartVo delVo);


}

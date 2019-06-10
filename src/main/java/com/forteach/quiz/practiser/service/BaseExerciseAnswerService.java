package com.forteach.quiz.practiser.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-10 16:28
 * @version: 1.0
 * @description:
 */
@Service
public class BaseExerciseAnswerService {

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

     Criteria queryCriteria(final String exeBookType, final String chapterId, final String courseId, final String preview, final String studentId, final String questionId, final String classId) {
        //设置查询条件
        Criteria criteria = buildExerciseBook(exeBookType, chapterId, courseId, preview, classId, studentId);

        if (StrUtil.isNotBlank(questionId)) {
            criteria.and("questionId").is(questionId);
        }
        return criteria;
    }

     Update updateQuery(final String exeBookType, final String chapterId, final String courseId, final String preview, final String studentId, final String classId) {
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
     * 判断是否回答完成
     *
     * @return
     */
     Mono<String> isCompletedSuccess(final List<String> answerQuestionIds, final List<String> questionIds) {
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
}

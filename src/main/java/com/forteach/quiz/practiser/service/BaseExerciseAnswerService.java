package com.forteach.quiz.practiser.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.forteach.quiz.practiser.web.vo.AnswerVo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.forteach.quiz.common.Dic.MONGDB_ID;

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
     * @return
     */
     Criteria buildExerciseBook(final AnswerVo answerVo) {

        Criteria criteria = new Criteria();

        if (StrUtil.isNotBlank(answerVo.getExeBookType())) {
            criteria.and("exeBookType").is(answerVo.getExeBookType());
        }
        if (StrUtil.isNotBlank(answerVo.getChapterId())) {
            criteria.and("chapterId").is(answerVo.getChapterId());
        }
        if (StrUtil.isNotBlank(answerVo.getCourseId())) {
            criteria.and("courseId").is(answerVo.getCourseId());
        }
        if (StrUtil.isNotBlank(answerVo.getPreview())) {
            criteria.and("preview").is(answerVo.getPreview());
        }
        if (StrUtil.isNotBlank(answerVo.getClassId())) {
            criteria.and("classId").is(answerVo.getClassId());
        }
        if (StrUtil.isNotBlank(answerVo.getStudentId())) {
            criteria.and("studentId").is(answerVo.getStudentId());
        }
        return criteria;
    }

     Criteria queryCriteria(final AnswerVo answerVo, final String questionId) {
        //设置查询条件
        Criteria criteria = buildExerciseBook(answerVo);

        if (StrUtil.isNotBlank(questionId)) {
            criteria.and("bigQuestionExerciseBook.questionChildren.".concat(MONGDB_ID)).is(new ObjectId(questionId));
        }
        return criteria;
    }

     Update updateQuery(final AnswerVo answerVo) {
        // 修改答题记录
        Update update = Update.update("uDate", DateUtil.formatDateTime(new Date()));
        if (StrUtil.isNotBlank(answerVo.getExeBookType())) {
            update.set("exeBookType", answerVo.getExeBookType());
        }
        if (StrUtil.isNotBlank(answerVo.getChapterId())) {
            update.set("chapterId", answerVo.getChapterId());
        }
        if (StrUtil.isNotBlank(answerVo.getCourseId())) {
            update.set("courseId", answerVo.getCourseId());
        }
        if (StrUtil.isNotBlank(answerVo.getPreview())) {
            update.set("preview", answerVo.getPreview());
        }
        if (StrUtil.isNotBlank(answerVo.getClassId())) {
            update.set("classId", answerVo.getClassId());
        }
        if (StrUtil.isNotBlank(answerVo.getStudentId())) {
            update.set("studentId", answerVo.getStudentId());
        }
        return update;
    }
}

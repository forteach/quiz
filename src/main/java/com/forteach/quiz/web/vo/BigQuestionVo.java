package com.forteach.quiz.web.vo;

import com.forteach.quiz.domain.BigQuestion;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/16  14:05
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BigQuestionVo extends BigQuestion {

    public BigQuestionVo() {
    }

    public BigQuestionVo(int index) {
        this.index = index;
    }

    public BigQuestionVo(int index, BigQuestion bigQuestion) {
        this.id = bigQuestion.getId();
        this.score = bigQuestion.getScore();
        this.teacherId = bigQuestion.getTeacherId();
        this.paperInfo = bigQuestion.getPaperInfo();
        this.examChildren = bigQuestion.getExamChildren();
        this.index = index;
    }

    public BigQuestionVo(String teacherId, List examChildren, Double score, int index) {
        super(teacherId, examChildren, score);
        this.index = index;
    }

    public BigQuestionVo(String teacherId, String paperInfo, List examChildren, Double score, int index) {
        super(teacherId, paperInfo, examChildren, score);
        this.index = index;
    }

    public BigQuestionVo(String id, Double score, String teacherId, String paperInfo, List examChildren, int index) {
        super(id, score, teacherId, paperInfo, examChildren);
        this.index = index;
    }
}

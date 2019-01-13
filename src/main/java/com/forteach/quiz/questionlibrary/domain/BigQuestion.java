package com.forteach.quiz.questionlibrary.domain;

import com.forteach.quiz.questionlibrary.domain.base.QuestionExamEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  11:04
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "bigQuestion")
@ApiModel(value = "题对象", description = "所有的题目类型 全部由大题外部封装   由examChildren展示具体的题目信息")
public class BigQuestion<T> extends QuestionExamEntity<T> {

    public BigQuestion() {
    }

    public BigQuestion(final String id, final List<T> examChildren) {
        this.id = id;
        this.setUDate(new Date());
        this.examChildren = examChildren;
    }

    public BigQuestion(String teacherId, List<T> examChildren, Double score) {
        this.score = score;
        this.teacherId = teacherId;
        this.examChildren = examChildren;
    }

    public BigQuestion(String teacherId, String paperInfo, List<T> examChildren, Double score) {
        this.score = score;
        this.teacherId = teacherId;
        this.paperInfo = paperInfo;
        this.examChildren = examChildren;
    }

    public BigQuestion(String id, Double score, String teacherId, String paperInfo, List<T> examChildren) {
        this.id = id;
        this.score = score;
        this.teacherId = teacherId;
        this.paperInfo = paperInfo;
        this.examChildren = examChildren;
    }

}

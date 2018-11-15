package com.forteach.quiz.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

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
public class BigQuestion<T> extends AbstractExam {

    private String paperInfo;

    private List<T> examChildren;

    private String type;

    public BigQuestion() {
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

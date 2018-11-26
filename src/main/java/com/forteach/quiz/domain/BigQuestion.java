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
public class BigQuestion<T> extends AbstractExamEntity {

    protected String paperInfo;

    protected List<T> examChildren;

    protected String type;

    protected int index;


    public BigQuestion() {
    }

    public BigQuestion(String paperInfo, List<T> examChildren, String type, int index) {
        this.paperInfo = paperInfo;
        this.examChildren = examChildren;
        this.type = type;
        this.index = index;
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

    public BigQuestion(int index, BigQuestion bigQuestion) {
        this.id = bigQuestion.getId();
        this.score = bigQuestion.getScore();
        this.teacherId = bigQuestion.getTeacherId();
        this.paperInfo = bigQuestion.getPaperInfo();
        this.examChildren = bigQuestion.getExamChildren();
        this.type = bigQuestion.getType();
        this.index = index;
    }
}

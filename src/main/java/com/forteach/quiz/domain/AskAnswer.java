package com.forteach.quiz.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/30  9:27
 */
@Data
@Document(collection = "askAnswer")
@EqualsAndHashCode(callSuper = true)
public class AskAnswer extends BaseEntity {

    /**
     * 学生id
     */
    private String examineeId;
    /**
     * 参与方式
     */
    private String participate;
    /**
     * 答案
     */
    private String answ;
    /**
     * 问题id
     */
    private String questionId;

    public AskAnswer() {
    }

    public AskAnswer(String examineeId, String participate, String answ, String questionId, Date cDate, Date uDate) {
        this.examineeId = examineeId;
        this.participate = participate;
        this.answ = answ;
        this.questionId = questionId;
        this.cDate = cDate;
        this.uDate = uDate;
    }
}


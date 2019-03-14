package com.forteach.quiz.interaction.execute.domain.record;

import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/3  15:15
 */
@Data
public class InteractAnswerRecord {

    /**
     * 回答的学生id
     */
    private String examineeId;

    /**
     * 回答的答案
     */
    private String answer;

    /**
     * 回答对错
     */
    private String right;

    /**
     * 回答时间
     */
    private Date time;

    public InteractAnswerRecord() {
    }

    public InteractAnswerRecord(String examineeId, String answer, String right) {
        this.examineeId = examineeId;
        this.answer = answer;
        this.right = right;
        this.time = new Date();
    }

    public InteractAnswerRecord(String examineeId, String answer) {
        this.examineeId = examineeId;
        this.answer = answer;
        this.time = new Date();
    }
}

package com.forteach.quiz.interaction.execute.web.resp;

import com.forteach.quiz.web.pojo.Students;
import lombok.Builder;
import lombok.Data;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-3-20 15:41
 * @version: 1.0
 * @description:
 */
@Data
@Builder
public class InteractAnswerRecordResp {
    /**
     * 回答的学生id
     */
    private Students student;

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
    private String time;

    public InteractAnswerRecordResp() {
    }

    public InteractAnswerRecordResp(Students student, String answer, String right, String time) {
        this.student = student;
        this.answer = answer;
        this.right = right;
        this.time = time;
    }
}

package com.forteach.quiz.interaction.execute.web.resp;

import com.forteach.quiz.web.pojo.Students;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-2-26 16:22
 * @version: 1.0
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InteractAnswerRecordResp {

    /**
     * 回答的学生id
     */
    private Students students;

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
}

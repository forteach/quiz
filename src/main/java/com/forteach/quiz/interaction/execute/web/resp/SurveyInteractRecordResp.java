package com.forteach.quiz.interaction.execute.web.resp;

import com.forteach.quiz.interaction.execute.domain.InteractAnswerRecord;
import com.forteach.quiz.web.pojo.Students;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-2-26 14:59
 * @version: 1.0
 * @description:
 */
@Data
@NoArgsConstructor
public class SurveyInteractRecordResp {
    /**
     * 发布顺序
     */
    private Integer index;

    /**
     * 发布时间
     */
    private Date time;

    /**
     * 发布次数
     */
    private Integer number;

    /**
     * 发布类别 ,个人 , 小组
     */
    private String category;

    /**
     * 选择回答人id
     */
    private List<Students> students;

    /**
     * 回答的人数
     */
    private Integer answerNumber;

    /**
     * 问题id
     */
    private String[] questionsId;

    /**
     * 回答的情况
     */
    private List<InteractAnswerRecord> answerRecordList;
}

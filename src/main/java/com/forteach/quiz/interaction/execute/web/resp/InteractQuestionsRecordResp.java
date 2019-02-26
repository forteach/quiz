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
 * @date: 19-2-26 15:01
 * @version: 1.0
 * @description:
 */
@Data
@NoArgsConstructor
public class InteractQuestionsRecordResp {
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
     * 发布类别 ,个人(people) , 小组(team)
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
    private String questionsId;

    /**
     * 提问方式
     */
    private String interactive;

    /**
     * 举手的人数
     */
    private Integer raiseHandsNumber;

    /**
     * 举手的人数id
     */
    private List<Students> raiseHandsId;

    /**
     * 回答正确数量
     */
    private Integer correctNumber;

    /**
     * 回答错误的数量
     */
    private Integer errorNumber;

    /**
     * 回答的情况
     */
    private List<InteractAnswerRecord> answerRecordList;
}

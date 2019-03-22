package com.forteach.quiz.interaction.execute.web.resp;

import com.forteach.quiz.web.pojo.Students;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-3-20 15:38
 * @version: 1.0
 * @description:
 */
@Data
@Builder
public class InteractRecordResp implements Serializable {
    /**
     * 发布顺序
     */
    private Integer index;

    /**
     * 发布时间
     */
    protected String time;

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
    private List<InteractAnswerRecordResp> answerRecordList;

    public InteractRecordResp() {
    }

    public InteractRecordResp(Integer index, String time, Integer number, String category, List<Students> students,
                              Integer answerNumber, String questionsId, String interactive, Integer raiseHandsNumber,
                              List<Students> raiseHandsId, Integer correctNumber, Integer errorNumber,
                              List<InteractAnswerRecordResp> answerRecordList) {
        this.index = index;
        this.time = time;
        this.number = number;
        this.category = category;
        this.students = students;
        this.answerNumber = answerNumber;
        this.questionsId = questionsId;
        this.interactive = interactive;
        this.raiseHandsNumber = raiseHandsNumber;
        this.raiseHandsId = raiseHandsId;
        this.correctNumber = correctNumber;
        this.errorNumber = errorNumber;
        this.answerRecordList = answerRecordList;
    }
}

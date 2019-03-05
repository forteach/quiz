package com.forteach.quiz.interaction.execute.domain;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/3  15:01
 */
@Data
public class InteractQuestionsRecord {

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
    private List<String> selectId;

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
    private List<String> raiseHandsId;

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

    public InteractQuestionsRecord() {
    }

    public InteractQuestionsRecord(String questionsId, Long index, String interactive, String category, List<String> selectId) {
        this.questionsId = questionsId;
        this.index = index.intValue();
        this.interactive = interactive;
        this.category = category;
        this.selectId = selectId;
        this.time = new Date();
    }

    public InteractQuestionsRecord(String questionsId, Long index, List<String> selectId) {
        this.questionsId = questionsId;
        this.index = index.intValue();
        this.selectId = selectId;
        this.time = new Date();
    }
}

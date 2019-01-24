package com.forteach.quiz.interaction.execute.domain;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/14  17:16
 */
@Data
public class SurveyInteractRecord {

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
    private List<String> selectId;

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
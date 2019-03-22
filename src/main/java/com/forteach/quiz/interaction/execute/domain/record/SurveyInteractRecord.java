package com.forteach.quiz.interaction.execute.domain.record;

import com.forteach.quiz.common.DataUtil;
import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * @Description:   问卷记录(发布了哪些题目 发布顺序 发布时间 发布次数 选择了哪些人 哪些人进行了回答 回答情况 回答答案)
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
    private String time;

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
    private String questionsId;

    /**
     * 回答的情况
     */
    private List<InteractAnswerRecord> answerRecordList;

    public SurveyInteractRecord(String questionsId, Long index, String category, List<String> selectId) {
        this.questionsId = questionsId;
        this.index = index.intValue();
        this.category = category;
        this.selectId = selectId;
        this.time = DataUtil.format(new Date());
    }

    public SurveyInteractRecord() {
    }
}

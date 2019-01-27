package com.forteach.quiz.interaction.execute.domain;

import com.forteach.quiz.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * @Description: 记录课堂的交互情况 学生回答情况
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/3  14:23
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "interactRecord")
public class InteractRecord extends BaseEntity {

    /**
     * 上课教师id
     */
    private String teacherId;

    /**
     * 今天进行的第几次课
     */
    private Integer number;

    /**
     * 互动课堂id
     */
    private String circleId;

    /**
     * 参加过课堂的学生id
     */
    private List<String> students;

    /**
     * 参加的学生数
     */
    private Integer participate;

    /**
     * 进行提问过的问题(发布了哪些题目 发布顺序 发布时间 发布次数 选择了哪些人 哪些人进行了回答 回答情况 回答答案)
     */
    private List<InteractQuestionsRecord> questions;

    /**
     * 头脑风暴记录
     */
    private List<BrainstormInteractRecord> brainstorms;

    /**
     * 任务记录
     */
    private List<TaskInteractRecord> interacts;

    /**
     * 问卷记录(发布了哪些题目 发布顺序 发布时间 发布次数 选择了哪些人 哪些人进行了回答 回答情况 回答答案)
     */
    private List<SurveyInteractRecord> surveys;

    /**
     * 上课时间
     */
    private Date time;

    public InteractRecord() {
    }

    public InteractRecord(String circleId, String teacherId, Long number) {
        this.teacherId = teacherId;
        this.circleId = circleId;
        this.time = new Date();
        this.number = number.intValue();
    }
}

package com.forteach.quiz.interaction.execute.domain;

import com.forteach.quiz.common.DataUtil;
import com.forteach.quiz.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
     * 今天创建的第几次课
     */
    private Integer number;

    /**
     * 加入课堂的学生id
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
     * 创建课堂时间
     */
    private String ceateTime;

    public InteractRecord() {
    }

    public InteractRecord(String circleId, String teacherId, Long number) {
        this.teacherId = teacherId;
        this.id = circleId;
        this.ceateTime = DataUtil.format(new Date());
        this.uDate=DataUtil.format(new Date());
        this.number = number.intValue();
    }

    public InteractRecord( String teacherId, Long number) {
        this.teacherId = teacherId;
        this.ceateTime = DataUtil.format(new Date());
        this.uDate=DataUtil.format(new Date());
        this.number = number.intValue();
    }
}

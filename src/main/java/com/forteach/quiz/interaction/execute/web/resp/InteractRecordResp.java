package com.forteach.quiz.interaction.execute.web.resp;

import com.forteach.quiz.interaction.execute.domain.BrainstormInteractRecord;
import com.forteach.quiz.interaction.execute.domain.InteractQuestionsRecord;
import com.forteach.quiz.interaction.execute.domain.SurveyInteractRecord;
import com.forteach.quiz.interaction.execute.domain.TaskInteractRecord;
import com.forteach.quiz.web.pojo.Students;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-2-26 14:56
 * @version: 1.0
 * @description:
 */
@Data
@NoArgsConstructor
public class InteractRecordResp implements Serializable {
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
    private List<Students> students;

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
}

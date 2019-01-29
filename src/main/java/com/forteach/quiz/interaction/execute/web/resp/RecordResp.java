package com.forteach.quiz.interaction.execute.web.resp;

import com.forteach.quiz.interaction.execute.domain.*;
import com.forteach.quiz.web.pojo.Students;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-1-28 14:49
 * @version: 1.0
 * @description:
 */
@Builder
@Data
public class RecordResp {
    /**
     * 学生信息
     */
    @ApiModelProperty(value = "学生信息", name = "students")
    private List<Students> students;
    /**
     * 互动方式
     * <p>
     * race   : 抢答
     * raise  : 举手
     * select : 选则
     * vote   : 投票
     */
    @ApiModelProperty(value = "互动方式 race   : 抢答/raise  : 举手/select : 选择/vote   : 投票", name = "interactive", notes = "race   : 抢答/raise  : 举手/select : 选择/vote   : 投票")
    private String interactive;

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
}

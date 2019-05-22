package com.forteach.quiz.evaluate.web.control.req;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


/**
 * @Description: 对学生参与活动的奖励列表
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/11  14:14
 */
@Data
@ApiModel(value = "奖励累加", description = "对学生进行奖励累加")
public class CumulativeReq  {

    private String circleId;

    /**
     * 被累加的学生id
     */
    @ApiModelProperty(value = "被累加的学生id", name = "studentId")
    private String studentId;

    /**
     * 增加值
     */
    @ApiModelProperty(value = "增加值", name = "num")
    private String num;

    /**
     * 教师Id
     */
    @ApiModelProperty(value = "教师id", name = "teacherId")
    private String teacherId;


    @ApiModelProperty(value = "奖励类别：小红花", name = "rewardType")
    private String rewardType;

    @ApiModelProperty(value = "题目Id", name = "questionId")
    private String questionId;

    @ApiModelProperty(value = "题目互动活动 提问、任务", name = "questionType")
    private String questionType;

    @ApiModelProperty(value = "修改前", name = "preValue")
    private String preValue;

    @ApiModelProperty(value = "修改后", name = "nextValue")
    private String nextValue;


}

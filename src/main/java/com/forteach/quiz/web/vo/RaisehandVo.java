package com.forteach.quiz.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/4  15:02
 */
@Data
@ApiModel(value = "学生举手", description = "通过课堂圈子id 及 学生id举手")
public class RaisehandVo {

    /**
     * 学生id
     */
    @ApiModelProperty(value = "学生id", name = "examineeId")
    private String examineeId;

    /**
     * 课堂圈子id
     */
    @ApiModelProperty(value = "课堂圈子id", name = "circleId")
    private String circleId;

    /**
     * 课堂圈子id
     */
    @ApiModelProperty(value = "课堂圈子id", name = "questionId")
    private String questionId;

    /**
     * 题目交互类型 提问、抢答
     */
    @ApiModelProperty(value = "课堂圈子id", name = "questionId")
    private String questionType;
}

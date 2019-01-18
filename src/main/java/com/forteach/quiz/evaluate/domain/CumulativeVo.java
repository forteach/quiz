package com.forteach.quiz.evaluate.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 对学生进行累加
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/11  14:14
 */
@Data
@ApiModel(value = "奖励累加", description = "对学生进行奖励累加")
public class CumulativeVo {

    @ApiModelProperty(value = "被累加的学生id", name = "studentId")
    private String studentId;

    @ApiModelProperty(value = "增加值", name = "amount")
    private String amount;

    @ApiModelProperty(value = "教师id", name = "teacher")
    private String teacher;
}

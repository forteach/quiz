package com.forteach.quiz.evaluate.web.control.res;


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
@Builder
@AllArgsConstructor
@ApiModel(value = "奖励累加", description = "对学生进行奖励累加")
public class CumulativeRes {

    private String circleId;

    /**
     * 被累加的学生id
     */
    @ApiModelProperty(value = "被累加的学生id", name = "studentId")
    private String studentId;

    /**
     * 增加值
     */
    @ApiModelProperty(value = "数量", name = "count")
    private String count;

    @ApiModelProperty(value = "奖励类别：小红花", name = "rewardType")
    private String rewardType;
}

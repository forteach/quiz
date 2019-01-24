package com.forteach.quiz.interaction.team.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/23  11:00
 */
@Data
@ApiModel(value = "随机分组", description = "")
public class GroupRandomVo {

    /**
     * 课堂圈子id
     */
    @ApiModelProperty(value = "课堂圈子id", name = "circleId")
    private String circleId;

    /**
     * 共分几组
     */
    @ApiModelProperty(value = "要分分几个组", name = "number")
    private Integer number;

}

package com.forteach.quiz.interaction.execute.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@ApiModel(value = "获得已发布题目的列表状态")
@NoArgsConstructor
@AllArgsConstructor
public class DelSelectStuVo {

    @ApiModelProperty(value = "课堂id", name = "circleId")
    protected String circleId;

    @ApiModelProperty(value="学生id")
    private String stuId;

}

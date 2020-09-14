package com.forteach.quiz.interaction.execute.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@ApiModel(value = "获得已发布题目的列表状态")
@NoArgsConstructor
@AllArgsConstructor
public class QuestFabuListVo implements Serializable {

    @ApiModelProperty(value = "课堂id", name = "circleId")
    protected String circleId;

//    @ApiModelProperty(value="题目类型")
//    private String questType;

//    @ApiModelProperty(value = "互动方式 race   : 抢答/raise  : 举手/select : 选择/vote   : 投票", name = "interactive", notes = "race   : 抢答/raise  : 举手/select : 选择/vote   : 投票")
//    private String interactive;
}

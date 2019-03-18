package com.forteach.quiz.interaction.execute.web.control.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@ApiModel(value = "返回已发布题目的列表状态")
@NoArgsConstructor
@AllArgsConstructor
public class QuestFabuListResponse {

    @ApiModelProperty(value = "课堂圈子id", name = "circleId")
    protected String circleId;
    //已发布题目的列表
    @ApiModelProperty(value="已发布题目的列表")
    private List<String> fabuQuest;
    //当前题目Id
    @ApiModelProperty(value="当前题目Id")
    private String nowQuestId;

}

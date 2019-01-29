package com.forteach.quiz.interaction.execute.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/17  14:51
 */
@Data
@ApiModel(value = "互动活动答题卡", description = "互动活动答题卡")
public class InteractiveSheetAnsw {

    /**
     * 问题id
     */
    @ApiModelProperty(value = "问题id", name = "questionId")
    private String questionId;

    /**
     * 答案
     */
    @ApiModelProperty(value = "答案", name = "answer")
    private String answer;

    private Date date;

}

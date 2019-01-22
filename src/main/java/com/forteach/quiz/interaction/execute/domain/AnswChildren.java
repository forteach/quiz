package com.forteach.quiz.interaction.execute.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/21  9:10
 */
@Data
@ApiModel(value = "互动活动答题卡 答案", description = "互动活动答题卡 答案")
public class AnswChildren {

    /**
     * 大题下子项id
     */
    @ApiModelProperty(value = "大题下子项id", name = "questionId")
    private String questionId;

    /**
     * 学生答案
     */
    @ApiModelProperty(value = "大题下子项id", name = "questionId")
    private String answer;

    /**
     * 答题得分
     */
    private Double score;

    /**
     * 答案评价  主观题: {人工输入:优.良.中.差}    客观题: true  false  halfOf
     */
    @ApiModelProperty(value = "大题下子项id", name = "questionId")
    private String evaluation;
}

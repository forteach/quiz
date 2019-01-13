package com.forteach.quiz.questionlibrary.domain.question;

import com.fasterxml.jackson.annotation.JsonView;
import com.forteach.quiz.questionlibrary.domain.base.AbstractExam;
import com.forteach.quiz.web.vo.BigQuestionView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/13  11:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "判断题", description = "BigQuestion的子项")
public class TrueOrFalse extends AbstractExam {

    @JsonView(BigQuestionView.Summary.class)
    @ApiModelProperty(value = "题目题干", name = "trueOrFalseInfo", required = true, example = "亚特兰蒂斯是否存在")
    private String trueOrFalseInfo;

    @JsonView(BigQuestionView.SummaryWithDetail.class)
    @ApiModelProperty(value = "题目答案", name = "trueOrFalseAnsw", required = true, example = "true")
    private Boolean trueOrFalseAnsw;

    @JsonView(BigQuestionView.SummaryWithDetail.class)
    @ApiModelProperty(value = "题目解析", name = "trueOrFalseAnalysis", required = true, example = "历史存在 现与海底")
    private String trueOrFalseAnalysis;

}

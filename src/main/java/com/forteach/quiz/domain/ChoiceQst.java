package com.forteach.quiz.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.forteach.quiz.web.vo.BigQuestionView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  10:00
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "选择题", description = "BigQuestion的子项 多选及单选")
public class ChoiceQst extends AbstractExam {

    @JsonView(BigQuestionView.Summary.class)
    @ApiModelProperty(value = "题目题干", name = "choiceQstTxt", required = true, example = "1+1 = ?")
    private String choiceQstTxt;

    @JsonView(BigQuestionView.SummaryWithDetail.class)
    @ApiModelProperty(value = "题目答案", name = "choiceQstAnsw", required = true, example = "A")
    private String choiceQstAnsw;

    @JsonView(BigQuestionView.SummaryWithDetail.class)
    @ApiModelProperty(value = "题目解析", name = "choiceQstAnalysis", example = "A选项正确")
    private String choiceQstAnalysis;

    @JsonView(BigQuestionView.Summary.class)
    @ApiModelProperty(value = "单选与多选区分 single  multiple", name = "choiceType", required = true, example = "single")
    private String choiceType;

    @JsonView(BigQuestionView.Summary.class)
    @ApiModelProperty(value = "选项集", name = "optChildren", required = true)
    private List<ChoiceQstOption> optChildren;
}

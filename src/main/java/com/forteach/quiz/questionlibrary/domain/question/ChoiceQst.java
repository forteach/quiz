package com.forteach.quiz.questionlibrary.domain.question;

import com.fasterxml.jackson.annotation.JsonView;
import com.forteach.quiz.questionlibrary.domain.base.AbstractExam;
import com.forteach.quiz.web.vo.BigQuestionView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description: 选择题
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  10:00
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "选择题", description = "BigQuestion的子项 多选及单选")
public class ChoiceQst extends AbstractExam {

    /**
     * 题目题干
     */
    @JsonView(BigQuestionView.Summary.class)
    @ApiModelProperty(value = "题目题干", name = "choiceQstTxt", required = true, example = "1+1 = ?")
    private String choiceQstTxt;

    /**
     * 题目答案
     */
    @JsonView(BigQuestionView.SummaryWithDetail.class)
    @ApiModelProperty(value = "题目答案", name = "choiceQstAnsw", required = true, example = "A")
    private String choiceQstAnsw;

    /**
     * 题目解析
     */
    @JsonView(BigQuestionView.SummaryWithDetail.class)
    @ApiModelProperty(value = "题目解析", name = "choiceQstAnalysis", example = "A选项正确")
    private String choiceQstAnalysis;

    /**
     * 单选与多选区分 single  multiple
     */
    @JsonView(BigQuestionView.Summary.class)
    @ApiModelProperty(value = "单选与多选区分 single  multiple", name = "choiceType", required = true, example = "single")
    private String choiceType;

    /**
     * 选项集
     */
    @JsonView(BigQuestionView.Summary.class)
    @ApiModelProperty(value = "选项集", name = "optChildren", required = true)
    private List<ChoiceQstOption> optChildren;
}

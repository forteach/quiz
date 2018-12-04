package com.forteach.quiz.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.forteach.quiz.web.vo.BigQuestionView;
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
public class ChoiceQst extends AbstractExam {

    @JsonView(BigQuestionView.Summary.class)
    private String choiceQstTxt;
    @JsonView(BigQuestionView.SummaryWithDetail.class)
    private String choiceQstAnsw;
    @JsonView(BigQuestionView.SummaryWithDetail.class)
    private String choiceQstAnalysis;

    /**
     *
     */
    @JsonView(BigQuestionView.Summary.class)
    private String choiceType;

    @JsonView(BigQuestionView.Summary.class)
    private List<ChoiceQstOption> optChildren;
}

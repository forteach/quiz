package com.forteach.quiz.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.forteach.quiz.web.vo.BigQuestionView;
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
public class TrueOrFalse extends AbstractExam {

    @JsonView(BigQuestionView.Summary.class)
    private String trueOrFalseInfo;
    @JsonView(BigQuestionView.SummaryWithDetail.class)
    private Boolean trueOrFalseAnsw;
    @JsonView(BigQuestionView.SummaryWithDetail.class)
    private String trueOrFalseAnalysis;

}

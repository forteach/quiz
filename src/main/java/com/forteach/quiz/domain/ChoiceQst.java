package com.forteach.quiz.domain;

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

    private String choiceQstTxt;

    private String choiceQstAnsw;

    private String choiceQstAnalysis;

    /**
     *
     */
    private String choiceType;

    private List<ChoiceQstOption> optChildren;
}

package com.forteach.quiz.domain;

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


    private String trueOrFalseInfo;

    private boolean trueOrFalseAnsw;

    private String trueOrFalseAnalysis;

}

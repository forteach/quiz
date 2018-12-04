package com.forteach.quiz.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.forteach.quiz.web.vo.BigQuestionView;
import lombok.Data;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  0:15
 */
@Data
public abstract class AbstractExam {

    @JsonView(BigQuestionView.Summary.class)
    protected String id;

    protected Double score;

    /**
     * 创作老师
     */
    protected String teacherId;

    /**
     * 考题类型   choice   trueOrFalse    design
     */
    @JsonView(BigQuestionView.Summary.class)
    protected String examType;

    /**
     * 是否修改应用到所有的练习册
     * 1 : 应用到所有练习册    0  :  只修改本题
     */
    private int relate;

}

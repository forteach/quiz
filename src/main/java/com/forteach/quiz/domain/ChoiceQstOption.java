package com.forteach.quiz.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.forteach.quiz.web.vo.BigQuestionView;
import lombok.Data;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  10:10
 */
@Data
public class ChoiceQstOption {

    @JsonView(BigQuestionView.Summary.class)
    private String id;

    @JsonView(BigQuestionView.Summary.class)
    private String optTxt;

    @JsonView(BigQuestionView.Summary.class)
    private String optValue;

}

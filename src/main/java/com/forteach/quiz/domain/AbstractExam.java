package com.forteach.quiz.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.forteach.quiz.web.vo.BigQuestionView;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "题目id", name = "id", required = true, example = "463bcd8e5fed4a33883850c14f877271")
    protected String id;

    @ApiModelProperty(value = "题目分数", name = "score", required = true, example = "2.0")
    protected Double score;

    /**
     * 创作老师
     */
    @ApiModelProperty(value = "创作老师id", name = "teacherId", example = "463bcd8e5fed4a33883850c14f877271")
    protected String teacherId;

    /**
     * 考题类型   choice   trueOrFalse    design
     */
    @JsonView(BigQuestionView.Summary.class)
    @ApiModelProperty(value = "暂不需要 考题类型 choice  trueOrFalse  design", name = "id", example = "trueOrFalse")
    protected String examType;

    /**
     * 是否修改应用到所有的练习册
     * 1 : 应用到所有练习册    0  :  只修改本题
     */
    @ApiModelProperty(value = "题目id", name = "id", example = "0")
    private int relate;

}

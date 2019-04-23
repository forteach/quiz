package com.forteach.quiz.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/29  14:48
 */
@Data
@ApiModel(value = "学生提交答案", description = "学生提交答案 需要传入接收的题目的cut值")
public class InteractAnswerVo {

    public InteractAnswerVo() {
    }

    /**
     * 学生id
     */
//    @ApiModelProperty(value = "学生id", name = "examineeId")
    private String examineeId;

    /**
     * 课堂圈子id
     */
    @ApiModelProperty(value = "课堂圈子id", name = "circleId")
    private String circleId;

    /**
     * 问题id
     */
    @ApiModelProperty(value = "问题id", name = "questionId")
    private String questionId;

    /**
     * 答案
     */
    @ApiModelProperty(value = "答案", name = "answer")
    private String answer;

    /**
     * 切换提问类型过期标识
     */
    @ApiModelProperty(value = "切换提问类型过期标识  接收的该题cut", name = "cut")
    private String cut;

    public InteractAnswerVo(String examineeId, String circleId, String questionId, String answer, String cut) {
        this.examineeId = examineeId;
        this.circleId = circleId;
        this.questionId = questionId;
        this.answer = answer;
        this.cut = cut;
    }

}

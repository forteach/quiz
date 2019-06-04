package com.forteach.quiz.practiser.web.resp;

import com.forteach.quiz.web.pojo.Students;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-4 14:05
 * @version: 1.0
 * @description:
 */
@Data
@ApiModel(value = "学生回答的情况")
@EqualsAndHashCode(callSuper = true)
public class AnswerStudentResp extends Students implements Serializable {
    @ApiModelProperty(name = "questions", value = "问题id")
    private List<String> questions;

    @ApiModelProperty(name = "isAnswerCompleted", value = "是否答完题 Y/N")
    private String isAnswerCompleted;


    public AnswerStudentResp(List<String> questions, String isAnswerCompleted) {
        this.questions = questions;
        this.isAnswerCompleted = isAnswerCompleted;
    }

    public AnswerStudentResp(String id, String name, String portrait, List<String> questions, String isAnswerCompleted) {
        super(id, name, portrait);
        this.questions = questions;
        this.isAnswerCompleted = isAnswerCompleted;
    }
}

package com.forteach.quiz.practiser.web.resp;

import com.forteach.quiz.web.pojo.Students;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

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
    @ApiModelProperty(name = "questionId", value = "问题id")
    private String questionId;

    @ApiModelProperty(name = "isAnswerCompleted", value = "是否答完题 Y/N")
    private String isAnswerCompleted;


    public AnswerStudentResp(String questionId, String isAnswerCompleted) {
        this.questionId = questionId;
        this.isAnswerCompleted = isAnswerCompleted;
    }

    public AnswerStudentResp(String id, String name, String portrait, String questionId, String isAnswerCompleted) {
        super(id, name, portrait);
        this.questionId = questionId;
        this.isAnswerCompleted = isAnswerCompleted;
    }
}

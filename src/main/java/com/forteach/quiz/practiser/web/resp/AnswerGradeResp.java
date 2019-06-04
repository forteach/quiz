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
 * @date: 19-6-4 15:11
 * @version: 1.0
 * @description:
 */
@Data
@ApiModel(value = "学生回答的情况")
@EqualsAndHashCode(callSuper = true)
public class AnswerGradeResp extends Students implements Serializable {

    @ApiModelProperty(name = "questionId", value = "问题id")
    private String questionId;


//    @ApiModelProperty(name = "isGradeCompleted", value = "是否批改完 Y/N")
//    private String isGradeCompleted;

    public AnswerGradeResp(String questionId) {
        this.questionId = questionId;
    }

    public AnswerGradeResp(String id, String name, String portrait, String questionId) {
        super(id, name, portrait);
        this.questionId = questionId;
    }
}

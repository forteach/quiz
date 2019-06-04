package com.forteach.quiz.practiser.web.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-4 17:14
 * @version: 1.0
 * @description:
 */
@Data
public class AnswerGradeListResp implements Serializable {

    @ApiModelProperty(name = "answerGradeResps", value = "批改的习题列表")
    private List<AnswerGradeResp> answerGradeResps;

    public AnswerGradeListResp(List<AnswerGradeResp> answerGradeResps) {
        this.answerGradeResps = answerGradeResps;
    }
}

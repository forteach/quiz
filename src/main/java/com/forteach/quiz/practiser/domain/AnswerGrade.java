package com.forteach.quiz.practiser.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-3 16:43
 * @version: 1.0
 * @description:
 */
@Data
public class AnswerGrade {

    @ApiModelProperty(name = "studentId", value = "学生id")
    private String studentId;

    @ApiModelProperty(name = "questionId", value = "问题id")
    private String questionId;

}

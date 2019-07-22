package com.forteach.quiz.practiser.web.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-13 17:12
 * @version: 1.0
 * @description:
 */
@Data
public class AnswerResp implements Serializable {

    @ApiModelProperty(name = "studentId", value = "学生id", dataType = "string")
    private String studentId;

    @ApiModelProperty(name = "studentName", value = "学生名称", dataType = "string")
    private String studentName;

    @ApiModelProperty(value = "学生头像", name = "portrait", dataType = "string")
    private String portrait;

    @ApiModelProperty(name = "bigQuestionExerciseBooks", value = "习题快照和答题内容")
    private List<BigQuestionExerciseBookResp> bigQuestionExerciseBooks;

    public AnswerResp() {
    }

    public AnswerResp(String studentId, String studentName, String portrait) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.portrait = portrait;
    }
}

package com.forteach.quiz.testpaper.web.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/9/11 14:37
 * @Version: v1.0
 * @Modified：学生回答试卷题信息
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "学生回答练习册")
public class AddResultReq implements Serializable {

    @NotBlank(message = "试卷id不能为空")
    @ApiModelProperty(name = "testPaperId", value = "试卷id", dataType = "string", required = true)
    private String testPaperId;

    @NotBlank(message = "试卷名称不能为空")
    @ApiModelProperty(name = "testPaperName", value = "试卷名称", dataType = "string", required = true)
    private String testPaperName;

    @NotBlank(message = "习题id不能为空")
    @ApiModelProperty(name = "questionId", value = "习题id", dataType = "string", required = true)
    private String questionId;

    @NotBlank(message = "习题类型不能为空")
    @ApiModelProperty(name = "questionType", value = "习题类型", dataType = "string", required = true)
    private String questionType;

    @NotBlank(message = "回答内容为空")
    @ApiModelProperty(name = "answer", value = "回答内容", dataType = "string", required = true)
    private String answer;

    @NotBlank(message = "学生id不为空")
    @ApiModelProperty(name = "studentId", value = "学生id", dataType = "string", required = true)
    private String studentId;

    @NotBlank(message = "学生名称不为空")
    @ApiModelProperty(name = "studentName", value = "学生名称", dataType = "string", required = true)
    private String studentName;

    @ApiModelProperty(name = "classId", value = "班级id", dataType = "string", required = true)
    private String classId;

    @NotBlank(message = "班级名称不能为空")
    @ApiModelProperty(name = "className", value = "班级名称", dataType = "string", required = true)
    private String className;

    @NotBlank(message = "课程id不为空")
    @ApiModelProperty(name = "courseId", value = "课程Id", dataType = "string", required = true)
    private String courseId;

    @NotBlank(message = "课程名称不为空")
    @ApiModelProperty(name = "courseName", value = "课程名称", dataType = "string", required = true)
    private String courseName;
}
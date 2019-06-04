package com.forteach.quiz.practiser.web.req;

import com.forteach.quiz.practiser.web.req.base.AbstractReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-4 15:18
 * @version: 1.0
 * @description:
 */
@Data
@ApiModel(value = "查询教师批改作业情况")
@EqualsAndHashCode(callSuper = true)
public class FindAnswerGradeReq extends AbstractReq implements Serializable {

    @ApiModelProperty(name = "studentId", value = "学生id", dataType = "string")
    private String studentId;

    @ApiModelProperty(name = "questionId", value = "习题id", dataType = "string")
    private String questionId;

    @ApiModelProperty(hidden = true)
    private String teacherId;

}

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
 * @date: 19-6-4 14:11
 * @version: 1.0
 * @description:
 */
@Data
@ApiModel(value = "查询习题回答情况")
@EqualsAndHashCode(callSuper = true)
public class FindAnswerStudentReq extends AbstractReq implements Serializable {

    @ApiModelProperty(value = "学生id", name = "studentId", dataType = "string")
    private String studentId;

    @ApiModelProperty(name = "isAnswerCompleted", value = "是否提交过答案的 Y/N", dataType = "string", required = true)
    private String isAnswerCompleted;

    @ApiModelProperty(name = "isCorrectCompleted", value = "是否批改完作业　Y/N", dataType = "string", required = true)
    private String isCorrectCompleted;

}

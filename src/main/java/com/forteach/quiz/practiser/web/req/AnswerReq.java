package com.forteach.quiz.practiser.web.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-3 09:43
 * @version: 1.0
 * @description:
 */
@Data
@ApiModel(value = "回答记录")
public class RnswerReq implements Serializable {

    @ApiModelProperty(name = "courseId", value = "课程id", dataType = "string")
    private String courseId;

    @ApiModelProperty(name = "chapterId", value = "章节id", dataType = "string")
    private String chapterId;

    @ApiModelProperty(value = "练习册类型：1、提问册 2、练习册3、作业册", name = "exeBookType", example = "3")
    private int exeBookType;

    @ApiModelProperty(name = "answer", value = "回答内容", dataType = "string", required = true)
    private String answer;

    /**
     * 回答的学生
     */
    @ApiModelProperty(hidden = true)
    private String studentId;
}

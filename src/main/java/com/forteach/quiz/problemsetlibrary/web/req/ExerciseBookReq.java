package com.forteach.quiz.problemsetlibrary.web.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/18  10:24
 */
@Data
public class ExerciseBookReq {


    /**
     * 挂接的课堂练习题：1、预习练习册 2、课堂练习册3、课后作业册
     */
    @ApiModelProperty(value = "题集类型", name = "exeBookType", example = "1、预习练习册 2、课堂练习册3、课后作业册")
    private String exeBookType;

    /**
     * 章节id
     */
    @ApiModelProperty(value = "章节id", name = "chapterId", example = "章节id")
    private String chapterId;

    /**
     * 课程id
     */
    @ApiModelProperty(value = "课程id", name = "courseId", example = "章节id")
    private String courseId;

}

package com.forteach.quiz.problemsetlibrary.web.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/18  10:24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseBookReq implements Serializable {


    /**
     * 挂接的课堂练习题：1、提问册 2、练习册3、作业册
     */
    @ApiModelProperty(value = "题集类型", name = "exeBookType", example = "1、提问册 2、练习册3、作业册")
    private String exeBookType;

    /**
     * 章节id
     */
    @ApiModelProperty(value = "章节id", name = "chapterId", example = "章节id")
    private String chapterId;

    /**
     * 课程id
     */
    @ApiModelProperty(value = "课程id", name = "courseId", example = "课程id")
    private String courseId;

    /**
     * 课堂练习：before/预习 now/课堂 before,now/全部
     */
    @ApiModelProperty(value = "课堂练习  before/预习 now/课堂 before,now/全部", name = "preview", dataType = "string", example = "before")
    private String preview;

}

package com.forteach.quiz.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/18  17:19
 */
@Data
@ApiModel(value = "解除课程章节下的题目", description = "解除课程章节下的题目")
public class DelExerciseBookPartVo {

    /**
     * 挂接的课堂练习题：1、预习练习册 2、课堂练习册3、课后作业册
     */
    @ApiModelProperty(value = "题集类型", name = "exeBookType", example = "1、预习练习册 2、课堂练习册3、课后作业册")
    private String exeBookType;

    /**
     * 章节id
     */
    @ApiModelProperty(value = "章节id", name = "chapter", example = "章节id")
    private String chapter;

    /**
     * 课程id
     */
    @ApiModelProperty(value = "课程id", name = "courseId", example = "章节id")
    private String courseId;

    /**
     * target id
     */
    @ApiModelProperty(value = "被解除的题目id", name = "targetId", example = "被解除的题目id ... ")
    private String targetId;


}

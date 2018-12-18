package com.forteach.quiz.web.req;

import com.forteach.quiz.web.vo.SortVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/18  10:24
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExerciseBookReq extends SortVo {


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
     * 难易度id
     */
    @ApiModelProperty(value = "难易度id", name = "levelId", example = "0")
    private String levelId;


}

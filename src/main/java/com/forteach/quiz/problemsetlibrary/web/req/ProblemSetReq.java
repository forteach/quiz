package com.forteach.quiz.problemsetlibrary.web.req;

import com.forteach.quiz.web.vo.SortVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/14  14:33
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProblemSetReq extends SortVo implements Serializable {

    /**
     * 题集类型：1、预习练习册 2、课堂练习册3、课后作业册
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

    /**
     * 难易度id
     */
    @ApiModelProperty(value = "难易度id", name = "levelId", example = "0")
    private String levelId;

    /**
     * 显示全部详情或者是只返回id
     */
    @ApiModelProperty(value = "显示题集包含的题目全部详情或者是只返回id all 全部/part 只显示id", name = "questionType", example = "part")
    private String allOrPart;


}

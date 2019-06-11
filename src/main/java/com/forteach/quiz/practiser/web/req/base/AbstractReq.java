package com.forteach.quiz.practiser.web.req.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-4 10:50
 * @version: 1.0
 * @description:
 */
@Data
public abstract class AbstractReq {

    @ApiModelProperty(name = "courseId", value = "课程id", dataType = "string", required = true)
    private String courseId;

    @ApiModelProperty(name = "chapterId", value = "章节id", dataType = "string", required = true)
    private String chapterId;

    @ApiModelProperty(name = "chapterName", value = "章节名称", dataType = "string")
    private String chapterName;

    @ApiModelProperty(value = "练习册类型: 1、提问册 2、练习册3、作业册", name = "exeBookType", example = "3")
    private String exeBookType;

    @ApiModelProperty(name = "questionId", value = "问题id", dataType = "string")
    private String questionId;

    /**
     * 课堂练习：before/预习 now/课堂 before,now/全部
     */
    @ApiModelProperty(value = "习题类型  before/预习 now/课堂 after/课后练习", name = "preview", required = true, dataType = "string", example = "before")
    private String preview;

    @ApiModelProperty(name = "classId", value = "班级id", dataType = "string", required = true)
    private String classId;
}

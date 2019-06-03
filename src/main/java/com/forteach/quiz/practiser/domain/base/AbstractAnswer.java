package com.forteach.quiz.practiser.domain.base;

import com.forteach.quiz.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-3 16:04
 * @version: 1.0
 * @description:
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractAnswer extends BaseEntity {

    @ApiModelProperty(value = "练习册类型：1、提问册 2、练习册3、作业册", name = "exeBookType", example = "3")
    protected int exeBookType;

    @ApiModelProperty(value = "章节id", name = "chapterId", example = "463bcd8e5fed4a33883850c14f877271")
    protected String chapterId;

    @ApiModelProperty(name = "courseId", value = "课程id", dataType = "string")
    private String courseId;

}

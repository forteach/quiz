package com.forteach.quiz.practiser.domain.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.forteach.quiz.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-9 16:43
 * @version: 1.0
 * @description:
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class AbstractAnswer extends BaseEntity {
    @ApiModelProperty(value = "练习册类型: 1、提问册 2、练习册3、作业册", name = "exeBookType", example = "3")
    @Indexed
    private String exeBookType;

    @ApiModelProperty(name = "courseId", value = "课程id", dataType = "string")
    @Indexed
    private String courseId;

    @ApiModelProperty(value = "章节id", name = "chapterId", example = "463bcd8e5fed4a33883850c14f877271")
    @Indexed
    protected String chapterId;

    @ApiModelProperty(name = "classId", value = "班级id", dataType = "string")
    private String classId;
    /**
     * 课堂练习：before/预习 now/课堂 before,now/全部
     */
    @ApiModelProperty(value = "习题类型  before/预习 now/课堂 after/课后练习", name = "preview", dataType = "string", example = "before")
    @Indexed
    private String preview;

    /**
     * 学生id
     */
    @Indexed
    @JsonIgnore
    @ApiModelProperty(value = "学生id", name = "studentId", dataType = "string")
    private String studentId;
}

package com.forteach.quiz.evaluate.domain;

import com.forteach.quiz.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-5 19:08
 * @version: 1.0
 * @description:
 */
@Data
@Document(collection = "questionExerciseReward")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "作业奖励记录", description = "作业的奖励记录表")
public class QuestionExerciseReward extends BaseEntity {

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

    @Indexed
    @ApiModelProperty(value = "学生id", name = "studentId", dataType = "string")
    private String studentId;

    @ApiModelProperty(name = "num", value = "添加小红花个数")
    private String num;
}

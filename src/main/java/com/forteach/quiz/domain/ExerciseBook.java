package com.forteach.quiz.domain;

import com.forteach.quiz.web.vo.ExerciseBookVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Description: 挂接课堂的练习题 类型：1、预习练习册 2、课堂练习册3、课后作业册
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  16:35
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "exerciseBook")
public class ExerciseBook<T> extends BaseEntity {

    @ApiModelProperty(value = "练习册类型：1、预习练习册 2、课堂练习册3、课后作业册", name = "exeBookType", example = "3")
    protected int exeBookType;

    @ApiModelProperty(value = "教师id (创建人id)", name = "teacherId", example = "001")
    protected String teacherId;

    @ApiModelProperty(value = "挂接课堂的练习题 的子题目集", name = "questionChildren")
    protected List<T> questionChildren;

    /**
     * 章节id
     */
    @ApiModelProperty(value = "章节id", name = "sectionId", example = "章节id")
    private String sectionId;

    /**
     * 课程id
     */
    @ApiModelProperty(value = "课程id", name = "courseId", example = "章节id")
    private String courseId;


    public ExerciseBook() {
    }

    public ExerciseBook(final ExerciseBookVo exerciseBookVo, final List<T> list) {
        BeanUtils.copyProperties(exerciseBookVo, this);
        this.questionChildren = list;
    }

}

package com.forteach.quiz.problemsetlibrary.domain.base;

import com.forteach.quiz.domain.BaseEntity;
import com.forteach.quiz.problemsetlibrary.web.vo.ProblemSetVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @Description: 挂接课堂的练习题 类型：1、提问册 2、练习册3、作业册
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  16:35
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExerciseBook<T> extends BaseEntity {

    @ApiModelProperty(value = "教师id (创建人id)", name = "teacherId", example = "001")
    protected String teacherId;

    @ApiModelProperty(value = "挂接课堂的练习题 的子题目集", name = "questionChildren")
    protected List<T> questionChildren;

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


    public ExerciseBook() {
    }

    public ExerciseBook(final ProblemSetVo problemSetVo, final List<T> list) {
        BeanUtils.copyProperties(problemSetVo, this);
        this.questionChildren = list;
    }

}

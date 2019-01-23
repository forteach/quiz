package com.forteach.quiz.problemsetlibrary.domain;

import com.forteach.quiz.problemsetlibrary.domain.base.ExerciseBook;
import com.forteach.quiz.problemsetlibrary.web.vo.ProblemSetVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/13  21:23
 */
@EqualsAndHashCode(callSuper = true)
@Document(collection = "bigQuestionexerciseBook")
@Data
public class BigQuestionExerciseBook extends ExerciseBook {

    /**
     * 练习册类型：1、提问册 2、练习册3、作业册
     */
    @ApiModelProperty(value = "练习册类型：1、提问册 2、练习册3、作业册", name = "exeBookType", example = "3")
    protected int exeBookType;

    public BigQuestionExerciseBook() {
    }

    public BigQuestionExerciseBook(final ProblemSetVo problemSetVo, final List<?> list) {
        BeanUtils.copyProperties(problemSetVo, this);
        this.questionChildren = list;
    }
}

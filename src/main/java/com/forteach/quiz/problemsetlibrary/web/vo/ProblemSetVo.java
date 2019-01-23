package com.forteach.quiz.problemsetlibrary.web.vo;

import com.forteach.quiz.domain.QuestionIds;
import com.forteach.quiz.problemsetlibrary.domain.base.ExerciseBook;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  17:27
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProblemSetVo extends ExerciseBook {

    /**
     * 只有考题库存在 练习册类型：1、提问册 2、练习册3、作业册
     */
    @ApiModelProperty(value = "只有考题库存在 练习册类型：1、提问册 2、练习册3、作业册", name = "exeBookType", example = "3")
    protected int exeBookType;
    /**
     * 题目Id
     */
    @ApiModelProperty(value = "题目id", name = "questionIds", example = "")
    private List<QuestionIds> questionIds;

}


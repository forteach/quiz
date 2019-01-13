package com.forteach.quiz.problemsetlibrary.web.vo;

import com.forteach.quiz.domain.ExerciseBook;
import com.forteach.quiz.domain.QuestionIds;
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

    private List<QuestionIds> questionIds;

}


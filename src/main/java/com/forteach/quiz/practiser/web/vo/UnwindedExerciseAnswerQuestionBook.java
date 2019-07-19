package com.forteach.quiz.practiser.web.vo;

import com.forteach.quiz.domain.BaseEntity;
import com.forteach.quiz.problemsetlibrary.web.vo.UnwindedBigQuestionexerciseBook;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-7-19 17:06
 * @version: 1.0
 * @description:
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UnwindedExerciseAnswerQuestionBook extends BaseEntity {
    private UnwindedBigQuestionexerciseBook bigQuestionExerciseBook;
}

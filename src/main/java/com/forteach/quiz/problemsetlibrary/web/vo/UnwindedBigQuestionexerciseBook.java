package com.forteach.quiz.problemsetlibrary.web.vo;

import com.forteach.quiz.domain.BaseEntity;
import com.forteach.quiz.questionlibrary.domain.BigQuestion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Auther: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2019/7/19 00:40
 * @Version: 1.0
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UnwindedBigQuestionexerciseBook extends BaseEntity {

    private String teacherId;
    private BigQuestion questionChildren;
    private String chapterId;
    private String courseId;
    private int exeBookType;
}
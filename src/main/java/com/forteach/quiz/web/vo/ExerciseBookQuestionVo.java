package com.forteach.quiz.web.vo;

import com.forteach.quiz.questionlibrary.domain.BigQuestion;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/20  13:54
 */
@Data
public class ExerciseBookQuestionVo {

    /**
     * 习题册id
     */
    private String exerciseBookId;

    private List<BigQuestion> bigQuestions;

    /**
     * 是否修改应用到所有的题库
     * 1 : 应用到所有题库    0  :  只修改到本练习册的题目
     */
    private int relate;

}

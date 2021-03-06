package com.forteach.quiz.domain;

import lombok.Data;

/**
 * @Description: 题目集的子题
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/16  9:33
 */
@Data
public class QuestionIds {

    /**
     * 题目id
     */
    private String bigQuestionId;

    /**
     * 题目坐标
     */
    private int index;

}

package com.forteach.quiz.domain;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/19  11:18
 */
@Data
public class Answ {

    /**
     * 答案集
     */
    List<AnswChildren> childrenList;
    /**
     * 大题id
     */
    private String bigQuestionId;
    /**
     * 分数
     */
    private Double score;

}

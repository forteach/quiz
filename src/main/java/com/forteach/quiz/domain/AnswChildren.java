package com.forteach.quiz.domain;

import lombok.Data;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/21  9:10
 */
@Data
public class AnswChildren {

    /**
     * 大题下子项id
     */
    private String questionId;

    /**
     * 学生答案
     */
    private String answer;

    /**
     * 答题得分
     */
    private Double score;

    /**
     * 答案评价  主观题: {人工输入:优.良.中.差}    客观题: true  false  halfOf
     */
    private String evaluation;
}

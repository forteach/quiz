package com.forteach.quiz.web.vo;

/**
 * @Description: 题目返回规则
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/29  16:27
 */
public class BigQuestionView {

    /**
     * 部分显示  : 隐藏敏感信息  如隐藏问题答案
     */
    public interface Summary {
    }

    /**
     * 全部显示
     */
    public interface SummaryWithDetail extends Summary {
    }

}

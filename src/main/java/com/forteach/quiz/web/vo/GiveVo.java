package com.forteach.quiz.web.vo;

import lombok.Data;

import static com.forteach.quiz.common.KeyStorage.CLASSROOM_ASK_QUESTIONS_RACE;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/27  15:21
 */
@Data
public class GiveVo {

    /**
     * 课堂圈子id
     */
    private String circleId;

    /**
     * 问题id
     */
    private String questionId;

    /**
     * 互动方式
     * <p>
     * race   : 抢答
     * raise  : 举手
     * select : 选则
     * vote   : 投票
     */
    private String interactive;

    /**
     * 选取类别
     * <p>
     * people  :  个人
     * team    :  小组
     */
    private String category;

    /**
     * 选中人员  [逗号 分割]
     */
    private String selected;

    /**
     * 切换
     * 当老师重复提问问题时  但是两次的提问类型不一样  如第一次抢答 第二次提问
     * 需要传送切换标识
     * 0 : 原题目   非0 : 切题
     */
    private int cut;

    public String getRaceAnswerFlag() {
        return CLASSROOM_ASK_QUESTIONS_RACE.concat(circleId).concat(questionId);
    }
}

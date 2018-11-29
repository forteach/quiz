package com.forteach.quiz.web.vo;

import lombok.Data;

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
}

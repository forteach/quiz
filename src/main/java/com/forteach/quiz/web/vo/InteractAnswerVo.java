package com.forteach.quiz.web.vo;

import lombok.Data;

import static com.forteach.quiz.common.Dic.ASK_RAISE_HAND;
import static com.forteach.quiz.common.KeyStorage.CLASSROOM_ASK_QUESTIONS_ID;
import static com.forteach.quiz.common.KeyStorage.CLASSROOM_ASK_QUESTIONS_RACE;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/29  14:48
 */
@Data
public class InteractAnswerVo {

    /**
     * 学生id
     */
    private String examineeId;

    /**
     * 课堂圈子id
     */
    private String circleId;

    /**
     * 问题id
     */
    private String questionId;

    /**
     * 答案
     */
    private String answer;

    /**
     * 切换提问类型过期标识
     */
    private String cut;

    /**
     * 获取redis 提问 key
     *
     * @return
     */
    public String getAskKey() {
        return CLASSROOM_ASK_QUESTIONS_ID.concat(circleId);
    }

    public String getRaceAnswerFlag() {
        return CLASSROOM_ASK_QUESTIONS_RACE.concat(circleId).concat(questionId);
    }

    public String getRaiseKey() {
        return ASK_RAISE_HAND.concat(circleId);
    }
}

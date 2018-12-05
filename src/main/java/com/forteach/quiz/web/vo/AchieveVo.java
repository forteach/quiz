package com.forteach.quiz.web.vo;

import lombok.Builder;
import lombok.Data;

import static com.forteach.quiz.common.KeyStorage.CLASSROOM_ASK_QUESTIONS_DISTINCT;
import static com.forteach.quiz.common.KeyStorage.CLASSROOM_ASK_QUESTIONS_ID;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/27  14:39
 */
@Data
@Builder
public class AchieveVo {

    /**
     * 学生id
     */
    private String examineeId;

    /**
     * 课堂圈子id
     */
    private String circleId;

    /**
     * 随机数
     */
    private String random;

    public AchieveVo() {
    }

    public AchieveVo(String examineeId, String circleId, String random) {
        this.examineeId = examineeId;
        this.circleId = circleId;
        this.random = random;
    }

    /**
     * 获取redis 提问 key
     *
     * @return
     */
    public String getAskKey() {
        return CLASSROOM_ASK_QUESTIONS_ID.concat(circleId);
    }

    public String getDistinctKey(String questions) {
        return CLASSROOM_ASK_QUESTIONS_DISTINCT
                .concat(circleId)
                .concat(examineeId)
                .concat(questions)
                .concat(random);
    }
}

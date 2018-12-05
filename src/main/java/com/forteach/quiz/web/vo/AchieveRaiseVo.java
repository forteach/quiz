package com.forteach.quiz.web.vo;

import lombok.Builder;
import lombok.Data;

import static com.forteach.quiz.common.Dic.ASK_RAISE_HAND;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/4  15:46
 */
@Data
@Builder
public class AchieveRaiseVo {

    /**
     * 学生id
     */
    private String studentId;

    /**
     * 课堂圈子id
     */
    private String circleId;

    /**
     * 随机数
     */
    private String random;

    public String getRaiseKey() {
        return ASK_RAISE_HAND.concat(circleId);
    }

}

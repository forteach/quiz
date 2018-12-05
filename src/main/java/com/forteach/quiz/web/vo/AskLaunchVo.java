package com.forteach.quiz.web.vo;

import lombok.Data;

import static com.forteach.quiz.common.Dic.ASK_RAISE_HAND;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/4  14:21
 */
@Data
public class AskLaunchVo {

    /**
     * 课堂圈子id
     */
    private String circleId;


    public String getRaiseKey() {
        return ASK_RAISE_HAND.concat(circleId);
    }

}

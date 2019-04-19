package com.forteach.quiz.web.vo;

import lombok.Builder;
import lombok.Data;


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


}

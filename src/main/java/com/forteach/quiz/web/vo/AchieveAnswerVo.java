package com.forteach.quiz.web.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Description: 实时获取学生回答情况
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/7  15:13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchieveAnswerVo {

    /**
     * 课堂圈子id
     */
    private String circleId;

    /**
     * 教师id
     */
    private String teacher;

    /**
     * 随机数
     */
    private String random;

}

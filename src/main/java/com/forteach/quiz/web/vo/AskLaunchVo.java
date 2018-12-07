package com.forteach.quiz.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import static com.forteach.quiz.common.Dic.ASK_RAISE_HAND;
import static com.forteach.quiz.common.KeyStorage.RAISE_HAND_STUDENT_DISTINCT;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/4  14:21
 */
@Data
@ApiModel(value = "发起提问", description = "每次推送提问题后 老师发起提问 会清空上次的举手显示的学生")
public class AskLaunchVo {

    /**
     * 课堂圈子id
     */
    @ApiModelProperty(value = "课堂圈子id", name = "circleId")
    private String circleId;


    public String getRaiseKey() {
        return ASK_RAISE_HAND.concat(circleId);
    }

    public String getRaiseDistinctKey() {
        return RAISE_HAND_STUDENT_DISTINCT.concat(circleId);
    }

}

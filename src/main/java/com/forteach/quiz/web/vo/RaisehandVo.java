package com.forteach.quiz.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import static com.forteach.quiz.common.Dic.ASK_RAISE_HAND;
import static com.forteach.quiz.common.KeyStorage.CLASSROOM_ASK_QUESTIONS_ID;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/4  15:02
 */
@Data
@ApiModel(value = "学生举手", description = "通过课堂圈子id 及 学生id举手")
public class RaisehandVo {

    /**
     * 学生id
     */
    @ApiModelProperty(value = "学生id", name = "examineeId")
    private String examineeId;

    /**
     * 课堂圈子id
     */
    @ApiModelProperty(value = "课堂圈子id", name = "circleId")
    private String circleId;


    public String getRaiseKey() {
        return ASK_RAISE_HAND.concat(circleId);
    }

    /**
     * 获取redis 提问 key
     *
     * @return
     */
    public String getAskKey() {
        return CLASSROOM_ASK_QUESTIONS_ID.concat(circleId);
    }

}

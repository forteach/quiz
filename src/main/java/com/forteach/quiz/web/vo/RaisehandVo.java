package com.forteach.quiz.web.vo;

import com.forteach.quiz.interaction.execute.config.BigQueKey;
import com.forteach.quiz.questionlibrary.domain.QuestionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import static com.forteach.quiz.common.Dic.ASK_RAISE_HAND;


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
//    @ApiModelProperty(value = "学生id", name = "examineeId")
    private String examineeId;

    /**
     * 课堂圈子id
     */
    @ApiModelProperty(value = "课堂圈子id", name = "circleId")
    private String circleId;

    /**
     * 课堂圈子id
     */
    @ApiModelProperty(value = "课堂圈子id", name = "questionId")
    private String questionId;


    public String getRaiseKey() {
        return ASK_RAISE_HAND.concat(circleId);
    }

    /**
     * 获取redis 提问 key
     *
     * @return
     */
    public String getAskKey(QuestionType type) {
        return BigQueKey.CLASSROOM_ASK_QUESTIONS_ID.concat(circleId).concat(type.name());
    }

}

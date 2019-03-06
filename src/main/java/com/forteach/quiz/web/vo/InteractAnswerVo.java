package com.forteach.quiz.web.vo;

import com.forteach.quiz.interaction.execute.config.BigQueKey;
import com.forteach.quiz.questionlibrary.domain.QuestionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import static com.forteach.quiz.common.Dic.ASK_RAISE_HAND;
import static com.forteach.quiz.common.KeyStorage.*;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/29  14:48
 */
@Data
@ApiModel(value = "学生提交答案", description = "学生提交答案 需要传入接收的题目的cut值")
public class InteractAnswerVo {

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

    /**
     * 问题id
     */
    @ApiModelProperty(value = "问题id", name = "questionId")
    private String questionId;

    /**
     * 答案
     */
    @ApiModelProperty(value = "答案", name = "answer")
    private String answer;

    /**
     * 切换提问类型过期标识
     */
    @ApiModelProperty(value = "切换提问类型过期标识  接收的该题cut", name = "cut")
    private String cut;

    /**
     * 获取redis 提问 key
     *
     * @return
     */
    public String getAskKey(QuestionType type) {
        return BigQueKey.CLASSROOM_ASK_QUESTIONS_ID.concat(circleId).concat(type.name());
    }

    public String getRaceAnswerFlag() {
        return BigQueKey.CLASSROOM_ASK_QUESTIONS_RACE.concat(circleId).concat(questionId);
    }

    public String getRaiseKey() {
        return ASK_RAISE_HAND.concat(circleId);
    }

    public String getExamineeIsReplyKey(QuestionType type) {
        return BigQueKey.EXAMINEE_IS_REPLY_KEY.concat(circleId).concat(type.name());
    }

}

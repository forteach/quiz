package com.forteach.quiz.interaction.domain;

import com.forteach.quiz.interaction.domain.base.GiveVo;
import com.forteach.quiz.questionlibrary.domain.QuestionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.forteach.quiz.common.KeyStorage.CLASSROOM_ASK_QUESTIONS_ID;
import static com.forteach.quiz.common.KeyStorage.CLASSROOM_ASK_QUESTIONS_RACE;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/27  15:21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "考题 练习题库 发布课堂提问", description = "除了提问题,所有问题只有选中的学生才会收到")
public class BigQuestionGiveVo extends GiveVo {

    /**
     * 问题id
     */
    @ApiModelProperty(value = "问题id", name = "questionId")
    private String questionId;

    /**
     * 互动方式
     * <p>
     * race   : 抢答
     * raise  : 举手
     * select : 选则
     * vote   : 投票
     */
    @ApiModelProperty(value = "互动方式 race   : 抢答/raise  : 举手/select : 选择/vote   : 投票", name = "interactive", notes = "race   : 抢答/raise  : 举手/select : 选择/vote   : 投票")
    private String interactive;

    public String getRaceAnswerFlag() {
        return CLASSROOM_ASK_QUESTIONS_RACE.concat(circleId).concat(questionId);
    }


    /**
     * 根据课堂 获取题目id
     *
     * @return
     */
    public String getAskQuestionsId(QuestionType type) {
        return CLASSROOM_ASK_QUESTIONS_ID.concat(type.name()).concat(circleId);
    }

}

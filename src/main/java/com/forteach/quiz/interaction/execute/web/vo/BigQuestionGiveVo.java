package com.forteach.quiz.interaction.execute.web.vo;

import com.forteach.quiz.interaction.execute.config.BigQueKey;
import com.forteach.quiz.interaction.execute.domain.base.GiveVo;
import com.forteach.quiz.questionlibrary.domain.QuestionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


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

    /**
     * 返回那个课堂的问题KEY
     * @return
     */
    public String getRaceAnswerFlag() {
        return BigQueKey.CLASSROOM_ASK_QUESTIONS_RACE.concat(circleId).concat(questionId);
    }


    /**
     * 课堂的题目类型KEY
     *
     * @return
     */
    public String getAskQuestionsId(QuestionType type) {
        return BigQueKey.CLASSROOM_ASK_QUESTIONS_ID.concat(type.name()).concat(circleId);
    }

}

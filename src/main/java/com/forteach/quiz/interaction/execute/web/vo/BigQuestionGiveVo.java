package com.forteach.quiz.interaction.execute.web.vo;

import com.forteach.quiz.interaction.execute.domain.base.GiveVo;
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

    @ApiModelProperty(value = "教师id", name = "teacherId")
    private String teacherId;


    @ApiModelProperty(value = "题目互动类型(TiWen，FengBao，RenWu，WenJuan，LianXi)", name = "questionType")
    private String questionType;

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
     * select : 选人
     * vote   : 投票
     */
    @ApiModelProperty(value = "互动方式 race   : 抢答/raise  : 举手/select : 选择/vote   : 投票", name = "interactive", notes = "race   : 抢答/raise  : 举手/select : 选择/vote   : 投票")
    private String interactive;


    public BigQuestionGiveVo(String questionId, String interactive) {
        this.questionId = questionId;
        this.interactive = interactive;
    }

    public BigQuestionGiveVo() {
    }
}

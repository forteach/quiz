package com.forteach.quiz.web.vo;

import com.fasterxml.jackson.annotation.JsonView;
import com.forteach.quiz.domain.BigQuestion;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/30  10:59
 */
@Data
@Builder
@ApiModel(value = "学生获取的提问题目信息", description = "接收的cut值 提交答案需要")
public class AskQuestionVo {

    /**
     * 提交答案所需cut值
     */
    @JsonView(BigQuestionView.Summary.class)
    @ApiModelProperty(value = "提交答案所需cut值", name = "cut")
    private String cut;

    /**
     * 提交答案所需随机数
     */
    @JsonView(BigQuestionView.Summary.class)
    @ApiModelProperty(value = "题目信息", name = "bigQuestion")
    private BigQuestion bigQuestion;

    /**
     * 参与方式
     */
    @JsonView(BigQuestionView.Summary.class)
    @ApiModelProperty(value = "参与方式 race   : 抢答\n" +
            "     * raise  : 举手\n" +
            "     * select : 选则\n" +
            "     * vote   : 投票", name = "circleId", notes = "")
    private String participate;

    public AskQuestionVo(String cut, BigQuestion bigQuestion, String participate) {
        this.cut = cut;
        this.bigQuestion = bigQuestion;
        this.participate = participate;
    }
}

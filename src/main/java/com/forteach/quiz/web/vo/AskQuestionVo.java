package com.forteach.quiz.web.vo;

import com.fasterxml.jackson.annotation.JsonView;
import com.forteach.quiz.domain.BigQuestion;
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
public class AskQuestionVo {

    /**
     * 提交答案所需随机数
     */
    @JsonView(BigQuestionView.Summary.class)
    private String cut;

    /**
     * 提交答案所需随机数
     */
    @JsonView(BigQuestionView.Summary.class)
    private BigQuestion bigQuestion;

    /**
     * 参与方式
     */
    @JsonView(BigQuestionView.Summary.class)
    private String participate;

    public AskQuestionVo(String cut, BigQuestion bigQuestion, String participate) {
        this.cut = cut;
        this.bigQuestion = bigQuestion;
        this.participate = participate;
    }
}

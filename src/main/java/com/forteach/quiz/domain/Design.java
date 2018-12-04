package com.forteach.quiz.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.forteach.quiz.web.vo.BigQuestionView;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/12  17:06
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Design extends AbstractExam {

    @JsonView(BigQuestionView.Summary.class)
    private String designQuestion;
    @JsonView(BigQuestionView.SummaryWithDetail.class)
    private String designAnsw;
    @JsonView(BigQuestionView.SummaryWithDetail.class)
    private String designAnalysis;

    public Design() {
    }

    public Design(String designQuestion) {
        this.designQuestion = designQuestion;
    }

    public Design(String designQuestion, String designAnsw, String designAnalysis, Double score) {
        this.designQuestion = designQuestion;
        this.designAnsw = designAnsw;
        this.designAnalysis = designAnalysis;
        this.score = score;
    }

    public Design(String id, String designQuestion, String designAnsw, String designAnalysis, Double score) {
        this.id = id;
        this.designQuestion = designQuestion;
        this.designAnsw = designAnsw;
        this.designAnalysis = designAnalysis;
        this.score = score;
    }

}

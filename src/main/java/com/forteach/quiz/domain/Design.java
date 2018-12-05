package com.forteach.quiz.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.forteach.quiz.web.vo.BigQuestionView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "简答思考题", description = "BigQuestion的子项")
public class Design extends AbstractExam {

    @JsonView(BigQuestionView.Summary.class)
    @ApiModelProperty(value = "题目题干", name = "examChildren", required = true, example = "亚特兰蒂斯是否存在")
    private String designQuestion;

    @JsonView(BigQuestionView.SummaryWithDetail.class)
    @ApiModelProperty(value = "题目答案", name = "designAnsw", required = true, example = "存在")
    private String designAnsw;

    @JsonView(BigQuestionView.SummaryWithDetail.class)
    @ApiModelProperty(value = "题目解析", name = "designAnalysis", required = true, example = "历史存在 现与海底")
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

package com.forteach.quiz.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/30  9:27
 */
@Data
@Document(collection = "askAnswer")
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@ApiModel(value = "回答信息", description = "学生回答信息")
public class AskAnswer extends BaseEntity {

    /**
     * 学生id
     */
    @ApiModelProperty(value = "学生id", name = "examineeId")
    private String examineeId;

    /**
     * 回答参与方式
     */
    @ApiModelProperty(value = "参与方式 race   : 抢答/raise  : 举手/select : 选择/vote   : 投票", name = "interactive")
    private String interactive;

    /**
     * 答案
     */
    @ApiModelProperty(value = "答案", name = "answer")
    private String answer;
    /**
     * 问题id
     */
    @ApiModelProperty(value = "问题id", name = "questionId")
    private String questionId;

    /**
     * 答案对错
     */
    @ApiModelProperty(value = "答案回答对错 true  false  主观题则未空串", name = "right")
    private String right;

    /**
     * 答案分数
     */
    @ApiModelProperty(value = "答案分数 暂时不需要", name = "score")
    private String score;

    /**
     * 答案评价
     */
    @ApiModelProperty(value = "主观题 教师给出的答案评价", name = "evaluate")
    private String evaluate;

    /**
     * 课堂id
     */
    @ApiModelProperty(value = "课堂id", name = "circleId")
    private String circleId;


    public AskAnswer() {
    }

    public AskAnswer(String examineeId, String interactive, String answer, String questionId, Date uDate, String right, String circleId) {
        this.examineeId = examineeId;
        this.interactive = interactive;
        this.answer = answer;
        this.questionId = questionId;
        this.uDate = uDate;
        this.right = right;
        this.circleId = circleId;
    }

}


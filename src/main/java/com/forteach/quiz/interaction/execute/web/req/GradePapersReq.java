package com.forteach.quiz.interaction.execute.web.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-5-30 14:43
 * @version: 1.0
 * @description:
 */
@Data
public class GradePapersReq implements Serializable {

    @ApiModelProperty(value = "学生id", name = "examineeId", dataType = "string", required = true)
    private String examineeId;
    /**
     * 问题id
     */
    @ApiModelProperty(value = "问题id", name = "questionId", dataType = "string", required = true)
    private String questionId;

    /**
     * 回答参与方式
     */
    @ApiModelProperty(value = "参与方式 race   : 抢答/raise  : 举手/select : 选择/vote  没有参与方式：no  : 投票", name = "interactive", dataType = "string")
    private String interactive;

    /**
     * 答案对错
     */
    @ApiModelProperty(value = "答案回答对错 true  false  主观题则为空串", name = "right", dataType = "string")
    private String right;

    /**
     * 答案分数
     */
    @ApiModelProperty(value = "答案分数", name = "score", dataType = "string")
    private String score;

    /**
     * 答案评价
     */
    @ApiModelProperty(value = "主观题 教师给出的答案评价", name = "evaluate", dataType = "string")
    private String evaluate;

    /**
     * 课堂id
     */
    @ApiModelProperty(value = "课堂id", name = "circleId", dataType = "string")
    private String circleId;

    @ApiModelProperty(value = "提问：TiWen  任务：RenWu", name ="questionType", dataType = "string")
    private String questionType;

}

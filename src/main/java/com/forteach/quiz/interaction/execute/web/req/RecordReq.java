package com.forteach.quiz.interaction.execute.web.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Auther: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2019/1/27 17:22
 * @Version: 1.0
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordReq implements Serializable {

    @ApiModelProperty(value = "课堂id", name = "circleId", dataType = "string", required = true)
    private String circleId;

    @ApiModelProperty(value = "问题id", name = "questionId", dataType = "string")
    private String questionId;

    @ApiModelProperty(value = "学生id", name = "examineeId", dataType = "string")
    private String examineeId;

    @ApiModelProperty(name = "questionType", value = "TiWen (提问) / FengBao(头脑风暴题库) /  RenWu (任务)　/ WenJuan (问卷) / LianXi (练习册)", dataType = "string")
    private String questionType;

    /**
     * 回答参与方式
     */
    @ApiModelProperty(value = "参与方式 race   : 抢答/raise  : 举手/select : 选择/vote  没有参与方式：no  : 投票", name = "interactive", dataType = "string")
    private String interactive;

}

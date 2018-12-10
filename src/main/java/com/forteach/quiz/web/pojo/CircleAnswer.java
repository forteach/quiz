package com.forteach.quiz.web.pojo;

import com.forteach.quiz.domain.AskAnswer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/7  15:38
 */
@Data
@NoArgsConstructor
@ApiModel(value = "回答信息", description = "学生回答信息")
public class CircleAnswer extends Students {


    @ApiModelProperty(value = "回答状态   1: 已回答   2 : 未回答", name = "state")
    private String state;

    @ApiModelProperty(value = "学生回答信息  ", name = "askAnswer")
    private AskAnswer askAnswer;

    public CircleAnswer(String state, AskAnswer askAnswer) {
        this.state = state;
        this.askAnswer = askAnswer;
    }
}

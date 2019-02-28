package com.forteach.quiz.interaction.execute.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/19  11:18
 */
@Data
@ApiModel(value = "答案集对象", description = "答案集对象")
public class Answ {

    /**
     * 答案集
     */
    @ApiModelProperty(value = "大题下的答案", name = "childrenList")
    private List<AnswChildren> childrenList;
    /**
     * 大题id
     */
    @ApiModelProperty(value = "大题id", name = "answList")
    private String bigQuestionId;
    /**
     * 分数
     */
    @ApiModelProperty(value = "答案集", name = "answList")
    private Double score;

    public Answ() {
    }
}

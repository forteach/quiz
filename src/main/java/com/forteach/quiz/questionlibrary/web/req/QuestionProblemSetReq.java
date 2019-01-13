package com.forteach.quiz.questionlibrary.web.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/13  14:17
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "加上练习册的题库", description = "加上练习册的题库 会给予练习册的交集 差集")
public class QuestionProblemSetReq extends QuestionBankReq {

    /**
     * 练习册id
     */
    @ApiModelProperty(value = "练习册id", name = "problemSetId", example = "5c10b2b6dc623b4024d693af")
    private String problemSetId;

}

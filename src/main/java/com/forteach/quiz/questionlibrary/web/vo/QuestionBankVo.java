package com.forteach.quiz.questionlibrary.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  1:27
 */
@Data
@ApiModel(value = "题目分享")
public class QuestionBankVo {

    @ApiModelProperty(name = "id", value = "id", dataType = "string", required = true)
    private String id;

    @ApiModelProperty(name = "teacherId", value = "teacherId", dataType = "string", required = true)
    private String teacherId;

}

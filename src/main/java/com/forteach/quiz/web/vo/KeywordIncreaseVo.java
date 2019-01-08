package com.forteach.quiz.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Arrays;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/8  10:18
 */
@Data
@ApiModel(value = "问题关键字", description = "问题添加或移除关键字")
public class KeywordIncreaseVo {

    @ApiModelProperty(value = "关键字数组", name = "value")
    private String[] value;

    @ApiModelProperty(value = "问题id", name = "bigQuestionId")
    private String bigQuestionId;

    public String[] getValue() {
        return Arrays.stream(value).map(String::trim).toArray(String[]::new);
    }
}

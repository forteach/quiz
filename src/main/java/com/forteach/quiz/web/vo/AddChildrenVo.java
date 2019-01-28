package com.forteach.quiz.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 增加大体下子项
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/7  16:19
 */
@Data
@ApiModel(value = "增加大体下子项", description = "增加大体下子项 ")
public class AddChildrenVo {

    /**
     * 修改人id
     */
    @ApiModelProperty(value = "修改人id", name = "teacherId")
    private String teacherId;

    /**
     * 修改的对象字json符串
     */
    @ApiModelProperty(value = "修改的对象字json符串", name = "json")
    private String json;

    /**
     * 大题id
     */
    @ApiModelProperty(value = "大题id", name = "questionId")
    private String questionId;

}

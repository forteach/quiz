package com.forteach.quiz.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/7  14:44
 */
@Data
@ApiModel(value = "修改大体下某题目", description = "修改大体下 某题目 ")
public class UpdateChildrenVo {

    /**
     * 修改人id
     */
    @ApiModelProperty(value = "修改人id", name = "teacherId")
    private String teacherId;

    /**
     * 修改的子项id
     */
    @ApiModelProperty(value = "修改的子项id", name = "childrenId")
    private String childrenId;

    /**
     * 修改的对象json字符串
     */
    @ApiModelProperty(value = "修改的对象json字符串", name = "json")
    private String json;

}

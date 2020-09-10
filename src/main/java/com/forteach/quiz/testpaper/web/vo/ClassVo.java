package com.forteach.quiz.testpaper.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author ：zhang10092009@hotmail.com
 * @date ：Created in 2020/9/7 16:23
 * @description：班级信息
 * @modified By：
 * @version: V1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "班级信息")
public class ClassVo implements Serializable {
    @ApiModelProperty(name = "classId", value = "班级id", dataType = "string", required = true)
    private String classId;
    @ApiModelProperty(name = "className", value = "班级名称", dataType = "string", required = true)
    private String className;
}

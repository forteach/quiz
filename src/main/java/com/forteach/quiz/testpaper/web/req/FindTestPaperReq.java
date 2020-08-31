package com.forteach.quiz.testpaper.web.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/8/27 17:48
 * @Version: v1.0
 * @Modified：查询试卷信息
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "查询试卷信息")
public class FindTestPaperReq implements Serializable {
    @ApiModelProperty(name = "courseId", value = "课程Id", dataType = "string")
    private String courseId;
}
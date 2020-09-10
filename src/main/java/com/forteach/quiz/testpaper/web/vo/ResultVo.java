package com.forteach.quiz.testpaper.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/9/10 11:11
 * @Version: v1.0
 * @Modified：回答结果
 * @Description:
 */
@Data
@ApiModel(value = "回答的具体小题结果和评论")
public class ResultVo implements Serializable {
    /**
     * 题号
     */
    @ApiModelProperty(name = "questionId", value = "习题编号", dataType = "string", required = true)
    private String questionId;
    /**
     * 回答结果
     */
    @ApiModelProperty(name = "answer", value = "回答结果", dataType = "string")
    private String answer;
    /**
     * 评价
     */
    @ApiModelProperty(name = "assess", value = "教师评价、评论", dataType = "string")
    private String assess;
    /**
     * 结果对错
     */
    @ApiModelProperty(name = "result", value = "回答结果正确与否", dataType = "boolean")
    private Boolean result;
    /**
     * 评分
     */
    @ApiModelProperty(name = "score", value = "评分", dataType = "number")
    private Number score;
}

package com.forteach.quiz.practiser.domain;

import com.forteach.quiz.interaction.execute.web.vo.DataDatumVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/21  9:10
 */
@Data
@ApiModel(value = "互动活动答题卡 答案", description = "互动活动答题卡 答案")
public class AnswChildren implements Serializable {

    /**
     * 习题id
     */
    @ApiModelProperty(value = "习题id", name = "questionId")
    private String questionId;

    /**
     * 学生id
     */
    @ApiModelProperty(value = "学生id", name = "studentId", dataType = "string")
    private String studentId;

    /**
     * 学生答案
     */
    @ApiModelProperty(value = "回答内容", name = "answer")
    private String answer;

    /**
     * 答案附件
     */
    @ApiModelProperty(value = "附件列表", name = "fileList")
    private List<DataDatumVo> fileList;

    /**
     * 答案图片列表
     */
    private List<String> answerImageList;

    /**
     * 答题得分
     */
    @ApiModelProperty(name = "score", value = "得分", dataType = "string")
    private String score;

    /**
     * 答案评价  主观题: {人工输入:优.良.中.差}    客观题: true  false  halfOf
     */
    @ApiModelProperty(value = "主观题 教师给出的答案评价", name = "evaluation", example = "客观题: true  false  halfOf, 答案评价  主观题: {人工输入:优.良.中.差}    客观题: true  false  halfOf")
    private String evaluation;

    public AnswChildren() {
    }
}

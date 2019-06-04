package com.forteach.quiz.practiser.web.req;

import com.forteach.quiz.practiser.web.req.base.AbstractReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-4 11:07
 * @version: 1.0
 * @description:
 */
@Data
@ApiModel(value = "老师批改学生回答/给予奖励")
@EqualsAndHashCode(callSuper = true)
public class GradeAnswerReq extends AbstractReq implements Serializable {

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

    /**
     * 学生id
     */
    @ApiModelProperty(value = "学生id", name = "studentId", dataType = "string")
    private String studentId;

    /**
     * 批改的教师id
     */
    @ApiModelProperty(hidden = true)
    private String teacherId;

}

package com.forteach.quiz.practiser.domain;

import com.forteach.quiz.interaction.execute.web.vo.DataDatumVo;
import com.forteach.quiz.practiser.domain.base.AbstractAnswer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-5 10:32
 * @version: 1.0
 * @description: 学生回答详情记录表(包含老师评价)
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "askAnswerExercise")
@Builder
@AllArgsConstructor
@ApiModel(value = "学生作业回答详情信息", description = "学生作业回答详情信息")
public class AskAnswerExercise extends AbstractAnswer {

    /**
     * 习题id
     */
    @ApiModelProperty(value = "习题id", name = "questionId")
    @Indexed
    private String questionId;



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

    @ApiModelProperty(name = "right", value = "回答正确与错误", dataType = "string")
    private String right;

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

    @ApiModelProperty(name = "teacherId", value = "批改教师id", dataType = "string")
    private String teacherId;

}

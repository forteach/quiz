package com.forteach.quiz.practiser.domain;

import com.forteach.quiz.practiser.domain.base.AbstractAnswer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-9 16:52
 * @version: 1.0
 * @description:　学生回答记录表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "answerLists")
@Builder
@AllArgsConstructor
@ApiModel(value = "学生回答记录表", description = "学生作业回答信息记录表")
public class AnswerLists extends AbstractAnswer {

    @ApiModelProperty(name = "isAnswerCompleted", value = "是否回答完毕 Y/N")
    private String isAnswerCompleted;

    @ApiModelProperty(name = "questions", value = "已经回答过的问题信息", dataType = "list")
    private List<String> questions;

    @ApiModelProperty(name = "isCorrectCompleted", value = "是否批改完　Y/N", dataType = "list")
    private String isCorrectCompleted;

    @ApiModelProperty(name = "correctQuestionIds", value = "批改的集合列表", dataType = "list")
    private List<String> correctQuestionIds;
}

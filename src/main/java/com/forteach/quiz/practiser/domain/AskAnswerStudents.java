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
 * @date: 19-6-3 16:30
 * @version: 1.0
 * @description:
 */
@Data
@Document(collection = "askAnswerStudents")
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@ApiModel(value = "学生作业回答记录表", description = "学生作业回答记录表")
public class AskAnswerStudents extends AbstractAnswer {

    @ApiModelProperty(name = "studentId", value = "学生id", dataType = "string")
    private String studentId;

    @ApiModelProperty(name = "isAnswerCompleted", value = "是否回答完 Y/N", dataType = "string")
    private String isAnswerCompleted;

    @ApiModelProperty(name = "questions", value = "学生回答过的问题集合", dataType = "list")
    private List<String> questions;

}

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
 * @date: 19-6-3 16:39
 * @version: 1.0
 * @description:
 */
@Data
@Document(collection = "askAnswerStudents")
@Builder
@AllArgsConstructor
@ApiModel(value = "教师批改作业记录表", description = "教师批改作业记录表")
@EqualsAndHashCode(callSuper = true)
public class AskAnswerGrade extends AbstractAnswer {

    @ApiModelProperty(name = "teacherId", value = "教师id", dataType = "string")
    private String teacherId;

    @ApiModelProperty(name = "questionId", value = "习题id", dataType = "string")
    private List<AnswerGrade> questionId;

}

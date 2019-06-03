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
 * @date: 19-6-3 16:11
 * @version: 1.0
 * @description:
 */
@Data
@Document(collection = "askAnswerExerciseBook")
@Builder
@AllArgsConstructor
@ApiModel(value = "学生作业回答详情信息", description = "学生作业回答详情信息")
@EqualsAndHashCode(callSuper = true)
public class AskAnswerExerciseBook extends AbstractAnswer {

    @ApiModelProperty(name = "answerList", value = "回答问题列表或批改列表")
    private List<AnswChildren> answerList;

}

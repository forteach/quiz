package com.forteach.quiz.practiser.domain;

import com.forteach.quiz.practiser.domain.base.AbstractAnswer;
import com.forteach.quiz.practiser.web.vo.AnswerVo;
import com.forteach.quiz.problemsetlibrary.domain.BigQuestionExerciseBook;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-12 09:59
 * @version: 1.0
 * @description:
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "exerciseAnswerQuestionBook")
@ApiModel(value = "学生作业回答详情信息", description = "学生作业回答详情信息")
public class ExerciseAnswerQuestionBook extends AbstractAnswer {

    // 添加6个字段 answer fileList

    @ApiModelProperty(name = "bigQuestionExerciseBook", value = "习题快照和答题内容")
    private BigQuestionExerciseBook bigQuestionExerciseBook;

    @ApiModelProperty(name = "teacherId", value = "批改教师id", dataType = "string")
    private String teacherId;

    public ExerciseAnswerQuestionBook() {
    }


    public ExerciseAnswerQuestionBook(BigQuestionExerciseBook bigQuestionExerciseBook, final AnswerVo answerVo) {
        BeanUtils.copyProperties(answerVo, this);
        this.bigQuestionExerciseBook = bigQuestionExerciseBook;
    }

    public ExerciseAnswerQuestionBook(BigQuestionExerciseBook bigQuestionExerciseBook, String teacherId) {
        this.bigQuestionExerciseBook = bigQuestionExerciseBook;
        this.teacherId = teacherId;
    }
}

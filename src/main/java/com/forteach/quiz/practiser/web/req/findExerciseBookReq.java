package com.forteach.quiz.practiser.web.req;

import com.forteach.quiz.problemsetlibrary.web.req.ExerciseBookReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-7-19 14:04
 * @version: 1.0
 * @description:
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class findExerciseBookReq extends ExerciseBookReq {

    @ApiModelProperty(hidden = true)
    private String studentId;
}

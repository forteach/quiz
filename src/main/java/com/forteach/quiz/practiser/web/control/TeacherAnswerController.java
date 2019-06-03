package com.forteach.quiz.practiser.web.control;

import com.forteach.quiz.practiser.service.ExerciseBookAnswerService;
import com.forteach.quiz.service.TokenService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-3 16:54
 * @version: 1.0
 * @description:
 */
@Slf4j
@RestController
@Api(value = "学生提交答案")
@RequestMapping(path = "/teacherAnswer", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TeacherAnswerController {
    private final TokenService tokenService;
    private final ExerciseBookAnswerService exerciseBookAnswerService;

    @Autowired
    public TeacherAnswerController(ExerciseBookAnswerService exerciseBookAnswerService, TokenService tokenService) {
        this.exerciseBookAnswerService = exerciseBookAnswerService;
        this.tokenService = tokenService;
    }
}

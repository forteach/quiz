package com.forteach.quiz.practiser.web.control;

import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.practiser.service.ExerciseBookAnswerService;
import com.forteach.quiz.practiser.web.req.AnswerReq;
import com.forteach.quiz.service.TokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-3 11:11
 * @version: 1.0
 * @description:
 */
@Slf4j
@RestController
@Api(value = "学生提交答案")
@RequestMapping(path = "/studentAnswer", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class StudentAnswerController {

    private final TokenService tokenService;
    private final ExerciseBookAnswerService exerciseBookAnswerService;

    @Autowired
    public StudentAnswerController(ExerciseBookAnswerService exerciseBookAnswerService, TokenService tokenService) {
        this.exerciseBookAnswerService = exerciseBookAnswerService;
        this.tokenService = tokenService;
    }

    @ApiOperation(value = "学生回答作业记录")
    @PostMapping("/saveAnswer")
    public Mono<WebResult> saveAnswer(@RequestBody AnswerReq answerReq, ServerHttpRequest request){
        MyAssert.isNull(answerReq.getAnswer(), DefineCode.ERR0010, "答案不为空");
        MyAssert.isNull(answerReq.getChapterId(), DefineCode.ERR0010, "章节不为空");
        MyAssert.isNull(answerReq.getCourseId(), DefineCode.ERR0010, "课程不为空");
        MyAssert.isNull(answerReq.getExeBookType(), DefineCode.ERR0010, "练习册/习题册类型类型不为空");
        MyAssert.isNull(answerReq.getQuestionId(), DefineCode.ERR0010, "题目id不为空");
        answerReq.setStudentId(tokenService.getStudentId(request));
        return exerciseBookAnswerService
                .saveAnswer(answerReq)
                .map(WebResult::okResult);
    }

}

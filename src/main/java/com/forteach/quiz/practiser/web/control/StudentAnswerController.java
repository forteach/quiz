package com.forteach.quiz.practiser.web.control;

import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.practiser.service.ExerciseAnswerService;
import com.forteach.quiz.practiser.web.req.AnswerReq;
import com.forteach.quiz.practiser.web.req.verify.AnswerVerify;
import com.forteach.quiz.service.TokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
    private final AnswerVerify answerVerify;
    private final ExerciseAnswerService exerciseAnswerService;

    @Autowired
    public StudentAnswerController(ExerciseAnswerService exerciseAnswerService, TokenService tokenService, AnswerVerify answerVerify) {
        this.tokenService = tokenService;
        this.answerVerify = answerVerify;
        this.exerciseAnswerService = exerciseAnswerService;
    }

    @ApiOperation(value = "学生回答作业", notes = "学生端学生提交答案")
    @PostMapping("/saveAnswer")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseId", value = "课程id", dataType = "string", required = true, paramType = "form"),
            @ApiImplicitParam(name = "chapterId", value = "章节id", dataType = "string", required = true, paramType = "form"),
            @ApiImplicitParam(name = "exeBookType", value = "练习册类型: 1、提问册 2、练习册3、作业册", example = "3", required = true, paramType = "form"),
            @ApiImplicitParam(name = "questionId", value = "问题id", dataType = "string", required = true, paramType = "form"),
            @ApiImplicitParam(name = "preview", value = "习题类型  before/预习 now/课堂 after/课后练习", required = true, paramType = "form"),
            @ApiImplicitParam(name = "answer", value = "回答内容", dataType = "string", required = true, paramType = "form"),
            @ApiImplicitParam(name = "classId", value = "班级id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "fileList", value = "附件列表", paramType = "form"),
            @ApiImplicitParam(name = "answerImageList", value = "答案图片列表", paramType = "form"),
    })
    public Mono<WebResult> saveAnswer(@RequestBody AnswerReq answerReq, ServerHttpRequest request){
        answerVerify.verify(answerReq);
        MyAssert.isNull(answerReq.getQuestionId(), DefineCode.ERR0010, "题目id不为空");
        MyAssert.isNull(answerReq.getAnswer(), DefineCode.ERR0010, "答案不为空");
        answerReq.setStudentId(tokenService.getStudentId(request));
        return exerciseAnswerService
                .saveAnswer(answerReq)
                .map(WebResult::okResult);
    }


    @PostMapping(path = "/findAnswer")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseId", value = "课程id", dataType = "string", required = true, paramType = "form"),
            @ApiImplicitParam(name = "chapterId", value = "章节id", dataType = "string", required = true, paramType = "form"),
            @ApiImplicitParam(name = "exeBookType", value = "练习册类型: 1、提问册 2、练习册3、作业册", example = "3", required = true, paramType = "form"),
            @ApiImplicitParam(name = "preview", value = "习题类型  before/预习 now/课堂 after/课后练习", required = true, paramType = "form"),
            @ApiImplicitParam(name = "classId", value = "班级id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "questionId", value = "问题id", dataType = "string", paramType = "form"),
    })
    public Mono<WebResult> findAnswer(@RequestBody AnswerReq answerReq, ServerHttpRequest request){
        answerVerify.verify(answerReq);
        answerReq.setStudentId(tokenService.getStudentId(request));
        return exerciseAnswerService
                .findAnswerStudent(answerReq)
                .map(WebResult::okResult);
    }

}

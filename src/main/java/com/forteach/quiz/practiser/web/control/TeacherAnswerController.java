package com.forteach.quiz.practiser.web.control;

import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.practiser.service.ExerciseAnswerService;
import com.forteach.quiz.practiser.web.req.AddRewardReq;
import com.forteach.quiz.practiser.web.req.FindAnswerStudentReq;
import com.forteach.quiz.practiser.web.req.GradeAnswerReq;
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
    private final AnswerVerify answerVerify;
    private final ExerciseAnswerService exerciseAnswerService;

    @Autowired
    public TeacherAnswerController(ExerciseAnswerService exerciseAnswerService, AnswerVerify answerVerify, TokenService tokenService) {
        this.exerciseAnswerService = exerciseAnswerService;
        this.tokenService = tokenService;
        this.answerVerify = answerVerify;
    }

    @ApiOperation(value = "老师批改作业、习题、练习册", notes = "老师批改学生的主观题并给出相应的评价　习题、提问册、练习册、作业册")
    @PostMapping("/gradeAnswer")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseId", value = "课程id", dataType = "string", required = true, paramType = "form"),
            @ApiImplicitParam(name = "chapterId", value = "章节id", dataType = "string", required = true, paramType = "form"),
            @ApiImplicitParam(name = "exeBookType", value = "练习册类型: 1、提问册 2、练习册3、作业册", dataType = "string", example = "3", required = true, paramType = "form"),
            @ApiImplicitParam(name = "questionId", value = "问题id", dataType = "string", required = true, paramType = "form"),
            @ApiImplicitParam(name = "preview", value = "习题类型  before/预习 now/课堂 after/课后练习", required = true, paramType = "form"),
            @ApiImplicitParam(name = "studentId", value = "学生id", dataType = "string", required = true, paramType = "form"),
            @ApiImplicitParam(name = "evaluation", value = "主观题 教师给出的答案评价", required = true, example = "客观题: true  false  halfOf, 答案评价  主观题: {人工输入:优.良.中.差}    客观题: true  false  halfOf"),
            @ApiImplicitParam(name = "score", value = "得分", dataType = "string", paramType = "form")
    })
    public Mono<WebResult> gradeAnswer(@RequestBody GradeAnswerReq gradeAnswerReq, ServerHttpRequest request){
        answerVerify.verify(gradeAnswerReq);
        MyAssert.isNull(gradeAnswerReq.getStudentId(), DefineCode.ERR0010, "要批改的学生id不为空");
        MyAssert.isNull(gradeAnswerReq.getEvaluation(), DefineCode.ERR0010, "老师评价不为空");
        tokenService.getTeacherId(request).ifPresent(gradeAnswerReq::setTeacherId);
        return exerciseAnswerService.gradeAnswer(gradeAnswerReq).map(WebResult::okResult);
    }


    @ApiOperation(value = "查找学生的答题记录", notes = "查询学生回答情况")
    @PostMapping(path = "/findAnswer")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "exeBookType", value = "练习册类型: 1、提问册 2、练习册3、作业册", dataType = "String", example = "3", required = true, paramType = "query"),
            @ApiImplicitParam(name = "courseId", value = "课程id", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "chapterId", value = "章节id", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "questionId", value = "问题id", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "preview", value = "习题类型  before/预习 now/课堂 after/课后练习", required = true, paramType = "query"),
            @ApiImplicitParam(name = "classId", value = "班级id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "studentId", value = "学生id", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "isAnswerCompleted", value = "是否回答完作业/习题 Y/N", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isCorrectCompleted", value = "是否批改完作业/习题 Y/N", example = "只有回答完的才传值", dataType = "string", paramType = "query")
    })
    public Mono<WebResult> findAnswer(@RequestBody FindAnswerStudentReq findAnswerStudentReq){
        answerVerify.verify(findAnswerStudentReq);
        MyAssert.isNull(findAnswerStudentReq.getIsAnswerCompleted(), DefineCode.ERR0010, "是否回答完作业不为空");
        return exerciseAnswerService.findAnswer(findAnswerStudentReq).map(WebResult::okResult);
    }




    @ApiOperation(value = "老师给予学生奖励", notes = "教师给予学生小红花等奖励")
    @PostMapping("/addReward")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseId", value = "课程id", dataType = "string", required = true, paramType = "form"),
            @ApiImplicitParam(name = "chapterId", value = "章节id", dataType = "string", required = true, paramType = "form"),
            @ApiImplicitParam(name = "exeBookType", value = "练习册类型: 1、提问册 2、练习册3、作业册", dataType = "string", example = "3", required = true, paramType = "form"),
            @ApiImplicitParam(name = "preview", value = "习题类型  before/预习 now/课堂 after/课后练习", required = true, paramType = "form"),
            @ApiImplicitParam(name = "studentId", value = "学生id", dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "num", value = "奖励", dataType = "string", paramType = "form")
    })
    public Mono<WebResult> addReward(@RequestBody AddRewardReq addRewardReq, ServerHttpRequest serverHttpRequest){
        answerVerify.verify(addRewardReq);
        MyAssert.isNull(addRewardReq.getNum(), DefineCode.ERR0010, "奖励数量不为空");
        tokenService.getTeacherId(serverHttpRequest).ifPresent(addRewardReq::setTeacherId);
        return exerciseAnswerService.addReward(addRewardReq).map(WebResult::okResult);
    }

}

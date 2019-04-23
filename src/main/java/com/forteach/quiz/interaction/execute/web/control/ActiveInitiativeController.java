package com.forteach.quiz.interaction.execute.web.control;

import com.fasterxml.jackson.annotation.JsonView;
import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.interaction.execute.service.ActiveInitiativeService;
import com.forteach.quiz.service.TokenService;
import com.forteach.quiz.web.vo.AchieveAnswerVo;
import com.forteach.quiz.web.vo.AchieveRaiseVo;
import com.forteach.quiz.web.vo.AchieveVo;
import com.forteach.quiz.web.vo.BigQuestionView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/15  14:38
 */
@RestController
@Api(value = "互动交互 轮询方式", tags = {"课堂提问等互动交互"})
@RequestMapping(value = "/interactPolling", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ActiveInitiativeController {
//
//    private final ActiveInitiativeService interactService;
//
//    private final TokenService tokenService;
//
//    public ActiveInitiativeController(ActiveInitiativeService interactService, TokenService tokenService) {
//        this.interactService = interactService;
//        this.tokenService = tokenService;
//    }
//
//
//    /**
//     * 主动推送问题
//     *
////     * @param examineeId
//     * @param circleId
//     * @param random
//     * @return
//     */
//    @JsonView(BigQuestionView.Summary.class)
//    @ApiImplicitParams({
////            @ApiImplicitParam(name = "examineeId", value = "学生id", paramType = "get", dataType = "string", required = true),
//            @ApiImplicitParam(name = "circleId", value = "课堂id", paramType = "get", dataType = "string", required = true),
//            @ApiImplicitParam(name = "random", value = "随机数 每次访问需要", paramType = "get", dataType = "string", required = true)
//    })
//    @ApiOperation(value = "学生获取推送的题目", notes = "学生链接接口时 每有新题目 则接收")
//    @GetMapping(value = "/achieve/questions")
//    public Mono<WebResult> achieveQuestions(//@RequestParam String examineeId,
//                                            @RequestParam String circleId, @RequestParam String random, ServerHttpRequest serverHttpRequest) {
//        String studentId = tokenService.getStudentId(serverHttpRequest);
//        return interactService.achieveQuestion(AchieveVo.builder().circleId(circleId).examineeId(studentId).random(random).build()).map(WebResult::okResult);
//    }
//
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "circleId", value = "课堂id", paramType = "get", dataType = "string", required = true),
//            @ApiImplicitParam(name = "random", value = "随机数 每次访问需要", paramType = "get", dataType = "string", required = true),
//            @ApiImplicitParam(name = "teacher", value = "教师id", paramType = "get", dataType = "string", required = true)
//    })
//    @ApiOperation(value = "老师获取实时举手的学生", notes = "每次有新的学生举手 则接收到推送")
//    @GetMapping(value = "/achieve/raise")
//    public Mono<WebResult> achieveRaise(@RequestParam String circleId, @RequestParam String random, @RequestParam String teacher) {
//
//        return interactService.achieveRaise(AchieveRaiseVo.builder().circleId(circleId).random(random).teacher(teacher).build()).map(WebResult::okResult);
//    }
//
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "circleId", value = "课堂id", paramType = "get", dataType = "string", required = true),
//            @ApiImplicitParam(name = "random", value = "随机数 每次访问需要", paramType = "get", dataType = "string", required = true),
//            @ApiImplicitParam(name = "teacher", value = "教师id", paramType = "get", dataType = "string", required = true)
//    })
//    @ApiOperation(value = "老师获取实时学生的答题情况", notes = "每次有新的学生答案 则接收到推送")
//    @GetMapping(value = "/achieve/answer")
//    public Mono<WebResult> achieveQuestion(@RequestParam String circleId, @RequestParam String random, @RequestParam String teacher) {
//
//        return interactService.achieveAnswer(AchieveAnswerVo.builder().circleId(circleId).random(random).teacher(teacher).build()).map(WebResult::okResult);
//    }
//
////    @ApiOperation(value = "查询课堂学生提交的答案", notes = "课堂id(必传),查询课堂答题的学生信息，问题id，查询答题各个题目学生信息")
////    @PostMapping("/findQuestionsRecord")
////    @ApiImplicitParams({
////            @ApiImplicitParam(value = "课堂id", name = "circleId", dataType = "string", required = true, paramType = "query"),
////            @ApiImplicitParam(value = "问题id", name = "questionsId", dataType = "string", required = true, paramType = "query"),
////    })
////    public Mono<WebResult> findQuestionsRecord(@ApiParam(value = "查询课堂提交的记录", required = true)@RequestBody RecordReq recordReq){
////        //验证请求参数
////        MyAssert.blank(recordReq.getCircleId(), DefineCode.ERR0010 ,"课堂编号不能为空");
////        MyAssert.blank(recordReq.getQuestionsId(), DefineCode.ERR0010 ,"问题编号不能为空");
////        return interactRecordQuestionsService.findRecordQuestion(recordReq.getCircleId(), recordReq.getQuestionsId()).map(WebResult::okResult);
////    }

}

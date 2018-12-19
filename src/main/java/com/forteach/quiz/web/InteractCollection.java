package com.forteach.quiz.web;

import com.fasterxml.jackson.annotation.JsonView;
import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.service.InteractService;
import com.forteach.quiz.web.vo.*;
import io.swagger.annotations.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/19  14:46
 */
@RestController
@Api(value = "互动交互", tags = {"课堂提问等互动交互"})
@RequestMapping(value = "/interact", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class InteractCollection extends BaseController {

    private final InteractService interactService;

    public InteractCollection(InteractService interactService) {
        this.interactService = interactService;
    }

    /**
     * 主动推送问题
     *
     * @param examineeId
     * @param circleId
     * @param random
     * @return
     */
    @JsonView(BigQuestionView.Summary.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "examineeId", value = "学生id", paramType = "get", dataType = "string", required = true),
            @ApiImplicitParam(name = "circleId", value = "课堂id", paramType = "get", dataType = "string", required = true),
            @ApiImplicitParam(name = "random", value = "随机数 每次访问需要", paramType = "get", dataType = "string", required = true)
    })
    @ApiOperation(value = "学生获取推送的题目", notes = "学生链接接口时 每有新题目 则接收")
    @GetMapping(value = "/achieve/questions")
    public Mono<WebResult> achieveQuestions(@RequestParam String examineeId, @RequestParam String circleId, @RequestParam String random) {

        return interactService.achieveQuestion(AchieveVo.builder().circleId(circleId).examineeId(examineeId).random(random).build()).map(WebResult::okResult);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "circleId", value = "课堂id", paramType = "get", dataType = "string", required = true),
            @ApiImplicitParam(name = "random", value = "随机数 每次访问需要", paramType = "get", dataType = "string", required = true),
            @ApiImplicitParam(name = "teacher", value = "教师id", paramType = "get", dataType = "string", required = true)
    })
    @ApiOperation(value = "老师获取实时举手的学生", notes = "每次有新的学生举手 则接收到推送")
    @GetMapping(value = "/achieve/raise")
    public Mono<WebResult> achieveRaise(@RequestParam String circleId, @RequestParam String random, @RequestParam String teacher) {

        return interactService.achieveRaise(AchieveRaiseVo.builder().circleId(circleId).random(random).teacher(teacher).build()).map(WebResult::okResult);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "circleId", value = "课堂id", paramType = "get", dataType = "string", required = true),
            @ApiImplicitParam(name = "random", value = "随机数 每次访问需要", paramType = "get", dataType = "string", required = true),
            @ApiImplicitParam(name = "teacher", value = "教师id", paramType = "get", dataType = "string", required = true)
    })
    @ApiOperation(value = "老师获取实时学生的答题情况", notes = "每次有新的学生答案 则接收到推送")
    @GetMapping(value = "/achieve/answer")
    public Mono<WebResult> achieveQuestion(@RequestParam String circleId, @RequestParam String random, @RequestParam String teacher) {

        return interactService.achieveAnswer(AchieveAnswerVo.builder().circleId(circleId).random(random).teacher(teacher).build()).map(WebResult::okResult);
    }

    /**
     * 课堂提问
     * 重新发起举手
     *
     * @return
     */
    @ApiOperation(value = "重新发起举手", notes = "课堂提问 重新发起举手")
    @PostMapping("/launch/raise")
    public Mono<WebResult> launchRaise(@ApiParam(value = "课堂提问 重新发起举手", required = true) @RequestBody AskLaunchVo askLaunchVo) {
        return interactService.launchRaise(askLaunchVo).map(WebResult::okResult);
    }

    /**
     * 课堂提问
     * 学生举手
     *
     * @return
     */
    @PostMapping("/raise")
    @ApiOperation(value = "学生举手", notes = "课堂提问 学生举手")
    public Mono<WebResult> raiseHand(@ApiParam(value = "学生举手", required = true) @RequestBody RaisehandVo raisehandVo) {
        return interactService.raiseHand(raisehandVo).map(WebResult::okResult);
    }

    /**
     * 发布问题
     *
     * @param giveVo
     * @return
     */
    @ApiOperation(value = "发布问题", notes = "通过课堂id 及提问方式 进行发布问题")
    @PostMapping("/send/question")
    public Mono<WebResult> sendQuestion(@ApiParam(value = "发布问题", required = true) @RequestBody GiveVo giveVo) {
        return interactService.sendQuestion(giveVo).map(WebResult::okResult);
    }

    /**
     * 提交答案
     *
     * @param interactAnswerVo
     * @return
     */
    @PostMapping("/send/answer")
    @ApiOperation(value = "提交答案", notes = "学生提交答案 只有符合规则的学生能够正确提交")
    public Mono<WebResult> sendAnswer(@ApiParam(value = "提交答案", required = true) @RequestBody InteractAnswerVo interactAnswerVo) {
        return interactService.sendAnswer(interactAnswerVo).map(WebResult::okResult);
    }


}

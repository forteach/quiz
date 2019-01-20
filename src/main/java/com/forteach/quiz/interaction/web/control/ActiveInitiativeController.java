package com.forteach.quiz.interaction.web.control;

import com.fasterxml.jackson.annotation.JsonView;
import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.interaction.service.ActiveInitiativeService;
import com.forteach.quiz.web.vo.AchieveAnswerVo;
import com.forteach.quiz.web.vo.AchieveRaiseVo;
import com.forteach.quiz.web.vo.AchieveVo;
import com.forteach.quiz.web.vo.BigQuestionView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
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

    private final ActiveInitiativeService interactService;

    public ActiveInitiativeController(ActiveInitiativeService interactService) {
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

}
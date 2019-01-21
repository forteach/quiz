package com.forteach.quiz.interaction.web.control;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.interaction.service.BigQuestionInteractService;
import com.forteach.quiz.interaction.web.vo.BigQuestionGiveVo;
import com.forteach.quiz.interaction.web.vo.InteractiveSheetVo;
import com.forteach.quiz.interaction.web.vo.MoreGiveVo;
import com.forteach.quiz.web.vo.AskLaunchVo;
import com.forteach.quiz.web.vo.InteractAnswerVo;
import com.forteach.quiz.web.vo.RaisehandVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/19  14:46
 */
@RestController
@Api(value = "题库 考题 互动交互", tags = {"题库 考题等交互"})
@RequestMapping(value = "/interact", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class BigQuestionInteractController {

    private final BigQuestionInteractService interactService;

    public BigQuestionInteractController(BigQuestionInteractService interactService) {
        this.interactService = interactService;
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

    /**
     * 发布问题
     *
     * @param giveVo
     * @return
     */
    @ApiOperation(value = "发布问题", notes = "通过课堂id 及提问方式 进行发布问题")
    @PostMapping("/send/question")
    public Mono<WebResult> sendQuestion(@ApiParam(value = "发布问题", required = true) @RequestBody BigQuestionGiveVo giveVo) {
        return interactService.sendQuestion(giveVo).map(WebResult::okResult);
    }

    /**
     * 发布本节课 练习册
     *
     * @param giveVo
     * @return
     */
    @ApiOperation(value = "发布互动练习册", notes = "传入练习册内题目的id 发布互动练习册题目")
    @PostMapping("/send/book")
    public Mono<WebResult> sendInteractiveBook(@ApiParam(value = "发布问题", required = true) @RequestBody MoreGiveVo giveVo) {
        return interactService.sendInteractiveBook(giveVo).map(WebResult::okResult);
    }

    /**
     * 提交课堂练习答案
     *
     * @param sheetVo
     * @return
     */
    @PostMapping("/sendBook/answer")
    @ApiOperation(value = "提交课堂练习答案", notes = "提交课堂练习答案 只有符合规则的学生能够正确提交")
    public Mono<WebResult> sendAnswer(@ApiParam(value = "提交答案", required = true) @RequestBody InteractiveSheetVo sheetVo) {
        return interactService.sendExerciseBookAnswer(sheetVo).map(WebResult::okResult);
    }


}

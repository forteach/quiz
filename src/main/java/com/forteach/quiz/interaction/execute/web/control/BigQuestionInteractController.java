package com.forteach.quiz.interaction.execute.web.control;

import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.interaction.execute.service.BigQuestionInteractService;
import com.forteach.quiz.interaction.execute.service.RaiseHandService;
import com.forteach.quiz.interaction.execute.service.SendAnswerService;
import com.forteach.quiz.interaction.execute.service.SendQuestService;
import com.forteach.quiz.interaction.execute.web.vo.BigQuestionGiveVo;
import com.forteach.quiz.interaction.execute.web.vo.InteractiveSheetVo;
import com.forteach.quiz.interaction.execute.web.vo.MoreGiveVo;
import com.forteach.quiz.web.vo.InteractAnswerVo;
import com.forteach.quiz.web.vo.RaisehandVo;
import io.swagger.annotations.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @Description: 课堂问题发布
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/19  14:46
 */
@RestController
@Api(value = "题库 考题 互动交互", tags = {"题库 考题等交互"})
@RequestMapping(value = "/interact", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class BigQuestionInteractController {

    private final BigQuestionInteractService interactService;

    //课堂发布问题
    private final SendQuestService sendQuestService;

    //课堂问题回答
    private final SendAnswerService sendAnswerService;

    //课堂举手
    private final RaiseHandService raiseHandService;

    public BigQuestionInteractController(BigQuestionInteractService interactService,
                                         SendAnswerService sendAnswerService,
                                         RaiseHandService raiseHandService,
                                         SendQuestService sendQuestService
    ) {
        this.interactService = interactService;
        this.sendQuestService = sendQuestService;
        this.sendAnswerService= sendAnswerService;
        this.raiseHandService=raiseHandService;
    }

    /**
     * 课堂老师发布问题
     *
     * @param giveVo
     * @return
     */
    @ApiOperation(value = "发布问题", notes = "通过课堂id 及提问方式 进行发布问题")
    @PostMapping("/send/question")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "问题id", name = "questionId"),
            @ApiImplicitParam(value = "互动方式 race   : 抢答/raise  : 举手/select : 选择/vote   : 投票",
                    name = "interactive", allowableValues = "race   : 抢答/raise  : 举手/select : 选择/vote   : 投票",
                    required = true, dataType = "string", paramType = "from")
    })
    public Mono<WebResult> sendQuestion(@ApiParam(value = "发布问题", required = true) @RequestBody BigQuestionGiveVo giveVo) {
        MyAssert.blank(giveVo.getQuestionId(), DefineCode.ERR0010,"课堂问题发布不能为空");
        MyAssert.blank(giveVo.getInteractive(), DefineCode.ERR0010,"课堂问题交互方式不能为空");
        MyAssert.blank(giveVo.getTeacherId(), DefineCode.ERR0010,"课堂问题发布教师不能为空");
        //课堂发布题目
        return sendQuestService.sendQuestion(
                giveVo.getCircleId(),
                giveVo.getTeacherId(),
                giveVo.getQuestionId(),
                giveVo.getInteractive(),
                giveVo.getCategory(),
                giveVo.getSelected(),
                giveVo.getCut()
        ).map(WebResult::okResult);
    }


    /**
     * 课堂提问
     * 学生举手
     *
     * @return
     */
    @PostMapping("/raise")
    @ApiOperation(value = "学生举手", notes = "课堂提问 学生举手")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "学生id", name = "examineeId", dataType = "string", paramType = "from", required = true),
            @ApiImplicitParam(value = "课堂圈子id", name = "circleId", dataType = "string", paramType = "from", required = true),
            @ApiImplicitParam(value = "举手的题目id", name = "questionId", dataType = "string", paramType = "from", required = true)
    })
    public Mono<WebResult> raiseHand(@ApiParam(value = "学生举手", required = true) @RequestBody RaisehandVo raisehandVo) {
        MyAssert.blank(raisehandVo.getQuestionId(), DefineCode.ERR0010,"课堂问题ID不能为空");
        return raiseHandService.raiseHand(raisehandVo.getCircleId(),raisehandVo.getExamineeId(),raisehandVo.getQuestionId()).map(WebResult::okResult);
    }

    /**
     * 课堂提问
     * 删除上次的举手学生，重新发起举手
     *
     * @return
     */
    @ApiOperation(value = "重新发起举手", notes = "课堂提问 重新发起举手")
    @PostMapping("/launch/raise")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "学生id", name = "examineeId", dataType = "string", paramType = "from", required = true),
            @ApiImplicitParam(value = "课堂圈子id", name = "circleId", dataType = "string", paramType = "from", required = true),
            @ApiImplicitParam(value = "举手的题目id", name = "questionId", dataType = "string", paramType = "from", required = true)
    })
    public Mono<WebResult> launchRaise(@ApiParam(value = "课堂提问 重新发起举手", required = true) @RequestBody RaisehandVo raisehandVo) {
        return raiseHandService.launchRaise(raisehandVo.getCircleId(),raisehandVo.getExamineeId(),raisehandVo.getQuestionId()).map(WebResult::okResult);
    }

    /**
     * 提交答案
     *
     * @param interactAnswerVo
     * @return
     */
    @PostMapping("/send/answer")
    @ApiOperation(value = "提交答案", notes = "学生提交答案 只有符合规则的学生能够正确提交")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "学生id", name = "examineeId", dataType = "string", required = true, paramType = "from"),
            @ApiImplicitParam(value = "课堂圈子id", name = "circleId", dataType = "string", required = true, paramType = "from"),
            @ApiImplicitParam(value = "问题id", name = "questionId", dataType = "string", required = true, paramType = "from"),
            @ApiImplicitParam(value = "答案", name = "answer", dataType = "string", required = true, paramType = "from"),
            @ApiImplicitParam(value = "切换提问类型过期标识  接收的该题cut", name = "cut", dataType = "string", required = true, paramType = "from")
    })
    public Mono<WebResult> sendAnswer(@ApiParam(value = "提交答案", required = true) @RequestBody InteractAnswerVo interactAnswerVo) {
        return sendAnswerService.sendAnswer(interactAnswerVo.getCircleId(),
                interactAnswerVo.getExamineeId(),
                interactAnswerVo.getQuestionId(),
                interactAnswerVo.getAnswer(),
                interactAnswerVo.getCut()).map(WebResult::okResult);
    }


    /**
     * 发布本节课 练习册
     *
     * @param giveVo
     * @return
     */
    @ApiOperation(value = "发布互动练习册", notes = "传入练习册内题目的id 发布互动练习册题目")
    @PostMapping("/send/book")
    @ApiImplicitParam(value = "问题id,多个id逗号分隔", name = "questionIds", required = true, dataType = "string", paramType = "from")
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
    @ApiImplicitParams({
            @ApiImplicitParam(value = "学生id", name = "examineeId", dataType = "string", required = true, paramType = "from"),
            @ApiImplicitParam(value = "课堂圈子id", name = "circleId", dataType = "string", required = true, paramType = "from"),
            @ApiImplicitParam(value = "切换提问类型过期标识  接收的该题cut", name = "cut", required = true, paramType = "from"),
            @ApiImplicitParam(value = "答案列表", name = "answList", dataType = "json", required = true, paramType = "from")
    })
    @ApiOperation(value = "提交课堂练习答案", notes = "提交课堂练习答案 只有符合规则的学生能够正确提交")
    public Mono<WebResult> sendAnswer(@ApiParam(value = "提交答案", required = true) @RequestBody InteractiveSheetVo sheetVo) {
        return interactService.sendExerciseBookAnswer(sheetVo).map(WebResult::okResult);
    }
}

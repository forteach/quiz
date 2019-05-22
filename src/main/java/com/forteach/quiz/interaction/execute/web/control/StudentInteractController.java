package com.forteach.quiz.interaction.execute.web.control;

import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.interaction.execute.service.SingleQue.FabuQuestService;
import com.forteach.quiz.interaction.execute.service.MoreQue.SendAnswerQuestBookService;
import com.forteach.quiz.interaction.execute.service.MoreQue.SendQuestBookService;
import com.forteach.quiz.interaction.execute.service.SingleQue.RaiseHandService;
import com.forteach.quiz.interaction.execute.service.SingleQue.SendAnswerService;
import com.forteach.quiz.interaction.execute.service.SingleQue.SendQuestService;
import com.forteach.quiz.interaction.execute.service.record.InteractRecordExerciseBookService;
import com.forteach.quiz.interaction.execute.service.record.InteractRecordQuestionsService;
import com.forteach.quiz.questionlibrary.domain.QuestionType;
import com.forteach.quiz.service.TokenService;
import com.forteach.quiz.web.vo.InteractAnswerVo;
import com.forteach.quiz.web.vo.RaisehandVo;
import io.swagger.annotations.*;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


/**
 * @Description: 课堂问题发布
 * @author: zjw
 * @version: V1.0
 * @date: 2018/11/19  14:46
 */
@RestController
@Api(value = "题库 考题 互动交互", tags = {"题库 考题等交互"})
@RequestMapping(value = "/stuInteract", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class StudentInteractController {

    private final InteractRecordQuestionsService interactRecordQuestionsService;
    private final InteractRecordExerciseBookService interactRecordExerciseBookService;
    private final TokenService tokenService;

    /**
     * 课堂发布问题
     */
    private final SendQuestService sendQuestService;

    /**
     * 课堂问题回答
     */
    private final SendAnswerService sendAnswerService;

    /**
     * 课堂举手
     */
    private final RaiseHandService raiseHandService;

    /**
     * 当前课堂已发布的题目列表
     */
    private final FabuQuestService fabuQuestService;

    /**
     * 练习册提问
     */
    private  final SendQuestBookService sendQuestBookService;

    /**
     * 练习册回答
     */
    private final SendAnswerQuestBookService sendAnswerQuestBookService;

    public StudentInteractController(InteractRecordQuestionsService interactRecordQuestionsService,
                                     InteractRecordExerciseBookService interactRecordExerciseBookService,
                                     SendQuestService sendQuestService,
                                     SendAnswerService sendAnswerService,
                                     RaiseHandService raiseHandService,
                                     FabuQuestService fabuQuestService,
                                     SendQuestBookService sendQuestBookService,
                                     SendAnswerQuestBookService sendAnswerQuestBookService,
                                     TokenService tokenService) {
        this.interactRecordQuestionsService = interactRecordQuestionsService;
        this.interactRecordExerciseBookService = interactRecordExerciseBookService;
        this.tokenService = tokenService;
        this.sendQuestService = sendQuestService;
        this.sendAnswerService= sendAnswerService;
        this.raiseHandService=raiseHandService;
        this.fabuQuestService=fabuQuestService;
        this.sendAnswerQuestBookService=sendAnswerQuestBookService;
        this.sendQuestBookService=sendQuestBookService;
    }
//******************教师端*************************************
//    /**
//     * 课堂老师发布问题
//     *
//     * @param giveVo
//     * @return
//     */
//    @ApiOperation(value = "发布问题", notes = "通过课堂id 及提问方式 进行发布问题")
//    @PostMapping("/send/question")
//    @ApiImplicitParams({
//            @ApiImplicitParam(value = "问题id", name = "questionId"),
//            @ApiImplicitParam(value = "教师id", name = "teacherId"),
//            @ApiImplicitParam(value = "个人、小组", name = "category"),
//            @ApiImplicitParam(value = "题目互动类型(TiWen，FengBao，RenWu，WenJuan，LianXi)", name = "questionType"),
//            @ApiImplicitParam(value = "互动方式 race   : 抢答/raise  : 举手/select : 选择/vote   : 投票",
//                    name = "interactive", allowableValues = "race   : 抢答/raise  : 举手/select : 选择/vote   : 投票",
//                    required = true, dataType = "string", paramType = "from")
//    })
//    public Mono<WebResult> sendQuestion(@ApiParam(value = "发布问题", required = true) @RequestBody BigQuestionGiveVo giveVo, ServerHttpRequest serverHttpRequest) {
//        giveVo.setTeacherId(tokenService.getTeacherId(serverHttpRequest).get());
//        MyAssert.blank(giveVo.getCircleId(), DefineCode.ERR0010,"课堂编号不能为空");
//        MyAssert.blank(giveVo.getQuestionId(), DefineCode.ERR0010,"课堂问题发布不能为空");
//        MyAssert.blank(giveVo.getInteractive(), DefineCode.ERR0010,"课堂问题交互方式不能为空");
//        MyAssert.blank(giveVo.getTeacherId(), DefineCode.ERR0010,"课堂问题发布教师不能为空");
//        MyAssert.blank(giveVo.getQuestionType(), DefineCode.ERR0010,"课堂问题互动类型不能为空");
//        MyAssert.blank(giveVo.getCategory(), DefineCode.ERR0010,"课堂问题人员参与类型不能为空");
//        //课堂发布题目
//        return sendQuestService.sendQuestion(
//                giveVo.getCircleId(),
//                giveVo.getTeacherId(),
//                giveVo.getQuestionType(),
//                giveVo.getQuestionId(),
//                giveVo.getInteractive(),
//                giveVo.getCategory(),
//                giveVo.getSelected().concat(","),
//                giveVo.getCut()
//        ).map(WebResult::okResult);
//    }
//
//    /**
//     * 课堂老师发布问题
//     *
//     * @param giveVo
//     * @return
//     */
//    @ApiOperation(value = "举手发布问题", notes = "通过课堂id 及提问方式 进行发布问题")
//    @PostMapping("/send/raise/question")
//    @ApiImplicitParams({
//            @ApiImplicitParam(value = "问题id", name = "questionId"),
//            @ApiImplicitParam(value = "教师id", name = "teacherId"),
//            @ApiImplicitParam(value = "个人、小组", name = "category"),
//            @ApiImplicitParam(value = "题目互动类型(TiWen，FengBao，RenWu，WenJuan，LianXi)", name = "questionType"),
//            @ApiImplicitParam(value = "互动方式 race   : 抢答/raise  : 举手/select : 选择/vote   : 投票",
//                    name = "interactive", allowableValues = "race   : 抢答/raise  : 举手/select : 选择/vote   : 投票",
//                    required = true, dataType = "string", paramType = "from")
//    })
//    public Mono<WebResult> raiseSendQuestion(@ApiParam(value = "发布问题", required = true) @RequestBody BigQuestionGiveVo giveVo, ServerHttpRequest serverHttpRequest) {
//        giveVo.setTeacherId(tokenService.getTeacherId(serverHttpRequest).get());
//        MyAssert.blank(giveVo.getCircleId(), DefineCode.ERR0010,"课堂编号不能为空");
//        MyAssert.blank(giveVo.getQuestionId(), DefineCode.ERR0010,"课堂问题发布不能为空");
//        MyAssert.blank(giveVo.getInteractive(), DefineCode.ERR0010,"课堂问题交互方式不能为空");
//        MyAssert.blank(giveVo.getTeacherId(), DefineCode.ERR0010,"课堂问题发布教师不能为空");
//        MyAssert.blank(giveVo.getQuestionType(), DefineCode.ERR0010,"课堂问题互动类型不能为空");
//        MyAssert.blank(giveVo.getCategory(), DefineCode.ERR0010,"课堂问题选举类型不能为空");
//        //课堂发布题目
//        return sendQuestService.raiseSendQuestion(
//                giveVo.getCircleId(),
//                giveVo.getTeacherId(),
//                giveVo.getQuestionType(),
//                giveVo.getQuestionId(),
//                giveVo.getInteractive(),
//                giveVo.getCategory(),
//                giveVo.getCut()
//        ).map(WebResult::okResult);
//    }


//###########################学生端##############################################
    /**
     * 课堂提问
     * 学生举手
     *
     * @return
     */
    @PostMapping("/raise")
    @ApiOperation(value = "学生举手", notes = "课堂提问 学生举手")
    @ApiImplicitParams({
//            @ApiImplicitParam(value = "学生id", name = "examineeId", dataType = "string", paramType = "from", required = true),
            @ApiImplicitParam(value = "课堂圈子id", name = "circleId", dataType = "string", paramType = "from", required = true),
            @ApiImplicitParam(value = "题目提问类型", name = "questionType", dataType = "string", paramType = "from", required = true),
            @ApiImplicitParam(value = "举手的题目id", name = "questionId", dataType = "string", paramType = "from", required = true)
    })
    public Mono<WebResult> raiseHand(@ApiParam(value = "学生举手", required = true) @RequestBody RaisehandVo raisehandVo, ServerHttpRequest serverHttpRequest) {
        MyAssert.blank(raisehandVo.getCircleId(), DefineCode.ERR0010,"课堂编号不能为空");
        MyAssert.blank(raisehandVo.getQuestionId(), DefineCode.ERR0010,"课堂问题ID不能为空");
        MyAssert.blank(raisehandVo.getQuestionType(), DefineCode.ERR0010,"题目提问类型不能为空");

        raisehandVo.setExamineeId(tokenService.getStudentId(serverHttpRequest));
        return raiseHandService.raiseHand(raisehandVo.getCircleId(),raisehandVo.getExamineeId(),raisehandVo.getQuestionId(),raisehandVo.getQuestionType()).map(WebResult::okResult);
    }

    /**
     * 课堂提问  TODO 需要重新考虑功能实现，逻辑实现有错误
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
    public Mono<WebResult> launchRaise(@ApiParam(value = "课堂提问 重新发起举手", required = true) @RequestBody RaisehandVo raisehandVo, ServerHttpRequest serverHttpRequest) {
        return raiseHandService.launchRaise(raisehandVo.getCircleId(),raisehandVo.getExamineeId(),raisehandVo.getQuestionId(),raisehandVo.getQuestionType()).map(WebResult::okResult);
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
//            @ApiImplicitParam(value = "学生id", name = "examineeId", dataType = "string", required = true, paramType = "from"),
            @ApiImplicitParam(value = "课堂圈子id", name = "circleId", dataType = "string", required = true, paramType = "from"),
            @ApiImplicitParam(value = "问题id", name = "questionId", dataType = "string", required = true, paramType = "from"),
            @ApiImplicitParam(value = "答案", name = "answer", dataType = "string", required = true, paramType = "from"),
            @ApiImplicitParam(value = "切换提问类型过期标识  接收的该题cut", name = "cut", dataType = "string", required = true, paramType = "from")
    })
    public Mono<WebResult> sendAnswer(@ApiParam(value = "提交答案", required = true) @RequestBody InteractAnswerVo interactAnswerVo, ServerHttpRequest serverHttpRequest) {
//        interactAnswerVo.setExamineeId(tokenService.getStudentId(serverHttpRequest));
        MyAssert.blank(interactAnswerVo.getCircleId(), DefineCode.ERR0010,"课堂编号不能为空");
        MyAssert.blank(interactAnswerVo.getQuestionId(), DefineCode.ERR0010,"课堂问题ID不能为空");
        MyAssert.blank(interactAnswerVo.getAnswer(), DefineCode.ERR0010,"题目回答内容不能为空");
        return sendAnswerService.sendAnswer(interactAnswerVo.getCircleId(),
                interactAnswerVo.getExamineeId(),
                interactAnswerVo.getQuestionId(),
                interactAnswerVo.getAnswer(),
                interactAnswerVo.getQuestionType(),
                interactAnswerVo.getFileList()).map(r-> String.valueOf(r)).map(WebResult::okResult);
    }

    /**
     * 提交课堂练习答案
     *
     * @param interactAnswerVo
     * @return
     */
    @PostMapping("/sendBook/answer")
    @ApiImplicitParams({
//            @ApiImplicitParam(value = "学生id", name = "examineeId", dataType = "string", required = true, paramType = "from"),
            @ApiImplicitParam(value = "课堂圈子id", name = "circleId", dataType = "string", required = true, paramType = "from"),
            @ApiImplicitParam(value = "问题id", name = "questionId", dataType = "string", required = true, paramType = "from"),
            @ApiImplicitParam(value = "答案", name = "answer", dataType = "string", required = true, paramType = "from"),
            @ApiImplicitParam(value = "切换提问类型过期标识  接收的该题cut", name = "cut", dataType = "string", required = true, paramType = "from")
    })
    @ApiOperation(value = "提交课堂练习答案", notes = "提交课堂练习答案 只有符合规则的学生能够正确提交")
    public Mono<WebResult> sendBookAnswer(@ApiParam(value = "提交答案", required = true) @RequestBody InteractAnswerVo interactAnswerVo, ServerHttpRequest serverHttpRequest) {

        interactAnswerVo.setExamineeId(tokenService.getStudentId(serverHttpRequest));
        return sendAnswerQuestBookService.sendAnswer(QuestionType.LianXi.name(),
                interactAnswerVo.getCircleId(),
                interactAnswerVo.getExamineeId(),
                interactAnswerVo.getQuestionId(),
                interactAnswerVo.getAnswer(),
                interactAnswerVo.getFileList()
                ).map(WebResult::okResult);
    }
    
}

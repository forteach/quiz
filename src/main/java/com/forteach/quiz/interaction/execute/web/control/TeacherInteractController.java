package com.forteach.quiz.interaction.execute.web.control;

import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.interaction.execute.service.MoreQue.SendAnswerQuestBookService;
import com.forteach.quiz.interaction.execute.service.MoreQue.SendQuestBookService;
import com.forteach.quiz.interaction.execute.service.SingleQue.FabuQuestService;
import com.forteach.quiz.interaction.execute.service.SingleQue.RaiseHandService;
import com.forteach.quiz.interaction.execute.service.SingleQue.SendAnswerService;
import com.forteach.quiz.interaction.execute.service.SingleQue.SendQuestService;
import com.forteach.quiz.interaction.execute.service.record.InteractRecordExerciseBookService;
import com.forteach.quiz.interaction.execute.service.record.InteractRecordQuestionsService;
import com.forteach.quiz.interaction.execute.web.req.RecordReq;
import com.forteach.quiz.interaction.execute.web.resp.QuestFabuListResponse;
import com.forteach.quiz.interaction.execute.web.vo.BigQuestionGiveVo;
import com.forteach.quiz.interaction.execute.web.vo.MoreGiveVo;
import com.forteach.quiz.interaction.execute.web.vo.QuestFabuListVo;
import com.forteach.quiz.service.TokenService;
import io.swagger.annotations.*;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Description: 课堂问题发布
 * @author: zjw
 * @version: V1.0
 * @date: 2018/11/19  14:46
 */
@RestController
@Api(value = "题库 考题 互动交互", tags = {"题库 考题等交互"})
@RequestMapping(value = "/teachInteract", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TeacherInteractController {

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

    public TeacherInteractController(InteractRecordQuestionsService interactRecordQuestionsService,
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
            @ApiImplicitParam(value = "教师id", name = "teacherId"),
            @ApiImplicitParam(value = "个人、小组", name = "category"),
            @ApiImplicitParam(value = "题目互动类型(TiWen，FengBao，RenWu，WenJuan，LianXi)", name = "questionType"),
            @ApiImplicitParam(value = "互动方式 race   : 抢答/raise  : 举手/select : 选择/vote   : 投票",
                    name = "interactive", allowableValues = "race   : 抢答/raise  : 举手/select : 选择/vote   : 投票",
                    required = true, dataType = "string", paramType = "from")
    })
    public Mono<WebResult> sendQuestion(@ApiParam(value = "发布问题", required = true) @RequestBody BigQuestionGiveVo giveVo, ServerHttpRequest serverHttpRequest) {
        //giveVo.setTeacherId(tokenService.getTeacherId(serverHttpRequest).get());
        MyAssert.blank(giveVo.getCircleId(), DefineCode.ERR0010,"课堂编号不能为空");
        MyAssert.blank(giveVo.getQuestionId(), DefineCode.ERR0010,"课堂问题发布不能为空");
        MyAssert.blank(giveVo.getInteractive(), DefineCode.ERR0010,"课堂问题交互方式不能为空");
        MyAssert.blank(giveVo.getTeacherId(), DefineCode.ERR0010,"课堂问题发布教师不能为空");
        MyAssert.blank(giveVo.getQuestionType(), DefineCode.ERR0010,"课堂问题互动类型不能为空");
        MyAssert.blank(giveVo.getCategory(), DefineCode.ERR0010,"课堂问题人员参与类型不能为空");
        //课堂发布题目
        return sendQuestService.sendQuestion(
                giveVo.getCircleId(),
                giveVo.getTeacherId(),
                giveVo.getQuestionType(),
                giveVo.getQuestionId(),
                giveVo.getInteractive(),
                giveVo.getCategory(),
                giveVo.getSelected().concat(","),
                giveVo.getCut()
        ).map(WebResult::okResult);
    }

    /**
     * 课堂老师发布问题
     *
     * @param giveVo
     * @return
     */
    @ApiOperation(value = "举手发布问题", notes = "通过课堂id 及提问方式 进行发布问题")
    @PostMapping("/send/raise/question")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "问题id", name = "questionId"),
            @ApiImplicitParam(value = "教师id", name = "teacherId"),
            @ApiImplicitParam(value = "个人、小组", name = "category"),
            @ApiImplicitParam(value = "题目互动类型(TiWen，FengBao，RenWu，WenJuan，LianXi)", name = "questionType"),
            @ApiImplicitParam(value = "互动方式 race   : 抢答/raise  : 举手/select : 选择/vote   : 投票",
                    name = "interactive", allowableValues = "race   : 抢答/raise  : 举手/select : 选择/vote   : 投票",
                    required = true, dataType = "string", paramType = "from")
    })
    public Mono<WebResult> raiseSendQuestion(@ApiParam(value = "发布问题", required = true) @RequestBody BigQuestionGiveVo giveVo, ServerHttpRequest serverHttpRequest) {
        giveVo.setTeacherId(tokenService.getTeacherId(serverHttpRequest).get());
        MyAssert.blank(giveVo.getCircleId(), DefineCode.ERR0010,"课堂编号不能为空");
        MyAssert.blank(giveVo.getQuestionId(), DefineCode.ERR0010,"课堂问题发布不能为空");
        MyAssert.blank(giveVo.getInteractive(), DefineCode.ERR0010,"课堂问题交互方式不能为空");
        MyAssert.blank(giveVo.getTeacherId(), DefineCode.ERR0010,"课堂问题发布教师不能为空");
        MyAssert.blank(giveVo.getQuestionType(), DefineCode.ERR0010,"课堂问题互动类型不能为空");
        MyAssert.blank(giveVo.getCategory(), DefineCode.ERR0010,"课堂问题选举类型不能为空");
        //课堂发布题目
        return sendQuestService.raiseSendQuestion(
                giveVo.getCircleId(),
                giveVo.getTeacherId(),
                giveVo.getQuestionType(),
                giveVo.getQuestionId(),
                giveVo.getInteractive(),
                giveVo.getCategory(),
                giveVo.getCut()
        ).map(WebResult::okResult);
    }





    @PostMapping("/fabu/questList")
    @ApiOperation(value = "获得当前已发布题目的题目列表", notes = "已发布题目列表、当前题目、以显示题目答案的列表")
    @ApiImplicitParam(name = "circleId", value = "课堂id", required = true, dataType = "string", paramType = "query")
    public Mono<WebResult> questFabuList(@RequestBody QuestFabuListVo questFabuListVo, ServerHttpRequest serverHttpRequest) {
        MyAssert.blank(questFabuListVo.getCircleId(), DefineCode.ERR0010, "课堂id不为空");
        return fabuQuestService.getFaBuQuestNow(questFabuListVo.getCircleId())
                .flatMap(list->Mono.just(new QuestFabuListResponse(questFabuListVo.getCircleId(),(List<String>)list.get(0),list.get(1).toString())))
                .onErrorReturn(new QuestFabuListResponse())
                .map(WebResult::okResult);
    }


    @ApiOperation(value = "查询课堂学生提交的答案", notes = "课堂id(必传),查询课堂答题的学生信息，问题id，查询答题各个题目学生信息")
    @PostMapping("/findQuestionsRecord")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "课堂id", name = "circleId", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(value = "问题id", name = "questionsId", dataType = "string", required = true, paramType = "query")
    })
    public Mono<WebResult> findQuestionsRecord(@RequestBody RecordReq recordReq){
        //验证请求参数
        MyAssert.blank(recordReq.getCircleId(), DefineCode.ERR0010 ,"课堂编号不能为空");
        MyAssert.blank(recordReq.getQuestionsId(), DefineCode.ERR0010 ,"问题编号不能为空");
        return interactRecordQuestionsService.findRecordQuestion(recordReq.getCircleId(), recordReq.getQuestionsId()).map(WebResult::okResult);
    }

    @ApiOperation(value = "查询课堂习题册提交的答案", notes = "课堂id(必传),查询课堂答题的学生信息，问题id，查询答题各个题目学生信息")
    @PostMapping("/findExerciseBookRecord")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "课堂id", name = "circleId", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(value = "问题id", name = "questionsId", dataType = "string", required = true, paramType = "query")
    })
    public Mono<WebResult> findExerciseBook(@ApiParam(value = "查询课堂提交的记录", required = true) @RequestBody RecordReq recordReq){
        //验证请求参数
        MyAssert.blank(recordReq.getCircleId(), DefineCode.ERR0010 ,"课堂编号不能为空");
        MyAssert.blank(recordReq.getQuestionsId(), DefineCode.ERR0010 ,"问题编号不能为空");
        return interactRecordExerciseBookService.findRecordExerciseBook(recordReq.getCircleId(), recordReq.getQuestionsId()).map(WebResult::okResult);
    }

//多题目类型**********************************************************************************************
    /**
     * 发布本节课 练习册
     *
     * @param giveVo
     * @return
     */
    @ApiOperation(value = "发布互动练习册", notes = "传入练习册内题目的id 发布互动练习册题目")
    @PostMapping("/send/book")
    @ApiImplicitParam(value = "问题id,多个id逗号分隔", name = "questionIds", required = true, dataType = "string", paramType = "from")
    public Mono<WebResult> sendInteractiveBook(@ApiParam(value = "发布问题", required = true) @RequestBody MoreGiveVo giveVo, ServerHttpRequest serverHttpRequest) {
        MyAssert.blank(giveVo.getCircleId(), DefineCode.ERR0010 ,"课堂编号不能为空");
        MyAssert.blank(giveVo.getQuestionId(), DefineCode.ERR0010 ,"题目信息不能为空");
        MyAssert.blank(giveVo.getTeacherId(), DefineCode.ERR0010,"课堂问题发布教师不能为空");
        MyAssert.blank(giveVo.getCategory(), DefineCode.ERR0010,"课堂问题选举类型不能为空");
        MyAssert.blank(giveVo.getSelected(), DefineCode.ERR0010,"练习册人员不能为空");
        MyAssert.blank(giveVo.getQuestionType(), DefineCode.ERR0010,"课堂问题互动类型不能为空");
        giveVo.setTeacherId(tokenService.getTeacherId(serverHttpRequest).get());
        return  sendQuestBookService.sendQuestionBook(giveVo.getCircleId(),giveVo.getTeacherId(),giveVo.getQuestionType() ,giveVo.getQuestionId(),giveVo.getCategory(),giveVo.getSelected().concat(",")) .map(WebResult::okResult);
    }
    
}

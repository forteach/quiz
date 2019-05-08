package com.forteach.quiz.interaction.execute.web.control;

import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.interaction.execute.service.RecordService;
import com.forteach.quiz.interaction.execute.service.SingleQue.FabuQuestService;
import com.forteach.quiz.interaction.execute.service.MoreQue.SendAnswerQuestBookService;
import com.forteach.quiz.interaction.execute.service.MoreQue.SendQuestBookService;
import com.forteach.quiz.interaction.execute.service.SingleQue.RaiseHandService;
import com.forteach.quiz.interaction.execute.service.SingleQue.SendAnswerService;
import com.forteach.quiz.interaction.execute.service.SingleQue.SendQuestService;
import com.forteach.quiz.interaction.execute.service.record.InteractRecordExerciseBookService;
import com.forteach.quiz.interaction.execute.service.record.InteractRecordQuestionsService;
import com.forteach.quiz.interaction.execute.web.resp.QuestFabuListResponse;
import com.forteach.quiz.interaction.execute.web.req.RecordReq;
import com.forteach.quiz.interaction.execute.web.vo.*;
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
import reactor.core.publisher.Flux;
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
@RequestMapping(value = "/interact", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class BigQuestionInteractController {

//    private final InteractRecordQuestionsService interactRecordQuestionsService;
//    private final InteractRecordExerciseBookService interactRecordExerciseBookService;

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

    /**
     * 查询记录
     */
    private final RecordService recordService;

    public BigQuestionInteractController(
//            InteractRecordQuestionsService interactRecordQuestionsService,
//                                         InteractRecordExerciseBookService interactRecordExerciseBookService,
                                         SendQuestService sendQuestService,
                                         SendAnswerService sendAnswerService,
                                         RaiseHandService raiseHandService,
                                         FabuQuestService fabuQuestService,
                                         SendQuestBookService sendQuestBookService,
                                         SendAnswerQuestBookService sendAnswerQuestBookService,
                                         RecordService recordService,
                                         TokenService tokenService) {
//        this.interactRecordQuestionsService = interactRecordQuestionsService;
//        this.interactRecordExerciseBookService = interactRecordExerciseBookService;
        this.tokenService = tokenService;
        this.sendQuestService = sendQuestService;
        this.sendAnswerService= sendAnswerService;
        this.raiseHandService=raiseHandService;
        this.fabuQuestService=fabuQuestService;
        this.sendAnswerQuestBookService=sendAnswerQuestBookService;
        this.sendQuestBookService=sendQuestBookService;
        this.recordService = recordService;
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
        giveVo.setTeacherId(tokenService.getTeacherId(serverHttpRequest).get());
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
        raisehandVo.setExamineeId(tokenService.getStudentId(serverHttpRequest));
        MyAssert.blank(raisehandVo.getCircleId(), DefineCode.ERR0010,"课堂编号不能为空");
        MyAssert.blank(raisehandVo.getQuestionId(), DefineCode.ERR0010,"课堂问题ID不能为空");
        MyAssert.blank(raisehandVo.getQuestionType(), DefineCode.ERR0010,"题目提问类型不能为空");
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
        interactAnswerVo.setExamineeId(tokenService.getStudentId(serverHttpRequest));
        MyAssert.blank(interactAnswerVo.getCircleId(), DefineCode.ERR0010,"课堂编号不能为空");
        MyAssert.blank(interactAnswerVo.getQuestionId(), DefineCode.ERR0010,"课堂问题ID不能为空");
        MyAssert.blank(interactAnswerVo.getAnswer(), DefineCode.ERR0010,"题目回答内容不能为空");
        return sendAnswerService.sendAnswer(interactAnswerVo.getCircleId(),
                interactAnswerVo.getExamineeId(),
                interactAnswerVo.getQuestionId(),
                interactAnswerVo.getAnswer(),
                interactAnswerVo.getQuestionType()).map(r-> String.valueOf(r)).map(WebResult::okResult);
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

//    @PostMapping("/fabu/delSelectStu")
//    @ApiOperation(value = "删除问题选人推送的学生列表", notes = "学生收到题目推送后调用")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "circleId", value = "课堂id", required = true, dataType = "string", paramType = "from"),
//            @ApiImplicitParam(name = "stuId", value = "学生id", required = true, dataType = "string", paramType = "from")
//    })
//    public Mono<WebResult> questFabuList(@RequestBody DelSelectStuVo delSelectStuVo) {
//        MyAssert.blank(delSelectStuVo.getCircleId(), DefineCode.ERR0010, "课堂id不为空");
//        MyAssert.blank(delSelectStuVo.getStuId(), DefineCode.ERR0010, "学生id不为空");
//        return fabuQuestService.delSelectStuId(delSelectStuVo.getStuId(),delSelectStuVo.getCircleId())
//                .map(WebResult::okResult);
//    }


//    @ApiOperation(value = "查询课堂学生提交的答案", notes = "课堂id(必传),查询课堂答题的学生信息，问题id，查询答题各个题目学生信息")
//    @PostMapping("/findQuestionsRecord")
//    @ApiImplicitParams({
//            @ApiImplicitParam(value = "课堂id", name = "circleId", dataType = "string", required = true, paramType = "query"),
//            @ApiImplicitParam(value = "问题id", name = "questionsId", dataType = "string", required = true, paramType = "query")
//    })
//    public Mono<WebResult> findQuestionsRecord(@RequestBody RecordReq recordReq){
//        //验证请求参数
//        MyAssert.blank(recordReq.getCircleId(), DefineCode.ERR0010 ,"课堂编号不能为空");
//        MyAssert.blank(recordReq.getQuestionsId(), DefineCode.ERR0010 ,"问题编号不能为空");
//        return interactRecordQuestionsService.findRecordQuestion(recordReq.getCircleId(), recordReq.getQuestionsId()).map(WebResult::okResult);
//    }

//    @ApiOperation(value = "查询课堂习题册提交的答案", notes = "课堂id(必传),查询课堂答题的学生信息，问题id，查询答题各个题目学生信息")
//    @PostMapping("/findExerciseBookRecord")
//    @ApiImplicitParams({
//            @ApiImplicitParam(value = "课堂id", name = "circleId", dataType = "string", required = true, paramType = "query"),
//            @ApiImplicitParam(value = "问题id", name = "questionsId", dataType = "string", required = true, paramType = "query")
//    })
//    public Mono<WebResult> findExerciseBook(@ApiParam(value = "查询课堂提交的记录", required = true) @RequestBody RecordReq recordReq){
//        //验证请求参数
//        MyAssert.blank(recordReq.getCircleId(), DefineCode.ERR0010 ,"课堂编号不能为空");
//        MyAssert.blank(recordReq.getQuestionsId(), DefineCode.ERR0010 ,"问题编号不能为空");
//        return interactRecordExerciseBookService.findRecordExerciseBook(recordReq.getCircleId(), recordReq.getQuestionsId()).map(WebResult::okResult);
//    }

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
        giveVo.setTeacherId(tokenService.getTeacherId(serverHttpRequest).get());
        return  sendQuestBookService.sendQuestionBook(giveVo.getCircleId(),giveVo.getTeacherId(), QuestionType.LianXi.name(),giveVo.getQuestionId(),giveVo.getCategory(),giveVo.getSelected().concat(",")) .map(WebResult::okResult);
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
                interactAnswerVo.getAnswer()
                ).map(WebResult::okResult);
    }


    @ApiOperation(value = "查询回答情况", notes = "查询学生回答信息")
    @PostMapping("/findQuestionRecord")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "课堂/课程 id", name = "circleId", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(value = "问题id", name = "questionsId", dataType = "string", required = true, paramType = "query")
    })
    public Mono<WebResult> findQuestionRecord(@RequestBody RecordReq recordReq){
        MyAssert.isNull(recordReq.getCircleId(), DefineCode.ERR0010, "课堂信息id不为空");
        MyAssert.isNull(recordReq.getQuestionId(), DefineCode.ERR0010, "问题不为空");
        return recordService.findQuestionRecord(recordReq.getCircleId(),
                recordReq.getQuestionId())
                .map(WebResult::okResult);
    }

    @ApiOperation(value = "查询回答大题", notes = "多条件查询学生回答大题情况")
    @PostMapping("/findAskAnswer")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "课堂/课程 id", name = "circleId", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(value = "问题id", name = "questionsId", dataType = "string", paramType = "query"),
            @ApiImplicitParam(value = "答题的学生id", name = "examineeId", dataType = "string", paramType = "query")
    })
    public Mono<WebResult> findAskQuestionRecord(@RequestBody RecordReq recordReq){
        MyAssert.isNull(recordReq.getCircleId(), DefineCode.ERR0010, "课堂信息id不为空");
        return recordService.findAskRecord(recordReq.getCircleId(),
                recordReq.getQuestionId(),
                recordReq.getExamineeId())
                .map(WebResult::okResult);
    }

}

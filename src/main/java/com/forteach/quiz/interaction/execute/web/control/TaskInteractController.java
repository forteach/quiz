package com.forteach.quiz.interaction.execute.web.control;

import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.interaction.execute.service.TaskInteractService;
import com.forteach.quiz.interaction.execute.service.record.InteractRecordTaskService;
import com.forteach.quiz.interaction.execute.web.req.RecordReq;
import com.forteach.quiz.interaction.execute.web.vo.InteractiveSheetAnsw;
import com.forteach.quiz.interaction.execute.web.vo.InteractiveSheetVo;
import com.forteach.quiz.interaction.execute.web.vo.MoreGiveVo;
import com.forteach.quiz.service.TokenService;
import io.swagger.annotations.*;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/16  22:24
 */
@RestController
@Api(value = "任务互动", tags = {"任务互动等互动"})
@RequestMapping(value = "/TaskInteract", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TaskInteractController {

    private final TaskInteractService interactService;

    private final TokenService tokenService;

    private final InteractRecordTaskService interactRecordTaskService;

    public TaskInteractController(TaskInteractService interactService, TokenService tokenService,
                                  InteractRecordTaskService interactRecordTaskService) {
        this.interactService = interactService;
        this.tokenService = tokenService;
        this.interactRecordTaskService = interactRecordTaskService;
    }


    /**
     * 发布任务
     *
     * @param giveVo 多个互动题目 发布课堂提问
     * @return
     */
    @ApiOperation(value = "发布任务", notes = "通过课堂id 进行发布任务")
    @PostMapping("/send")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "问题id,多个id逗号分隔", name = "questionId", dataType = "string", required = true, paramType = "from")
    })
    public Mono<WebResult> sendQuestion(@ApiParam(value = "发布任务", required = true) @RequestBody MoreGiveVo giveVo) {
        return interactService.sendQuestion(giveVo).map(WebResult::okResult);
    }

    /**
     * 提交答案
     *
     * @param sheetVo
     * @return
     */
    @PostMapping("/send/answer")
    @ApiOperation(value = "提交答案", notes = "学生提交答案 只有符合规则的学生能够正确提交")
    @ApiImplicitParams({
//            @ApiImplicitParam(value = "学生id", name = "examineeId", required = true, dataType = "string", paramType = "from"),
            @ApiImplicitParam(value = "课堂圈子id", name = "circleId", required = true, dataType = "string", paramType = "from"),
            @ApiImplicitParam(value = "切换提问类型过期标识  接收的该题cut", name = "cut", dataType = "string", paramType = "from"),
            @ApiImplicitParam(value = "答案列表", name = "answList", dataTypeClass = InteractiveSheetAnsw.class, paramType = "from")
    })
    public Mono<WebResult> sendAnswer(@ApiParam(value = "提交答案", required = true) @RequestBody InteractiveSheetVo sheetVo, ServerHttpRequest serverHttpRequest) {
        sheetVo.setExamineeId(tokenService.getStudentId(serverHttpRequest));
        return interactService.sendAnswer(sheetVo).map(WebResult::okResult);
    }

    @ApiOperation(value = "查询问卷记录", notes = "课堂id(必传),查询课堂答题的学生信息，问题id，查询答题各个题目学生信息")
    @PostMapping("/findTaskRecord")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "课堂id", name = "circleId", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(value = "问题id", name = "questionsId", dataType = "string", required = true, paramType = "query"),
    })
    public Mono<WebResult> findTask(@RequestBody RecordReq recordReq){
        //验证请求参数
        MyAssert.blank(recordReq.getCircleId(), DefineCode.ERR0010 ,"课堂编号不能为空");
        MyAssert.blank(recordReq.getQuestionsId(), DefineCode.ERR0010 ,"问题编号不能为空");
        return interactRecordTaskService.findTaskRecord(recordReq.getCircleId(), recordReq.getQuestionsId()).map(WebResult::okResult);
    }
}

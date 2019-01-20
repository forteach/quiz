package com.forteach.quiz.interaction.web.control;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.interaction.service.TaskInteractService;
import com.forteach.quiz.interaction.web.vo.InteractiveSheetVo;
import com.forteach.quiz.interaction.web.vo.MoreGiveVo;
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
 * @date: 2019/1/16  22:24
 */
@RestController
@Api(value = "任务互动", tags = {"任务互动等互动"})
@RequestMapping(value = "/TaskInteract", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TaskInteractController {


    private final TaskInteractService interactService;

    public TaskInteractController(TaskInteractService interactService) {
        this.interactService = interactService;
    }


    /**
     * 发布任务
     *
     * @param giveVo
     * @return
     */
    @ApiOperation(value = "发布任务", notes = "通过课堂id 进行发布任务")
    @PostMapping("/send")
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
    public Mono<WebResult> sendAnswer(@ApiParam(value = "提交答案", required = true) @RequestBody InteractiveSheetVo sheetVo) {
        return interactService.sendAnswer(sheetVo).map(WebResult::okResult);
    }


}

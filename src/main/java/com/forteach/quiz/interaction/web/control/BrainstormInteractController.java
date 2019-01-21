package com.forteach.quiz.interaction.web.control;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.interaction.service.BrainstormInteractService;
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
 * @date: 2019/1/16  22:25
 */
@RestController
@Api(value = "头脑风暴交互", tags = {"头脑风暴交互等互动"})
@RequestMapping(value = "/brainstormInteract", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class BrainstormInteractController {

    private final BrainstormInteractService interactService;

    public BrainstormInteractController(BrainstormInteractService interactService) {
        this.interactService = interactService;
    }


    /**
     * 发布问题
     *
     * @param giveVo
     * @return
     */
    @ApiOperation(value = "发布问卷", notes = "通过课堂id 及提问方式 进行发布问题")
    @PostMapping("/send")
    public Mono<WebResult> sendQuestion(@ApiParam(value = "发布问题", required = true) @RequestBody MoreGiveVo giveVo) {
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

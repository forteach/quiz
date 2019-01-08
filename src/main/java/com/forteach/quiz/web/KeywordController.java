package com.forteach.quiz.web;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.service.KeywordService;
import com.forteach.quiz.web.vo.KeywordIncreaseVo;
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
 * @date: 2019/1/8  10:12
 */
@RestController
@Api(value = "关键字", tags = {"题库 关键字"})
@RequestMapping(value = "/keyword", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class KeywordController extends BaseController {

    private final KeywordService keywordService;

    public KeywordController(KeywordService keywordService) {
        this.keywordService = keywordService;
    }

    /**
     * 增加关键字关联关系
     *
     * @return
     */
    @ApiOperation(value = "增加关键字关联关系", notes = "增加关键字关联关系")
    @PostMapping("/increase")
    public Mono<WebResult> increase(@ApiParam(value = "增加关键字关联关系", required = true) @RequestBody KeywordIncreaseVo increaseVo) {
        return keywordService.increase(increaseVo.getValue(), increaseVo.getBigQuestionId()).map(WebResult::okResult);
    }

    /**
     * 删除关键字
     *
     * @return
     */
    @ApiOperation(value = "删除关键字", notes = "删除关键字")
    @PostMapping("/undock")
    public Mono<WebResult> undock(@ApiParam(value = "删除关键字", required = true) @RequestBody KeywordIncreaseVo increaseVo) {
        return keywordService.undock(increaseVo.getValue(), increaseVo.getBigQuestionId()).map(WebResult::okResult);
    }

    /**
     * 查询知识点下问题id
     *
     * @return
     */
    @ApiOperation(value = "查询知识点下问题id", notes = "查询知识点下问题id")
    @PostMapping("/associated")
    public Mono<WebResult> associated(@ApiParam(value = "查询知识点下问题id", required = true) @RequestBody KeywordIncreaseVo increaseVo) {
        return keywordService.keywordQuestion(increaseVo.getValue()).collectList().map(WebResult::okResult);
    }


}

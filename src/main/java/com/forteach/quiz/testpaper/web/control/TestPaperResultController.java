package com.forteach.quiz.testpaper.web.control;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.service.TokenService;
import com.forteach.quiz.testpaper.service.TestPaperResultService;
import com.forteach.quiz.testpaper.web.req.AddResultReq;
import com.forteach.quiz.testpaper.web.req.TestPaperPageReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/9/11 14:33
 * @Version: v1.0
 * @Modified：学生回答结果
 * @Description:
 */
@Slf4j
@RestController
@Api(value = "试卷回答记录", tags = {"试卷回答信息"}, description = "试卷回答信息")
@RequestMapping(path = "/testPaperResult", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TestPaperResultController {

    private final TokenService tokenService;

    private final TestPaperResultService testPaperResultService;

    public TestPaperResultController(TokenService tokenService,
                                     TestPaperResultService testPaperResultService) {
        this.tokenService = tokenService;
        this.testPaperResultService = testPaperResultService;
    }

    @PostMapping(path = "/addResult")
    @ApiOperation(value = "学生回答")
    public Mono<WebResult> addResult(@RequestBody @Validated AddResultReq resultReq, @ApiIgnore ServerHttpRequest request) {
        return testPaperResultService.addResult(resultReq).map(WebResult::okResult);
    }

    @PostMapping(path = "/findAllPage")
    @ApiOperation(value = "查询成绩")
    public Mono<WebResult> findAllPage(@RequestBody @Validated TestPaperPageReq req) {
        return testPaperResultService.findAllPage(req).map(WebResult::okResult);
    }
}

package com.forteach.quiz.testpaper.web.control;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.service.TokenService;
import com.forteach.quiz.testpaper.domain.TestPaper;
import com.forteach.quiz.testpaper.service.TestPaperService;
import com.forteach.quiz.testpaper.web.req.FindTestPaperReq;
import com.forteach.quiz.testpaper.web.req.UpdateTestPaperReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/8/27 16:58
 * @Version: v1.0
 * @Modified：试卷信息
 * @Description:
 */
@Slf4j
@RestController
@Api(value = "试卷记录", tags = {"试卷信息"}, description = "试卷信息")
@RequestMapping(path = "/testPaper", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TestPaperController {
    private final TokenService tokenService;
    private final TestPaperService testPaperService;

    public TestPaperController(TokenService tokenService, TestPaperService testPaperService) {
        this.tokenService = tokenService;
        this.testPaperService = testPaperService;
    }

    @PostMapping("/update")
    @ApiOperation(value = "保存更新试卷信息")
    public Mono<WebResult> update(@RequestBody @Validated UpdateTestPaperReq req, @ApiIgnore ServerHttpRequest request){
        TestPaper testPaper = new TestPaper();
        BeanUtil.copyProperties(req, testPaper);
        if (StrUtil.isBlank(req.getId())){
            testPaper.setId(IdUtil.objectId());
        }
        return testPaperService.updateSave(testPaper).map(WebResult::okResult);
    }

    @ApiOperation(value = "查询试卷信息")
    @PostMapping(path = "/findAll")
    public Mono<WebResult> findAll(@RequestBody @Validated FindTestPaperReq req, @ApiIgnore ServerHttpRequest request){
        String teacherId = tokenService.getTeacherId(request).get();
        return testPaperService.findAll(teacherId, req.getCourseId()).collectList().map(WebResult::okResult);
    }
}

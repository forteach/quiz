package com.forteach.quiz.testpaper.web.control;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.questionlibrary.domain.BigQuestion;
import com.forteach.quiz.service.TokenService;
import com.forteach.quiz.testpaper.service.TestPaperResultService;
import com.forteach.quiz.testpaper.web.req.AddResultReq;
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
public class TestPaperResultController<T extends BigQuestion> {

    private final TokenService tokenService;

//    private final ExamInfoService examInfoService;

    private final TestPaperResultService testPaperResultService;

    public TestPaperResultController(TokenService tokenService,// ExamInfoService examInfoService,
                                     TestPaperResultService testPaperResultService) {
        this.tokenService = tokenService;
//        this.examInfoService = examInfoService;
        this.testPaperResultService = testPaperResultService;
    }

    @PostMapping(path = "/addResult")
    @ApiOperation(value = "学生回答")
    public Mono<WebResult> addResult(@RequestBody @Validated AddResultReq resultReq, @ApiIgnore ServerHttpRequest request){
//        String classId = tokenService.getClassId(request);
//        String classId = resultReq.getClassId();
//        Mono<Boolean> decide = examInfoService.decide(classId, resultReq.getTestPaperId());
//        MyAssert.isFalse(decide., DefineCode.ERR0013, "您没有对应的权限");
//        resultReq.setClassId(classId);

        return testPaperResultService.addResult(resultReq).map(WebResult::okResult);
    }
}

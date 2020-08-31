package com.forteach.quiz.testpaper.web.control;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.service.TokenService;
import com.forteach.quiz.testpaper.domain.ExamInfo;
import com.forteach.quiz.testpaper.service.ExamInfoService;
import com.forteach.quiz.testpaper.web.req.FindExamInfoReq;
import com.forteach.quiz.testpaper.web.req.UpdateExamInfoReq;
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
 * @Date: 2020/8/28 17:00
 * @Version: v1.0
 * @Modified：考试安排信息
 * @Description:
 */
@Slf4j
@RestController
@Api(value = "考试安排信息", tags = {"考试安排信息"}, description = "考试安排信息")
@RequestMapping(path = "/examInfo", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ExamInfoController {
    private final TokenService tokenService;
    private final ExamInfoService examInfoService;

    public ExamInfoController(TokenService tokenService, ExamInfoService examInfoService) {
        this.tokenService = tokenService;
        this.examInfoService = examInfoService;
    }

    @PostMapping("/update")
    @ApiOperation(value = "保存更新试卷信息")
    public Mono<WebResult> update(@RequestBody @Validated UpdateExamInfoReq req, @ApiIgnore ServerHttpRequest request){
        ExamInfo examInfo = new ExamInfo();
        if (StrUtil.isBlank(req.getId())){
            examInfo.setId(IdUtil.objectId());
        }
        BeanUtil.copyProperties(req, examInfo, "startDateTime", "endDateTime");
        examInfo.setStartDateTime(req.getStartDateTime());
        examInfo.setEndDateTime(req.getEndDateTime());
        return examInfoService.saveUpdate(examInfo).map(WebResult::okResult);
    }

    @ApiOperation(value = "查询考试信息")
    @PostMapping(path = "/findAll")
    public Mono<WebResult> findAll(@RequestBody @Validated FindExamInfoReq req, @ApiIgnore ServerHttpRequest request){
        String teacherId = tokenService.getTeacherId(request).get();
        req.setTeacherId(teacherId);
        return examInfoService.findAll(req).collectList().map(WebResult::okResult);
    }
}
package com.forteach.quiz.evaluate.web.control;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.evaluate.Service.RewardService;
import com.forteach.quiz.evaluate.web.control.req.CumulativeReq;
import io.swagger.annotations.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * @Description:  老师奖励小红花
 * @author: zjw
 * @version: V1.0
 * @date: 2019/5/20  10:24
 */
@RestController
@Api(value = "老师奖励学生红花", tags = {"对学生的任务、提问奖励等"})
@RequestMapping(value = "/reward", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class RewardController {

    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

//    @ApiOperation(value = "获取所有的学生评价", notes = "获取所有的评价")
//    @GetMapping(value = "/findAll")
//    public Mono<WebResult> findAll() {
//
//        return service.findAll().collectList().map(WebResult::okResult);
//    }

    /**
     * 课堂奖励(红花等... ...) 累加
     *
     * @return
     */
    @PostMapping("/class/add")
    @ApiOperation(value = "奖励(红花等... ...)", notes = "对学生进行奖励等")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "被累加的学生id", name = "studentId", required = true, dataType = "string", paramType = "from"),
            @ApiImplicitParam(value = "增加值", name = "num", dataType = "string", required = true, paramType = "from"),
            @ApiImplicitParam(value = "奖励类型：红花：flower", name = "rewardType", dataType = "string", required = true, paramType = "from")
    })
    public Mono<WebResult> rewardAdd(@ApiParam(value = "对学生奖励累加", required = true) @Valid @RequestBody CumulativeReq req) {
        return rewardService.cumulative(req.getCircleId(),req.getStudentId(), Integer.valueOf(req.getNum())).map(WebResult::okResult);
    }


}

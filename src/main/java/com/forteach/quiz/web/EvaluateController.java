package com.forteach.quiz.web;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.service.RewardService;
import com.forteach.quiz.web.vo.CumulativeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/11  14:45
 */
@RestController
@Api(value = "评价相关", tags = {"对学生的评价/提问奖励等"})
@RequestMapping(path = "/evaluate")
public class EvaluateController {

    private final RewardService rewardService;

    public EvaluateController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    /**
     * 奖励(红花等... ...) 累加
     *
     * @return
     */
    @PostMapping("/reward/cumulative")
    @ApiOperation(value = "奖励(红花等... ...)", notes = "对学生进行奖励等")
    public Mono<WebResult> reward(@ApiParam(value = "对学生奖励累加", required = true) @Valid @RequestBody CumulativeVo cumulativeVo) {
        return rewardService.cumulative(cumulativeVo.getStudentId(), Double.valueOf(cumulativeVo.getAmount())).map(WebResult::okResult);
    }


}

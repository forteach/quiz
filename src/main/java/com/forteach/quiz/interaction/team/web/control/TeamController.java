package com.forteach.quiz.interaction.team.web.control;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.interaction.team.service.TeamService;
import com.forteach.quiz.interaction.team.web.vo.CircleIdVo;
import com.forteach.quiz.interaction.team.web.vo.GroupRandomVo;
import com.forteach.quiz.interaction.team.web.vo.TeamChangeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/23  17:06
 */
@RestController
@Api(value = "课堂分组", tags = {"课堂,上课相关处理"})
@RequestMapping(path = "/classRoom/team")
public class TeamController {

    private TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }


    @ApiOperation(value = "课堂随机分组", notes = "填入分组个数,随机分组")
    @PostMapping(value = "/randomBuild")
    public Mono<WebResult> groupRandom(@ApiParam(value = "填入分组个数,随机分组", required = true) @RequestBody final GroupRandomVo random) {
        return teamService.groupRandom(Mono.just(random)).map(WebResult::okResult);
    }

    @ApiOperation(value = "获取当前课堂team", notes = "传入课堂id 获取当前课堂小组")
    @PostMapping(value = "/nowTeam")
    public Mono<WebResult> nowTeam(@ApiParam(value = "填入分组个数,随机分组", required = true) @RequestBody final CircleIdVo circleId) {
        return teamService.nowTeam(circleId.getCircleId()).map(WebResult::okResult);
    }

    @ApiOperation(value = "新增或移除小组成员", notes = "1 : 增加  2 : 减少")
    @PostMapping(value = "/teamChange")
    public Mono<WebResult> teamChange(@ApiParam(value = "新增或移除小组成员", required = true) @RequestBody final TeamChangeVo changeVo) {
        return teamService.teamChange(changeVo).map(WebResult::okResult);
    }




}

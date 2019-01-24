package com.forteach.quiz.interaction.team.web.control;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.interaction.team.service.TeamService;
import com.forteach.quiz.interaction.team.web.vo.GroupRandomVo;
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
    public Mono<WebResult> groupRandom(@ApiParam(value = "填入分组个数,随机分组", required = true) @RequestBody final Mono<GroupRandomVo> random) {
        return teamService.groupRandom(random).map(WebResult::okResult);
    }


}

package com.forteach.quiz.interaction.team.web.control;

import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.interaction.team.service.TeamPickService;
import com.forteach.quiz.interaction.team.service.TeamRandomService;
import com.forteach.quiz.interaction.team.web.req.CircleIdReq;
import com.forteach.quiz.interaction.team.web.req.GroupRandomReq;
import com.forteach.quiz.interaction.team.web.req.PickTeamReq;
import com.forteach.quiz.interaction.team.web.req.TeamChangeReq;
import com.forteach.quiz.service.TokenService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
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
@RequestMapping(path = "/classRoom/team", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TeamController {

    private final TokenService tokenService;
    private final TeamPickService teamPickService;
    private final TeamRandomService teamRandomService;

    @Autowired
    public TeamController(TeamPickService teamPickService,
                          TeamRandomService teamRandomService,
                          TokenService tokenService) {
        this.tokenService = tokenService;
        this.teamPickService = teamPickService;
        this.teamRandomService = teamRandomService;
    }


    @ApiOperation(value = "课堂随机分组", notes = "填入分组个数,随机分组")
    @PostMapping(value = "/randomBuild")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "circleId", value = "课堂圈子id/课程id", required = true, dataType = "string", paramType = "from"),
            @ApiImplicitParam(name = "number", value = "要分几个小组", required = true, dataType = "int", paramType = "from"),
            @ApiImplicitParam(name = "expType", value = "分组的有效期 forever : 永久, temporarily : 临时", required = true, dataType = "string", paramType = "from")
    })
    public Mono<WebResult> groupRandom(@ApiParam(value = "填入分组个数,随机分组", required = true) @RequestBody GroupRandomReq random, ServerHttpRequest request) {
        MyAssert.isNull(random.getCircleId(), DefineCode.ERR0010, "课堂id或课程id不为空");
        MyAssert.isNull(random.getNumber(), DefineCode.ERR0010, "分组数量不为空");
        MyAssert.isNull(random.getExpType(), DefineCode.ERR0010, "分组有效期不为空");
        random.setTeacherId(tokenService.getTeacherId(request).get());
        return teamRandomService.groupRandom(random).map(WebResult::okResult);
    }

    @ApiOperation(value = "获取当前课堂team", notes = "传入课堂id 获取当前课堂小组")
    @PostMapping(value = "/nowTeam")
    @ApiImplicitParam(name = "circleId", value = "课堂id/课程id", dataType = "string", required = true, paramType = "from")
    public Mono<WebResult> nowTeam(@ApiParam(value = "填入分组个数,随机分组", required = true) @RequestBody final CircleIdReq circleId) {
        MyAssert.isNull(circleId.getCircleId(), DefineCode.ERR0010, "课堂id或课程id不为空");
        return teamRandomService.nowTeam(circleId.getCircleId()).map(WebResult::okResult);
    }

    @ApiOperation(value = "新增或移除小组成员", notes = "1 : 增加  2 : 减少")
    @PostMapping(value = "/teamChange")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "circleId", value = "课堂圈子id/课程id", required = true, dataType = "string", paramType = "from"),
            @ApiImplicitParam(name = "teamId", value = "小组id", required = true, dataType = "string", paramType = "from"),
            @ApiImplicitParam(name = "students", value = "被 新增或移除 小组的 学生id, 逗号分割", required = true, dataType = "string", paramType = "from"),
            @ApiImplicitParam(name = "moreOrLess", value = "1 : 增加  2 : 减少", required = true, dataType = "string", paramType = "from")
    })
    public Mono<WebResult> teamChange(@ApiParam(value = "新增或移除小组成员", required = true) @RequestBody final TeamChangeReq changeVo) {
        MyAssert.isNull(changeVo.getCircleId(), DefineCode.ERR0010, "课堂id或课程id不为空");
        MyAssert.isNull(changeVo.getStudents(), DefineCode.ERR0010, "学生id不为空");
        MyAssert.isNull(changeVo.getTeamId(), DefineCode.ERR0010, "小组id不为空");
        MyAssert.isExcept(Integer.valueOf(changeVo.getMoreOrLess()),1, 2, DefineCode.ERR0010, "增加或减少参数不正确");
        return teamRandomService.teamChange(changeVo).map(WebResult::okResult);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "circleId", value = "课堂Id", dataType = "string", required = true, paramType = "from"),
            @ApiImplicitParam(name = "teamId", value = "小组id", dataType = "string", paramType = "from"),
            @ApiImplicitParam(name = "teamName", value = "小组名字", dataType = "string", paramType = "from"),
            @ApiImplicitParam(name = "students", value = "学生id(用逗号分割)", example = "1234,1235", dataType = "string", required = true, paramType = "from"),
            @ApiImplicitParam(name = "moreOrLess", value = "1 : 增加  2 : 减少", example = "1" ,dataType = "string", required = true, paramType = "from"),
            @ApiImplicitParam(name = "expType", value = "分组的有效期 forever : 永久, temporarily : 临时", example = "forever" ,dataType = "string", required = true, paramType = "from"),
    })
    @PostMapping("/pickTeam")
    @ApiOperation(value = "选人分组")
    public Mono<WebResult> pickTeam(@ApiParam(value = "选人分组", required = true) @RequestBody PickTeamReq req, ServerHttpRequest request){
        MyAssert.isNull(req.getCircleId(), DefineCode.ERR0010, "课堂id不为空");
        MyAssert.isNull(req.getStudents(), DefineCode.ERR0010, "学生信息不为空");
        MyAssert.isExcept(Integer.valueOf(req.getMoreOrLess()),1, 2, DefineCode.ERR0010, "增加或减少参数不正确");
        req.setTeacherId(tokenService.getTeacherId(request).get());
        return teamPickService.pickTeam(req).map(WebResult::okResult);
    }
}

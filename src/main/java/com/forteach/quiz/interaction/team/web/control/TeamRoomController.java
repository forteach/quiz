package com.forteach.quiz.interaction.team.web.control;

import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.interaction.team.service.TeamService;
import com.forteach.quiz.interaction.team.web.req.*;
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
import springfox.documentation.annotations.ApiIgnore;

import static com.forteach.quiz.interaction.team.constant.Dic.TEAM_FOREVER;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/23  17:06
 */
@RestController
@Api(value = "课堂分组", tags = {"课堂,上课相关处理"})
@RequestMapping(path = "/classRoom/team", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TeamRoomController {

    private final TokenService tokenService;
    private final TeamService teamService;

    @Autowired
    public TeamRoomController(TeamService teamService, TokenService tokenService) {
        this.tokenService = tokenService;
        this.teamService = teamService;
    }


    @ApiOperation(value = "课堂随机分组", notes = "填入分组个数,随机分组")
    @PostMapping(value = "/randomBuild")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "circleId", value = "课堂圈子id/课程id", dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "number", value = "要分几个小组", required = true, dataType = "int", paramType = "form"),
            @ApiImplicitParam(name = "classId", value = "班级id", dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "expType", value = "分组的有效期 forever : 永久, temporarily : 临时", required = true, dataType = "string", paramType = "form")
    })
    public Mono<WebResult> groupRandom(@ApiParam(value = "填入分组个数,随机分组", required = true) @RequestBody GroupRandomReq random, @ApiIgnore ServerHttpRequest request) {
        MyAssert.isNull(random.getCircleId(), DefineCode.ERR0010, "课堂id或课程id不为空");
        MyAssert.isNull(random.getNumber(), DefineCode.ERR0010, "分组数量不为空");
        MyAssert.isNull(random.getExpType(), DefineCode.ERR0010, "分组有效期不为空");
        if (TEAM_FOREVER.equals(random.getExpType())){
            MyAssert.isNull(random.getClassId(), DefineCode.ERR0010, "班级id不为空");
        }
        random.setTeacherId(tokenService.getTeacherId(request).get());
        return teamService.groupRandom(random).map(WebResult::okResult);
    }

    @ApiOperation(value = "修改小组名称", notes = "修改已经创建好的小组名称")
    @PostMapping("/updateTeamName")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "teamId", value = "小组id", required = true, dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "teamName", value = "小组名字", required = true, dataType = "string", paramType = "form")
    })
    public Mono<WebResult> changeTeamName(@RequestBody ChangeTeamNameReq req){
        MyAssert.isNull(req.getTeamId(), DefineCode.ERR0010, "小组id不为空");
        MyAssert.isNull(req.getTeamName(), DefineCode.ERR0010, "小组名称不为空");
        return teamService.updateTeamName(req).map(WebResult::okResult);
    }

    @ApiOperation(value = "需要删除小组", notes = "删除不需要的小组成员")
    @ApiImplicitParam(name = "teamId", value = "小组id", required = true, dataType = "string", paramType = "form")
    @PostMapping("/delete")
    public Mono<WebResult> deleteTeam(@RequestBody DeleteTeamReq deleteTeamReq){
        MyAssert.isNull(deleteTeamReq.getTeamId(), DefineCode.ERR0010, "小组id不为空");
        return teamService.deleteTeam(deleteTeamReq).map(WebResult::okResult);
    }

    @ApiOperation(value = "添加小组", notes = "已经建好的课堂新添加小组信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "circleId", value = "课堂圈子id/课程id", example = "必传", required = true, dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "classId", value = "班级id", required = true, example = "必传", dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "expType", value = "分组的有效期 forever : 永久, temporarily : 临时", example = "必传", required = true, dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "students", value = "学生id(用逗号分割)", example = "1234,1235", dataType = "string", required = true, paramType = "form")
    })
    @PostMapping("/addTeam")
    public Mono<WebResult> addTeam(@RequestBody AddTeamReq req){
        MyAssert.isNull(req.getCircleId(), DefineCode.ERR0010, "课堂id或课程id不为空");
        MyAssert.isNull(req.getExpType(), DefineCode.ERR0010, "分组有效期不为空");
        MyAssert.isNull(req.getClassId(), DefineCode.ERR0010, "班级id不为空");
        MyAssert.isNull(req.getStudents(), DefineCode.ERR0010, "学生信息不为空");
        return teamService.addTeam(req).map(WebResult::okResult);
    }

    @ApiOperation(value = "移动小组成员", notes = "对小组成员进行移动")
    @PostMapping(value = "/teamChange")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addTeamId", value = "添加到的小组id", required = true, dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "removeTeamId", value = "需要移除的小组id", required = true, dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "students", value = "被 新增或移除 小组的 学生id, 逗号分割", required = true, dataType = "string", paramType = "form")
    })
    public Mono<WebResult> teamChange(@ApiParam(value = "新增或移除小组成员", required = true) @RequestBody final ChangeTeamReq changeVo) {
        MyAssert.isNull(changeVo.getAddTeamId(), DefineCode.ERR0010, "课堂id或课程id不为空");
        MyAssert.isNull(changeVo.getStudents(), DefineCode.ERR0010, "学生id不为空");
        MyAssert.isNull(changeVo.getRemoveTeamId(), DefineCode.ERR0010, "小组id不为空");
        return teamService.teamChange(changeVo).map(WebResult::okResult);
    }

    @ApiOperation(value = "获取当前课堂小组信息", notes = "传入课堂/课程　id 获取当前课堂小组")
    @PostMapping(value = "/nowTeam")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "circleId", value = "课堂id/课程id", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "classId", value = "班级id", example = "如果是课程必传", dataType = "string", paramType = "query"),
    })
    public Mono<WebResult> nowTeam(@ApiParam(value = "填入分组个数,随机分组", required = true) @RequestBody final CircleIdReq circleIdReq) {
        MyAssert.isNull(circleIdReq.getCircleId(), DefineCode.ERR0010, "课堂id或课程id不为空");
        return teamService.nowTeam(circleIdReq).map(WebResult::okResult);
    }
}

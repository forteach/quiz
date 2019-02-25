package com.forteach.quiz.interaction.execute.web.control;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.interaction.execute.service.ClassRoomService;
import com.forteach.quiz.service.TokenService;
import com.forteach.quiz.web.BaseController;
import com.forteach.quiz.web.req.InteractiveStudentsReq;
import com.forteach.quiz.web.vo.InteractiveRoomVo;
import com.forteach.quiz.web.vo.JoinInteractiveRoomVo;
import io.swagger.annotations.*;
import jdk.nashorn.internal.parser.Token;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/21  17:50
 */
@RestController
@Api(value = "课堂", tags = {"课堂,上课相关处理"})
@RequestMapping(path = "/classRoom")
public class ClassRoomController extends BaseController {

    private final ClassRoomService classRoomService;

    private final TokenService tokenService;

    public ClassRoomController(ClassRoomService classRoomService, TokenService tokenService) {
        this.classRoomService = classRoomService;
        this.tokenService = tokenService;
    }

    @ApiOperation(value = "老师创建临时课堂", notes = "有效期为2个小时 此方法两个小时内返回同一数据")
    @PostMapping(value = "/create/reuse")
    @ApiImplicitParams({
//            @ApiImplicitParam(value = "教师id", name = "teacherId", required = true, dataType = "string", paramType = "from"),
            @ApiImplicitParam(value = "章节id", name = "chapterId", required = true, dataType = "string", paramType = "from")
    })
    public Mono<WebResult> createInteractiveRoom(@ApiParam(value = "发布课堂提问", required = true) @RequestBody InteractiveRoomVo roomVo, ServerHttpRequest request) {
        Optional<String> teacherId = tokenService.getTeacherId(request);
        teacherId.ifPresent(roomVo::setTeacherId);
        return classRoomService.createInteractiveRoom(roomVo).map(WebResult::okResult);
    }

    @ApiOperation(value = "学生加入互动课堂", notes = "学生加入互动课堂")
    @PostMapping(value = "/join/interactiveRoom")
    @ApiImplicitParams({
//            @ApiImplicitParam(value = "学生id", name = "examineeId", required = true, dataType = "string", paramType = "from"),
            @ApiImplicitParam(value = "课堂圈子id", name = "circleId", required = true, dataType = "string", paramType = "from")
    })
    public Mono<WebResult> joinInteractiveRoom(@ApiParam(value = "学生加入互动课堂", required = true) @RequestBody JoinInteractiveRoomVo joinVo, ServerHttpRequest request) {
        joinVo.setExamineeId(tokenService.getStudentId(request));
        return classRoomService.joinInteractiveRoom(joinVo).map(WebResult::okResult);
    }

    @ApiOperation(value = "老师创建临时课堂 覆写", notes = "有效期为2个小时 此方法覆盖之前数据")
    @PostMapping(value = "/create/cover")
    @ApiImplicitParams({
//            @ApiImplicitParam(value = "教师id", name = "teacherId", required = true, dataType = "string", paramType = "from"),
            @ApiImplicitParam(value = "章节id", name = "chapterId", required = true, dataType = "string", paramType = "from")
    })
    public Mono<WebResult> createCoverInteractiveRoom(@ApiParam(value = "发布课堂提问", required = true) @RequestBody InteractiveRoomVo roomVo, ServerHttpRequest request) {
        Optional<String> teacherId = tokenService.getTeacherId(request);
        teacherId.ifPresent(roomVo::setTeacherId);
        return classRoomService.createCoverInteractiveRoom(roomVo).map(WebResult::okResult);
    }

    @ApiOperation(value = "查找加入过的学生", notes = "查找加入过的学生")
    @PostMapping(value = "/find/interactiveStudents")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "课堂圈子id", name = "circleId", dataType = "string", paramType = "query", required = true)
    })
    public Mono<WebResult> findInteractiveStudents(@ApiParam(value = "查找加入课堂的学生", required = true) @RequestBody InteractiveStudentsReq interactiveReq) {
        return classRoomService.findInteractiveStudents(interactiveReq.getCircleId()).map(WebResult::okResult);
    }


}

package com.forteach.quiz.interaction.execute.web.control;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.interaction.execute.service.ClassRoomService;
import com.forteach.quiz.web.BaseController;
import com.forteach.quiz.web.req.InteractiveStudentsReq;
import com.forteach.quiz.web.vo.InteractiveRoomVo;
import com.forteach.quiz.web.vo.JoinInteractiveRoomVo;
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
 * @date: 2018/12/21  17:50
 */
@RestController
@Api(value = "课堂", tags = {"课堂,上课相关处理"})
@RequestMapping(path = "/classRoom")
public class ClassRoomController extends BaseController {

    private final ClassRoomService classRoomService;

    public ClassRoomController(ClassRoomService classRoomService) {
        this.classRoomService = classRoomService;
    }

    @ApiOperation(value = "老师创建临时课堂", notes = "有效期为2个小时 此方法两个小时内返回同一数据")
    @PostMapping(value = "/create/reuse")
    public Mono<WebResult> createInteractiveRoom(@ApiParam(value = "发布课堂提问", required = true) @RequestBody InteractiveRoomVo roomVo) {
        return classRoomService.createInteractiveRoom(roomVo).map(WebResult::okResult);
    }

    @ApiOperation(value = "老师创建临时课堂 覆写", notes = "有效期为2个小时 此方法覆盖之前数据")
    @PostMapping(value = "/create/cover")
    public Mono<WebResult> createCoverInteractiveRoom(@ApiParam(value = "发布课堂提问", required = true) @RequestBody InteractiveRoomVo roomVo) {
        return classRoomService.createCoverInteractiveRoom(roomVo).map(WebResult::okResult);
    }

    @ApiOperation(value = "学生加入互动课堂", notes = "学生加入互动课堂")
    @PostMapping(value = "/join/interactiveRoom")
    public Mono<WebResult> joinInteractiveRoom(@ApiParam(value = "学生加入互动课堂", required = true) @RequestBody JoinInteractiveRoomVo joinVo) {
        return classRoomService.joinInteractiveRoom(joinVo).map(WebResult::okResult);
    }

    @ApiOperation(value = "查找加入过的学生", notes = "查找加入过的学生")
    @PostMapping(value = "/find/interactiveStudents")
    public Mono<WebResult> findInteractiveStudents(@ApiParam(value = "查找加入课堂的学生", required = true) @RequestBody InteractiveStudentsReq interactiveReq) {
        return classRoomService.findInteractiveStudents(interactiveReq).map(WebResult::okResult);
    }


}

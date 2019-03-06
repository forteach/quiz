package com.forteach.quiz.interaction.execute.web.control;

import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.interaction.execute.service.ClassRoomService;
import com.forteach.quiz.web.BaseController;
import com.forteach.quiz.web.req.InteractiveStudentsReq;
import com.forteach.quiz.web.vo.InteractiveRoomVo;
import com.forteach.quiz.web.vo.JoinInteractiveRoomVo;
import io.swagger.annotations.*;
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

    @ApiOperation(value = "老师有效期内创建同一临时课堂", notes = "有效期为2个小时 此方法两个小时内返回同一数据")
    @PostMapping(value = "/create/reuse")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "教师id", name = "teacherId", required = true, dataType = "string", paramType = "from"),
            @ApiImplicitParam(value = "(可以为“”，不能不传)已存在临时课堂ID", name = "circleId", required = false, dataType = "string", paramType = "from"),
            @ApiImplicitParam(value = "章节id", name = "chapterId", required = true, dataType = "string", paramType = "from")
    })
    public Mono<WebResult> createInteractiveRoom(@ApiParam(value = "发布课堂提问", required = true) @RequestBody InteractiveRoomVo roomVo) {
        //验证请求参数
        MyAssert.blank(roomVo.getChapterId(), DefineCode.ERR0010 ,"章节编号不能为空");
        MyAssert.blank(roomVo.getTeacherId(), DefineCode.ERR0010 ,"教师编号不能为空");
        //流式调用
        return classRoomService.createInteractiveRoom(roomVo).map(WebResult::okResult);
    }

    @ApiOperation(value = "老师有效期期内，创建不同临时课堂 覆写", notes = "有效期为2个小时 此方法覆盖之前数据")
    @PostMapping(value = "/create/cover")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "教师id", name = "teacherId", required = true, dataType = "string", paramType = "from"),
            @ApiImplicitParam(value = "章节id", name = "chapterId", required = true, dataType = "string", paramType = "from")
    })
    public Mono<WebResult> createCoverInteractiveRoom(@ApiParam(value = "发布课堂提问", required = true) @RequestBody InteractiveRoomVo roomVo) {
        //验证请求参数
        MyAssert.blank(roomVo.getChapterId(), DefineCode.ERR0010 ,"章节编号不能为空");
        MyAssert.blank(roomVo.getTeacherId(), DefineCode.ERR0010 ,"教师编号不能为空");
        return classRoomService.createCoverInteractiveRoom(roomVo).map(WebResult::okResult);
    }

    @ApiOperation(value = "学生加入互动课堂", notes = "学生加入互动课堂")
    @PostMapping(value = "/join/interactiveRoom")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "学生id", name = "examineeId", required = true, dataType = "string", paramType = "from"),
            @ApiImplicitParam(value = "课堂圈子id", name = "circleId", required = true, dataType = "string", paramType = "from")
    })
    public Mono<WebResult> joinInteractiveRoom(@ApiParam(value = "学生加入互动课堂", required = true) @RequestBody JoinInteractiveRoomVo joinVo) {
        //验证请求参数
        MyAssert.blank(joinVo.getCircleId(), DefineCode.ERR0010 ,"课堂编号不存在");
        MyAssert.blank(joinVo.getExamineeId(), DefineCode.ERR0010 ,"学生编号不存在");
        return classRoomService.joinInteractiveRoom(joinVo).map(WebResult::okResult);
    }
    @PostMapping(value = "/test")
    public Mono<String> test(@RequestBody InteractiveRoomVo roomVo) {

        Mono<Boolean> time = Mono.just("123").flatMap(item -> MyAssert.isFalse(true, DefineCode.ERR0013, "redis操作错误"));
        Mono<Boolean> time1 = Mono.just("123").flatMap(item ->{System.out.println("########"); return MyAssert.isFalse(true, DefineCode.ERR0013, "操作错误");});

        //验证请求参数
        return Mono.just("ok")
                 //不创建key
                 .flatMap(str->{
                     return classRoomService.listTest();
                     //return time.flatMap(a->{System.out.println("*********"); MyAssert.isFalse(true, DefineCode.ERR0013, "redis操作错误"); return Mono.just("123");}).filterWhen(r->Mono.just(true));
                 });
                //如果KEY存在
                //.filterWhen(str->time1);


    }

    @ApiOperation(value = "查找加入过的学生", notes = "查找加入过的学生")
    @PostMapping(value = "/find/interactiveStudents")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "课堂圈子id", name = "circleId", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(value = "教师id", name = "teacherId", required = true, dataType = "string", paramType = "from")
    })
    public Mono<WebResult> findInteractiveStudents(@ApiParam(value = "查找加入课堂的学生", required = true) @RequestBody InteractiveStudentsReq interactiveReq) {
        return classRoomService.findInteractiveStudents(interactiveReq.getCircleId(),interactiveReq.getTeacherId()).map(WebResult::okResult);
    }


}

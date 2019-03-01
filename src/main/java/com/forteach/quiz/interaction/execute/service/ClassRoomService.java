package com.forteach.quiz.interaction.execute.service;

import cn.hutool.core.util.StrUtil;
import com.forteach.quiz.common.DataUtil;
import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.interaction.execute.config.ClassRoomKey;
import com.forteach.quiz.service.StudentsService;
import com.forteach.quiz.web.pojo.Students;
import com.forteach.quiz.web.vo.InteractiveRoomVo;
import com.forteach.quiz.web.vo.JoinInteractiveRoomVo;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.forteach.quiz.util.StringUtil.getRandomUUID;


/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/21  17:53
 */
@Service
public class ClassRoomService {


    private final StudentsService studentsService;
    private final ReactiveStringRedisTemplate stringRedisTemplate;
    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;
    private final InteractRecordExecuteService interactRecordExecuteService;

    public ClassRoomService(StudentsService studentsService, ReactiveStringRedisTemplate stringRedisTemplate, ReactiveHashOperations<String, String, String> reactiveHashOperations
            , InteractRecordExecuteService interactRecordExecuteService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.studentsService = studentsService;
        this.interactRecordExecuteService = interactRecordExecuteService;
    }


    public Mono<String> createInteractiveRoom(final InteractiveRoomVo roomVo) {
        //根据互动课堂KEY值，获得课堂互动ID属性，如果为空，创建互动课堂，否则返回返回互动课堂ID
        return Mono.just(roomVo.getCircleId()).flatMap(str ->
        {
            if (StrUtil.isBlank(str)) {
                //根据互动课堂ID和教师ID，创建Mongo或返回课堂信息
                return interactRecordExecuteService.init(roomVo.getTeacherId())
                        //创建Redis教室和教师信息,过期时间2小时
                        .filterWhen(circleId -> buildRoom(circleId, roomVo.getTeacherId(), roomVo.getChapterId(), roomVo.getRoomKey()));
            } else {
                //如果key过期，则返回不存在为true
                Mono<Boolean> notHasKey = stringRedisTemplate.hasKey(roomVo.getQrCode()).flatMap(res -> Mono.just(!res));
                // Mono<Boolean> HasKey = stringRedisTemplate.hasKey(roomVo.getQrCode()).flatMap(res->{MyAssert.isFalse(res.booleanValue(), DefineCode.ERR0013,"redis操作错误"); return Mono.just(!res);});
                //如果键值过期或不存在，创建新的键值
                return notHasKey
                        .flatMap(result -> {
                            if (result) {
                                return interactRecordExecuteService.init(roomVo.getTeacherId())
                                        //创建Mongo课堂信息,过期时间2小时
                                        .filterWhen(circleId -> buildRoom(circleId, roomVo.getTeacherId(), roomVo.getChapterId(), roomVo.getRoomKey()));
                            } else {
                                //如果键值不过期存在，放回当前键值
                                return Mono.just(roomVo.getCircleId());
                            }
                        });
            }
        });
    }


    /**
     * 重新覆写创建课堂
     * 最后记录创建
     *
     * @param roomVo
     * @return
     */
    public Mono<String> createCoverInteractiveRoom(final InteractiveRoomVo roomVo) {
        return interactRecordExecuteService.init(roomVo.getTeacherId())
                //根据互动课堂ID和教师ID，创建Mongo或返回课堂信息
                .filterWhen(circleId -> buildRoom(circleId, roomVo.getTeacherId(), roomVo.getChapterId(), roomVo.getRoomKey()));
    }

    /**
     * 新建一个临时教室信息有效时间是2个小时
     *
     * @param teacherId 　教室id
     * @param chapterId 课堂id
     * @param roomKey   课堂信息的redis临时前缀
     * @return　Mono<String> 返回课堂id ==> circleId
     */
    private Mono<String> buildRoom(final String teacherId, final String chapterId, final String roomKey) {

        //获取当前时间和两个小时时间后的时间
        LocalTime effectTime = LocalTime.now();//影响时间
        LocalTime failureTime = effectTime.plusHours(2);//失效时间

        //教室redis唯一编号
        final String interactiveId = getRandomUUID();

        //课堂基本信息
        HashMap<String, String> map = new HashMap<>(6);
        String interactiveIdQr = ClassRoomKey.CLASS_ROOM_QR_CODE_PREFIX.concat(interactiveId);
        map.put("interactiveId", interactiveIdQr);
        map.put("effectTime", effectTime.toString());//影响时间
        map.put("failureTime", failureTime.toString());//失效时间
        map.put("teacherId", teacherId);//教师编号
        map.put("chapterId", chapterId);//章节ID
        map.put("createTime", DataUtil.format(new Date()));

        Mono<Boolean> set = reactiveHashOperations.putAll(roomKey, map).flatMap(item -> MyAssert.isFalse(item.booleanValue(), DefineCode.ERR0013, "redis操作错误"));
        Mono<Boolean> time = stringRedisTemplate.expire(roomKey, Duration.ofSeconds(60 * 60 * 2)).flatMap(item -> MyAssert.isFalse(item.booleanValue(), DefineCode.ERR0013, "redis操作错误"));

        //返回课堂ID
        return Mono.just(interactiveIdQr).filterWhen(obj -> set).filterWhen(obj -> time);
    }

    //创建教室和教师
    private Mono<Boolean> buildRoom(final String circleId, final String teacherId, final String chapterId, final String roomKey) {

        //获取当前时间和两个小时时间后的时间
        LocalTime effectTime = LocalTime.now();//影响时间
        LocalTime failureTime = effectTime.plusHours(2);//失效时间

        //课堂基本信息
        HashMap<String, String> map = new HashMap<>(5);
        //课堂Redis 键值
        final String interactiveIdQr = ClassRoomKey.CLASS_ROOM_QR_CODE_PREFIX.concat(circleId);
        map.put("interactiveId", interactiveIdQr);
        map.put("effectTime", effectTime.toString());//影响时间
        map.put("failureTime", failureTime.toString());//失效时间
        map.put("teacherId", teacherId);//教师编号
        map.put("chapterId", chapterId);//章节ID
        map.put("createTime", DataUtil.format(new Date()));
        //创建教师ID的教室，不过期
        Mono<Boolean> set = reactiveHashOperations.putAll(roomKey, map).flatMap(item -> MyAssert.isFalse(item.booleanValue(), DefineCode.ERR0013, "redis操作错误"));

        //返回课堂ID
        return Mono.just(interactiveIdQr).flatMap(id ->
                {
                    //添加教室ID和教室的教师ID
                    return stringRedisTemplate.opsForSet().add(id, teacherId)
                            .flatMap(count -> {
                                return MyAssert.isFalse(count > 0, DefineCode.ERR0013, "redis操作错误");
                            })
                            //设置加入课堂后，两小时过期
                            .filterWhen(count -> stringRedisTemplate.expire(interactiveIdQr, Duration.ofSeconds(60 * 60 * 2)));
                }
        ).filterWhen(obj -> set);
    }

    /**
     * 学生加入课堂
     * 最后记录学生加入信息
     *
     * @return
     */
    public Mono<Long> joinInteractiveRoom(final JoinInteractiveRoomVo joinVo) {
        final String interactiveIdQr = ClassRoomKey.CLASS_ROOM_QR_CODE_PREFIX.concat(joinVo.getCircleId());
        //创建redis学生加入课堂的键值对
        return stringRedisTemplate.opsForSet().add(interactiveIdQr, joinVo.getExamineeId())
                .filterWhen(obj -> interactRecordExecuteService.join(joinVo.getCircleId(), joinVo.getExamineeId()));
    }

    /**
     * 查找加入的学生
     *
     * @param
     * @return
     */
    public Mono<List<Students>> findInteractiveStudents(final String circleId, final String teacherId) {
        return Mono.just(circleId)
                .flatMapMany(interactiveId -> stringRedisTemplate.opsForSet().members(ClassRoomKey.CLASS_ROOM_QR_CODE_PREFIX.concat(interactiveId)))
                .filter(id -> !id.equals(teacherId))//需要过滤掉教师ID
                .flatMap(studentsService::findStudentsBrief)
                .collectList();
    }

    /**
     * 查找加入的学生ID
     *
     * @param
     * @return
     */
    public Mono<List<String>> findInteractiveStudentsID(final String circleId, final String teacherId) {
        return Mono.just(circleId)
                .flatMapMany(interactiveId -> stringRedisTemplate.opsForSet().members(ClassRoomKey.CLASS_ROOM_QR_CODE_PREFIX.concat(interactiveId)))
                .filter(id -> !id.equals(teacherId))
                .collectList();
    }

    /**
     * 获得课堂人数
     *
     * @param circleId
     * @return
     */
    public Mono<Long> studentNumber(final String circleId) {
        return stringRedisTemplate.opsForSet().size(ClassRoomKey.CLASS_ROOM_QR_CODE_PREFIX.concat(circleId));
    }

}

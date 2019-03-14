package com.forteach.quiz.interaction.execute.service;

import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.interaction.execute.service.record.InteractRecordExecuteService;
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
import java.util.HashMap;
import java.util.List;

import static com.forteach.quiz.common.KeyStorage.INTERACTIVE_CLASSROOM_STUDENTS;
import static com.forteach.quiz.util.StringUtil.getRandomUUID;
import static com.forteach.quiz.util.StringUtil.isEmpty;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/21  17:53
 */
@Service
public class ClassRoomService {

    public static final String CLASS_ROOM_QR_CODE_PREFIX = "interactionQr";
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

    /**
     * 学生加入课堂
     * 最后记录学生加入信息
     *
     * @return
     */
    public Mono<Long> joinInteractiveRoom(final JoinInteractiveRoomVo joinVo) {
        return stringRedisTemplate.opsForSet().add(joinVo.getJoinKey(), joinVo.getExamineeId())
                .filterWhen(obj -> stringRedisTemplate.expire(joinVo.getJoinKey(), Duration.ofSeconds(60 * 60 * 2)))
                .filterWhen(obj -> interactRecordExecuteService.join(joinVo.getCircleId(), joinVo.getExamineeId()));

    }

    /**
     * 查找加入的学生
     *
     * @param
     * @return
     */
    public Mono<List<Students>> findInteractiveStudents(final String circleId) {
        return Mono.just(circleId)
                .flatMapMany(interactiveId -> stringRedisTemplate.opsForSet().members(INTERACTIVE_CLASSROOM_STUDENTS.concat(interactiveId)))
                .flatMap(studentsService::findStudentsBrief)
                .collectList();
    }

    /**
     * 获得课堂人数
     *
     * @param circleId
     * @return
     */
    public Mono<Long> studentNumber(final String circleId) {
        return stringRedisTemplate.opsForSet().size(INTERACTIVE_CLASSROOM_STUDENTS.concat(circleId));
    }

    /**
     * 创建课堂
     * 数据有就返回,没有就创建
     * 两个小时
     * 最后记录创建
     *
     * @param roomVo
     * @return
     */
    public Mono<String> createInteractiveRoom(final InteractiveRoomVo roomVo) {
         //根据互动课堂KEY值，获得课堂互动ID属性，如果为空，创建互动课堂，否则返回返回互动课堂ID
        return reactiveHashOperations.get(roomVo.getRoomKey(), "interactiveId").defaultIfEmpty("")
                .flatMap(id -> {
                    if (isEmpty(id)) {
                        //创建Redis课堂信息,过期时间2小时
                        return buildRoom(roomVo.getTeacherId(), roomVo.getChapterId(), roomVo.getRoomKey());
                    } else {
                        return Mono.just(id);
                    }
                })
                //根据互动课堂ID和教师ID，创建或返回课堂信息
                .filterWhen(circleId -> interactRecordExecuteService.init(circleId, roomVo.getTeacherId()));

    }

    /**
     * 重新覆写创建课堂
     * 最后记录创建
     *
     * @param roomVo
     * @return
     */
    public Mono<String> createCoverInteractiveRoom(final InteractiveRoomVo roomVo) {
        return buildRoom(roomVo.getTeacherId(), roomVo.getChapterId(), roomVo.getRoomKey())
                .filterWhen(circleId -> interactRecordExecuteService.init(circleId, roomVo.getTeacherId()));
    }

    /**
     * 新建一个临时教室信息有效时间是2个小时
     * @param teacherId　教室id
     * @param chapterId 课堂id
     * @param roomKey 课堂信息的临时前缀
     * @return　Mono<String> 返回课堂id ==> circleId
     */
    private Mono<String> buildRoom(final String teacherId, final String chapterId, final String roomKey) {

        //获取当前时间和两个小时时间后的时间
        LocalTime effectTime = LocalTime.now();
        LocalTime failureTime = effectTime.plusHours(2);

        final String interactiveId = getRandomUUID();

        HashMap<String, String> map = new HashMap<>(10);
        String interactiveIdQr = CLASS_ROOM_QR_CODE_PREFIX.concat(interactiveId);
        map.put("interactiveId", interactiveIdQr);
        map.put("effectTime", effectTime.toString());
        map.put("failureTime", failureTime.toString());
        map.put("teacherId", teacherId);
        map.put("chapterId", chapterId);

        Mono<Boolean> set = reactiveHashOperations.putAll(roomKey, map).flatMap(item->MyAssert.isFalse(item.booleanValue(), DefineCode.ERR0013,"redis操作错误"));
        Mono<Boolean> time = stringRedisTemplate.expire(roomKey, Duration.ofSeconds(60 * 60 * 2)).flatMap(item->MyAssert.isFalse(item.booleanValue(), DefineCode.ERR0013,"redis操作错误"));

        return Mono.just(interactiveIdQr).filterWhen(obj -> set).filterWhen(obj -> time);
    }


}

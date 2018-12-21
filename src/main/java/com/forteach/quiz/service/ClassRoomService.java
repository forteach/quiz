package com.forteach.quiz.service;

import com.forteach.quiz.web.pojo.Students;
import com.forteach.quiz.web.req.InteractiveStudentsReq;
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

    private final StudentsService studentsService;

    private final ReactiveStringRedisTemplate stringRedisTemplate;

    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;

    public ClassRoomService(StudentsService studentsService, ReactiveStringRedisTemplate stringRedisTemplate, ReactiveHashOperations<String, String, String> reactiveHashOperations) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.studentsService = studentsService;
    }

    /**
     * 学生加入课堂
     *
     * @return
     */
    public Mono<Long> joinInteractiveRoom(final JoinInteractiveRoomVo joinVo) {
        return stringRedisTemplate.opsForSet().add(joinVo.getJoinKey(), joinVo.getExamineeId());

    }

    /**
     * 查找加入的学生
     *
     * @param interactiveReq
     * @return
     */
    public Mono<List<Students>> findInteractiveStudents(final InteractiveStudentsReq interactiveReq) {
        return Mono.just(interactiveReq.getCircleId())
                .flatMapMany(interactiveId -> stringRedisTemplate.opsForSet().members(INTERACTIVE_CLASSROOM_STUDENTS.concat(interactiveId)))
                .flatMap(studentsService::findStudentsBrief)
                .collectList();
    }

    /**
     * 创建课堂
     * 数据有就返回,没有就创建
     * 两个小时
     *
     * @param roomVo
     * @return
     */
    public Mono<String> createInteractiveRoom(final InteractiveRoomVo roomVo) {
        return reactiveHashOperations.get(roomVo.getRoomKey(), "interactiveId")
                .flatMap(id -> {
                    if (isEmpty(id)) {
                        return buildRoom(roomVo.getTeacherId(), roomVo.getChapterId(), roomVo.getRoomKey());
                    } else {
                        return Mono.just(id);
                    }
                });
    }

    /**
     * 重新覆写创建课堂
     *
     * @param roomVo
     * @return
     */
    public Mono<String> createCoverInteractiveRoom(final InteractiveRoomVo roomVo) {
        return buildRoom(roomVo.getTeacherId(), roomVo.getChapterId(), roomVo.getRoomKey());
    }

    private Mono<String> buildRoom(final String teacherId, final String chapterId, final String roomKey) {

        LocalTime effectTime = LocalTime.now();
        LocalTime failureTime = effectTime.plusHours(2);

        final String interactiveId = getRandomUUID();

        HashMap<String, String> map = new HashMap<>(10);
        map.put("interactiveId", getRandomUUID());
        map.put("effectTime", effectTime.toString());
        map.put("failureTime", failureTime.toString());
        map.put("teacherId", teacherId);
        map.put("chapterId", chapterId);

        Mono<Boolean> set = reactiveHashOperations.putAll(roomKey, map);
        Mono<Boolean> time = stringRedisTemplate.expire(roomKey, Duration.ofSeconds(60 * 60 * 2));

        return Mono.just(interactiveId).filterWhen(obj -> set).filterWhen(obj -> time);
    }


}

package com.forteach.quiz.interaction.execute.service.ClassRoom;

import cn.hutool.core.util.StrUtil;
import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.interaction.execute.service.Key.ClassRoomKey;
import com.forteach.quiz.service.StudentsService;
import com.forteach.quiz.web.pojo.Students;
import org.bson.types.ObjectId;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static com.forteach.quiz.interaction.team.constant.Dic.CLASS_ROOM;

/**
 * @Description: 创建课堂
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/21  17:53
 */
@Service
public class ClassRoomService {


    private final StudentsService studentsService;
    private final ReactiveStringRedisTemplate stringRedisTemplate;
//    private final InteractRecordExecuteService interactRecordExecuteService;

    public ClassRoomService(StudentsService studentsService, ReactiveStringRedisTemplate stringRedisTemplate, ReactiveHashOperations<String, String, String> reactiveHashOperations
//            , InteractRecordExecuteService interactRecordExecuteService
    ) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.studentsService = studentsService;
//        this.interactRecordExecuteService = interactRecordExecuteService;
    }


    public Mono<String> createInteractiveRoom(final String circleId,final String teacherId,final String chapterId) {

        //TODO 创建课堂ID，REDIS键值，安课堂ID过期键值处理过期
        final String newcircleId=ObjectId.get().toString();
        //根据互动课堂KEY值，获得课堂互动ID属性，如果为空，创建互动课堂，否则返回返回互动课堂ID
        return Mono.just(teacherId).flatMap(tid ->
        {
            if (StrUtil.isBlank(circleId)) {

                return Mono.just(newcircleId)
                        //设置当前课堂ID
                        .filterWhen(cid->setInteractionType(cid,ClassRoomKey.CLASSROOM_JOIN_QUESTIONS_ID))
                        //创建Redis教室和教师信息,过期时间2小时;
                        .filterWhen(cid->buildRoom(cid, teacherId,chapterId));

            } else {
                //如果key过期，则返回不存在为true
                Mono<Boolean> notHasKey = stringRedisTemplate.hasKey(ClassRoomKey.getInteractiveIdQra(circleId));
                return notHasKey
                        .flatMap(result -> {
                            if (result) {
                                //如果键值存在，返回当前键值
                                return Mono.just(circleId);

                            } else {
                                //不存在，创建教室信息
                                return Mono.just(newcircleId).filterWhen(cid->buildRoom(cid, teacherId,chapterId));
                            }
                        });
                }
        });
//                //TODO 需要修改
//                .filterWhen(tid->interactRecordExecuteService.init(newcircleId,teacherId,chapterId));
    }

    /**
     * 设置当前课堂当前活动主题
     * @param circleId
     */
    public Mono<Boolean> setInteractionType(String circleId,String value){
        final String key= ClassRoomKey.setInteractionType(circleId);
        return stringRedisTemplate.opsForValue().set(key, value,Duration.ofSeconds(60*60*2));
    }


    /**
     * 创建教室和教师
     * @param circleId
     * @param teacherId
     * @return
     */
    private Mono<Boolean> buildRoom(final String circleId, final String teacherId,final String chapterId) {

        //设置课堂成员
        final String interactiveIdQr = ClassRoomKey.getInteractiveIdQra(circleId);
        final Mono<Boolean> roomUser =stringRedisTemplate.opsForSet().add(interactiveIdQr, teacherId)
                .flatMap(count -> MyAssert.isTrue(count == 0, DefineCode.ERR0013, "添加课堂教师失败"))
                //设置加入课堂后，两小时过期
                .filterWhen(count -> stringRedisTemplate.expire(interactiveIdQr, Duration.ofSeconds(60 * 60 * 12)));

        // TODO 正在开课的教室，以后可以创建个任务，清理这里无效的数据？****
        final Mono<Boolean> openRoomIDs = stringRedisTemplate.opsForSet().add(ClassRoomKey.OPEN_CLASSROOM,circleId)
                .flatMap(count -> MyAssert.isTrue(count==0, DefineCode.ERR0013, "创建课堂失败"))
                //设置正在开课的课堂ID集合2小时过期
                .filterWhen(count -> stringRedisTemplate.expire(ClassRoomKey.OPEN_CLASSROOM, Duration.ofSeconds(60 * 60 * 12)));

        //设置上课的教师
        final Mono<Boolean> RoomTeacher =stringRedisTemplate.opsForValue()
                .set(ClassRoomKey.getRoomTeacherKey(circleId),teacherId, Duration.ofSeconds(60 * 60 * 12))
                .flatMap(item -> MyAssert.isFalse(item, DefineCode.ERR0013, "创建课堂记录教师失败"));

        //设置上课的章节
        final Mono<Boolean> RoomChapter =stringRedisTemplate.opsForValue()
                .set(ClassRoomKey.getRoomChapterKey(circleId),chapterId, Duration.ofSeconds(60 * 60 * 2))
                .flatMap(item -> MyAssert.isFalse(item, DefineCode.ERR0013, "创建课堂章节信息失败"));

        //创建单个活跃课堂，并返回课堂ID
        return  roomUser.filterWhen(obj->openRoomIDs).filterWhen(obj->RoomTeacher).filterWhen(obj->RoomChapter);
    }

    /**
     * 学生加入课堂
     * 最后记录学生加入信息
     *
     * @return
     */
    public Mono<Long> joinInteractiveRoom(final String circleId,final String examineeId) {
        //创建redis学生加入课堂的键值对
        return Mono.just( ClassRoomKey.getInteractiveIdQra(circleId)).flatMap(key->stringRedisTemplate.opsForSet().add(key, examineeId));
//                .filterWhen(obj -> interactRecordExecuteService.join(circleId, examineeId)));
    }

    /**
     * 查找加入的学生
     *
     * @param
     * @return
     */
    public Mono<List<Students>> findInteractiveStudents(final String circleId, final String teacherId) {
        return Mono.just(circleId)
                .flatMapMany(cId -> stringRedisTemplate.opsForSet().members(ClassRoomKey.getInteractiveIdQra(cId)))
                //需要过滤掉教师ID
                .filter(id -> !id.equals(teacherId))
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
        return stringRedisTemplate.opsForSet().size(ClassRoomKey.getInteractiveIdQra(circleId));
    }


    /**
     * 通过班级id查找对应的学生信息
     * @param classId
     * @return
     */
    public Mono<List<Students>> findClassStudents(final String classId){
        return stringRedisTemplate.opsForSet().members(CLASS_ROOM.concat(classId)).collectList()
                .filter(Objects::nonNull)
                .flatMap(stringList -> studentsService.exchangeStudents(stringList));
    }
}

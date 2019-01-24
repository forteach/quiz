package com.forteach.quiz.interaction.team.service;

import com.forteach.quiz.exceptions.CustomException;
import com.forteach.quiz.interaction.execute.service.ClassRoomService;
import com.forteach.quiz.interaction.team.web.vo.GroupRandomVo;
import com.forteach.quiz.interaction.team.web.vo.GroupTeamVo;
import com.forteach.quiz.interaction.team.web.vo.Team;
import com.forteach.quiz.web.pojo.Students;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

import static com.forteach.quiz.util.StringUtil.getRandomUUID;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/22  15:48
 */
@Service
public class TeamService {

    private final ReactiveStringRedisTemplate stringRedisTemplate;
    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final ClassRoomService classRoomService;

    public TeamService(ReactiveStringRedisTemplate stringRedisTemplate,
                       ReactiveHashOperations<String, String, String> reactiveHashOperations,
                       ReactiveMongoTemplate reactiveMongoTemplate,
                       ClassRoomService classRoomService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.classRoomService = classRoomService;
    }

    /**
     * 随机分组
     *
     * @return
     */
    public Mono<GroupTeamVo> groupRandom(final Mono<GroupRandomVo> random) {

        return random
                .filterWhen(randomVo -> allotVerify(classRoomService.studentNumber(randomVo.getCircleId()), randomVo.getNumber()))
                .flatMap(randomVo -> {

                    Mono<List<Students>> list = classRoomService.findInteractiveStudents(randomVo.getCircleId()).transform(this::shuffle);

                    return list.map(students -> {
                        //总数 , 组数 , 每组个数 , 余数 ,余数累加值
                        int size = students.size();
                        int teamNumber = randomVo.getNumber();
                        int nitems = size / teamNumber;
                        int residue = size % teamNumber;
                        int cumulative = 0;

                        GroupTeamVo grouping = new GroupTeamVo();

                        //截取分组
                        for (int i = 0; i < teamNumber; i++) {

                            List<Students> studentsList;
                            //余数累加
                            if (residue != 0) {
                                studentsList = students.subList(i * nitems + cumulative, i * nitems + nitems + 1 + cumulative);
                                cumulative++;
                                residue--;
                            } else {
                                studentsList = students.subList(i * nitems + cumulative, i * nitems + nitems + cumulative);
                            }

                            grouping.addTeamList(new Team(getRandomUUID(), studentsList));

                        }

                        return grouping;

                    });
                });
    }


    /**
     * 至少每组两个人
     *
     * @param size
     * @param number
     * @return
     */
    private Mono<Boolean> allotVerify(final Mono<Long> size, final Integer number) {
        return size
                .map(count -> count > number * 2)
                .map(flag -> {
                    if (flag) {
                        return flag;
                    } else {
                        throw new CustomException("分组时 至少需要条件达到每组两个人");
                    }
                });
    }

    /**
     * 打乱学生列表顺序
     *
     * @param listMono
     * @return
     */
    private Mono<List<Students>> shuffle(final Mono<List<Students>> listMono) {
        return listMono
                .map(list -> {
                    Collections.shuffle(list);
                    return list;
                });
    }

}

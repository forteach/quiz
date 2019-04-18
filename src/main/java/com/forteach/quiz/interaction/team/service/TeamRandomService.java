package com.forteach.quiz.interaction.team.service;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.forteach.quiz.exceptions.CustomException;
import com.forteach.quiz.interaction.execute.service.ClassRoomService;
import com.forteach.quiz.interaction.team.web.req.GroupRandomReq;
import com.forteach.quiz.interaction.team.web.req.TeamChangeReq;
import com.forteach.quiz.interaction.team.web.resp.GroupTeamResp;
import com.forteach.quiz.interaction.team.web.resp.TeamResp;
import com.forteach.quiz.service.StudentsService;
import com.forteach.quiz.web.pojo.Students;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import static com.forteach.quiz.common.Dic.ASK_GROUP_CHANGE_LESS;
import static com.forteach.quiz.common.Dic.ASK_GROUP_CHANGE_MORE;
import static com.forteach.quiz.interaction.team.web.req.GroupRandomReq.groupKey;
import static com.forteach.quiz.util.StringUtil.isNotEmpty;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-4-11 13:53
 * @version: 1.0
 * @description:
 */
@Service
@Slf4j
public class TeamRandomService {

    private final ReactiveStringRedisTemplate stringRedisTemplate;
    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final ReactiveRedisTemplate redisTemplate;
    private final ClassRoomService classRoomService;
    private final StudentsService studentsService;
    private final TeamService teamService;

    public TeamRandomService(ReactiveStringRedisTemplate stringRedisTemplate,
                       ReactiveHashOperations<String, String, String> reactiveHashOperations,
                       ReactiveMongoTemplate reactiveMongoTemplate,
                       ReactiveRedisTemplate reactiveRedisTemplate,
                       TeamService teamService,
                       ClassRoomService classRoomService,
                       StudentsService studentsService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.redisTemplate = reactiveRedisTemplate;
        this.classRoomService = classRoomService;
        this.studentsService = studentsService;
        this.teamService = teamService;
    }


    /**
     * 随机分组
     *
     * @return
     */
    public Mono<GroupTeamResp> groupRandom(final GroupRandomReq random) {

        return Mono.just(random).filterWhen(randomVo -> allotVerify(classRoomService.studentNumber(randomVo.getCircleId()), randomVo.getNumber()))
                .flatMap(randomVo -> {

                    Mono<List<Students>> list = classRoomService.findInteractiveStudents(randomVo.getCircleId(), random.getTeacherId()).transform(this::shuffle);

                    return list.map(students -> {
                        //总数 , 组数 , 每组个数 , 余数 ,余数累加值
                        int size = students.size();
                        int teamNumber = randomVo.getNumber();
                        int tuple = size / teamNumber;
                        int residue = size % teamNumber;
                        int cumulative = 0;

                        GroupTeamResp grouping = new GroupTeamResp();

                        //截取分组
                        for (int i = 0; i < teamNumber; i++) {

                            List<Students> studentsList;
                            //余数累加
                            if (residue != 0) {
                                studentsList = students.subList(i * tuple + cumulative, i * tuple + tuple + 1 + cumulative);
                                cumulative++;
                                residue--;
                            } else {
                                studentsList = students.subList(i * tuple + cumulative, i * tuple + tuple + cumulative);
                            }

                            grouping.addTeamList(new TeamResp(IdUtil.objectId(), "小组 ".concat(String.valueOf(i + 1)), studentsList));

                        }

                        return grouping;

                        //分组完成时保存进redis 随机分组会覆盖小组信息
                    }).filterWhen(grouping -> saveGroup(grouping.getTeamList(), randomVo.getGroupKey()));
                });
    }

    /**
     * 获取现存的team信息
     *
     * @return
     */
    public Mono<List<TeamResp>> nowTeam(final String circleId) {
        return redisTemplate.opsForValue().get(groupKey(circleId)).switchIfEmpty(Mono.just(new ArrayList()))
                .map(obj -> JSON.parseObject(JSON.toJSONString(obj), new TypeReference<List<TeamResp>>() {
                }));
    }


    /**
     * 覆盖分组信息
     * 保存学生分组信息至redis
     *
     * @return
     */
    private Mono<Boolean> saveGroup(final List<TeamResp> teamList, final String groupKey) {
        return redisTemplate.opsForValue().set(groupKey, teamList, Duration.ofDays(1));
    }

    /**
     * 新增或移除小组成员
     *
     * @param changeVo
     * @return
     */
    public Mono<List<TeamResp>> teamChange(final TeamChangeReq changeVo) {

        switch (changeVo.getMoreOrLess()) {
            case ASK_GROUP_CHANGE_MORE:
                return teamMore(changeVo.getCircleId(), changeVo.getTeamId(), changeVo.getStudents())
                        .filterWhen(grouping -> saveGroup(grouping, changeVo.getGroupKey()));
            case ASK_GROUP_CHANGE_LESS:
                return teamLess(changeVo.getCircleId(), changeVo.getTeamId(), changeVo.getStudents())
                        .filterWhen(grouping -> saveGroup(grouping, changeVo.getGroupKey()));
            default:
                throw new CustomException("非法参数 错误的小组更改类型");
        }

    }

    /**
     * 小组增加学生
     *
     * @param circleId
     * @param teamId
     * @param students
     * @return
     */
    private Mono<List<TeamResp>> teamMore(final String circleId, final String teamId, final String students) {
        //获得小组list
        Mono<List<TeamResp>> nowTeam = nowTeam(circleId);
        //查询出学生信息
        Mono<List<Students>> ids = teamService.changeStudents(students);
        //组合Mono数据
        Mono<Tuple2<List<TeamResp>, List<Students>>> tuple2 = Mono.zip(nowTeam, ids);
        //小组增加学生
        return tuple2.map(tup -> {
            //peek 增加 遍历小组 如果小组id相同 add
            return tup.getT1().stream()
                    .peek(team -> {
                        if (team.getTeamId().equals(teamId)) {
                            team.getStudents().addAll(tup.getT2());
                        }
                    }).collect(Collectors.toList());
        });
    }

    private Mono<List<TeamResp>> teamLess(final String circleId, final String teamId, final String students) {
        //获得小组list
        Mono<List<TeamResp>> nowTeam = nowTeam(circleId);
        //获得被移除的学生
        Mono<List<String>> ids = teamService.changeStringStudent(students);
        //组合Mono数据
        Mono<Tuple2<List<TeamResp>, List<String>>> tuple2 = Mono.zip(nowTeam, ids);
        //小组删除学生
        return tuple2.map(tup -> {
            //peek 增加 遍历小组 如果小组id相同 删除其中学生
            return tup.getT1().stream().peek(team -> {
                //对team的学生列表遍历 如果id一致  删除
                if (team.getTeamId().equals(teamId)) {
                    //对比学生id,一致 剔除
                    team.setStudents(team.getStudents().stream().peek(now -> {
                        if (tup.getT2().contains(now.getId())) {
                            now.setId("");
                        }
                    }).filter(now -> isNotEmpty(now.getId())).collect(Collectors.toList()));

                }


            }).collect(Collectors.toList());
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
                        return true;
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

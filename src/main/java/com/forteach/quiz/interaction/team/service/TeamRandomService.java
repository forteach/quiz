package com.forteach.quiz.interaction.team.service;

import cn.hutool.core.util.IdUtil;
import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.exceptions.CustomException;
import com.forteach.quiz.interaction.execute.service.ClassRoomService;
import com.forteach.quiz.interaction.team.web.req.ChangeTeamReq;
import com.forteach.quiz.interaction.team.web.req.CircleIdReq;
import com.forteach.quiz.interaction.team.web.req.GroupRandomReq;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.forteach.quiz.common.Dic.TEAM_FOREVER;
import static com.forteach.quiz.common.Dic.TEAM_TEMPORARILY;

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

        return Mono.just(random)
                .filterWhen(randomVo -> {
                    if (TEAM_TEMPORARILY.equals(randomVo.getExpType())) {
                        return classRoomService.studentNumber(randomVo.getCircleId())
                                .flatMap(l -> this.allotVerify(l, randomVo.getNumber()))
                                .flatMap(f -> MyAssert.isFalse(f, DefineCode.ERR0002, "分组时 至少需要条件达到每组两个人"));
                    } else if (TEAM_FOREVER.equals(randomVo.getExpType())) {
                        return Mono.just(true);
                    } else {
                        return MyAssert.isNull(null, DefineCode.ERR0002, "有效期参数错误");
                    }
                })
                .flatMap(randomVo -> {
                    // TODO 需要做查询对应的班级所有学生
                    Mono<List<Students>> list = classRoomService.findInteractiveStudents(randomVo.getCircleId(), random.getTeacherId()).transform(this::shuffle);
                    return list.flatMap(l -> groupTeam(l, randomVo));
                })
                .filterWhen(groupTeamResp -> teamService.deleteTeams(random.getGroupKey()))
                .filterWhen(groupTeamResp -> teamService.saveRedisTeams(groupTeamResp.getTeamList(), random));
    }

    Mono<GroupTeamResp> groupTeam(final List<Students> list, final GroupRandomReq randomVo) {
        return Mono.just(list)
                .map(students -> {
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
        });//.filterWhen(grouping -> saveGroup(grouping.getTeamList(), randomVo.getGroupKey(), randomVo.getExpType()))
//                .filterWhen(groupTeamResp -> teamService.saveRedisTeams(groupTeamResp.getTeamList(), randomVo));
    }

    /**
     * 获取现存的team信息
     *
     * @return
     */
//    public Mono<List<TeamResp>> nowTeam(final String circleId, final String classId) {
//        return redisTemplate.opsForValue().get(groupKey(circleId, classId)).switchIfEmpty(Mono.just(new ArrayList()))
//                .map(obj -> JSON.parseObject(JSON.toJSONString(obj), new TypeReference<List<TeamResp>>() {
//                }));
//    }
    public Mono<List<TeamResp>> nowTeam(final CircleIdReq req) {
        return Mono.just(req.getGroupKey())
                .flatMap(key -> stringRedisTemplate.opsForSet().members(key).collectList())
                .flatMapMany(Flux::fromIterable)
                .flatMap(this::findTeam)
                .collectList();
    }

    private Mono<TeamResp> findTeam(final String teamId) {
        final String key = ChangeTeamReq.concatTeamKey(teamId);
        return reactiveHashOperations.get(key, "students")
                .flatMap(teamService::findListStudentsByStudentStr)
                .filter(Objects::nonNull)
                .flatMap(studentsList -> {
                    return reactiveHashOperations.get(key, "teamName")
                            .flatMap(name -> {
                                return Mono.just(new TeamResp(teamId, name, studentsList));
                            });
                });
    }

    /**
     * 覆盖分组信息
     * 保存学生分组信息至redis
     *
     * @return
     */
//    private Mono<Boolean> saveGroup(final List<TeamResp> teamList, final String groupKey, final String expType) {
//        return Mono.just(expType)
//                .flatMap(e -> {
//                    if (TEAM_TEMPORARILY.equals(e)){
//                        return redisTemplate.opsForValue().set(groupKey, teamList, Duration.ofDays(1));
//                    }else if (TEAM_FOREVER.equals(e)){
//                        return redisTemplate.opsForValue().set(groupKey, teamList, Duration.ofDays(365));
//                    }else {
//                        return Mono.error(new Exception("分组的有效期不正确"));
//                    }
//                });
//    }

    /**
     * 新增或移除小组成员
     *
     * @param changeVo
     * @return
     */
//    public Mono<List<TeamResp>> teamChange(final TeamChangeReq changeVo) {
//
//        switch (changeVo.getMoreOrLess()) {
//            case ASK_GROUP_CHANGE_MORE:
//                return teamMore(changeVo.getCircleId(), changeVo.getClassId(), changeVo.getTeamId(), changeVo.getStudents())
//                        .filterWhen(grouping -> saveGroup(grouping, changeVo.getGroupKey(), changeVo.getClassId()));
//            case ASK_GROUP_CHANGE_LESS:
//                return teamLess(changeVo.getCircleId(), changeVo.getClassId(), changeVo.getTeamId(), changeVo.getStudents())
//                        .filterWhen(grouping -> saveGroup(grouping, changeVo.getGroupKey(), changeVo.getClassId()));
//            default:
//                throw new CustomException("非法参数 错误的小组更改类型");
//        }

//    }
    public Mono<Boolean> teamChange(final ChangeTeamReq changeVo) {
        return Mono.just(changeVo.getTeamKey(changeVo.getRemoveTeamId()))
                .flatMap(key -> {
                    return reactiveHashOperations.get(key, "students")
                            .filterWhen(s -> {
                                return teamService.lessJoinTeamStudents(changeVo.getStudents(), s)
                                        .flatMap(students -> reactiveHashOperations.put(key, "students", teamService.studentsListToStr(students)));
                            })
                            .filterWhen(s -> {
                                return teamService.moreJoinTeamStudents(changeVo.getStudents(), s)
                                        .flatMap(students -> reactiveHashOperations.put(key, "students", teamService.studentsListToStr(students)));
                            });
                }).map(Objects::nonNull);
    }

    /**
     * 小组增加学生
     *
     * @param circleId
     * @param teamId
     * @param students
     * @return
     */
//    private Mono<List<TeamResp>> teamMore(final String circleId, final String classId, final String teamId, final String students) {
//        //获得小组list
//        Mono<List<TeamResp>> nowTeam = nowTeam(circleId, classId);
//        //查询出学生信息
//        Mono<List<Students>> ids = teamService.changeStudents(students);
//        //组合Mono数据
//        Mono<Tuple2<List<TeamResp>, List<Students>>> tuple2 = Mono.zip(nowTeam, ids);
//        //小组增加学生
//        return tuple2.map(tup -> {
//            //peek 增加 遍历小组 如果小组id相同 add
//            return tup.getT1().stream()
//                    .peek(team -> {
//                        if (team.getTeamId().equals(teamId)) {
//                            team.getStudents().addAll(tup.getT2());
//                        }
//                    }).collect(Collectors.toList());
//        });
//    }

//    private Mono<List<TeamResp>> teamLess(final String circleId, final String classId, final String teamId, final String students) {
//        //获得小组list
//        Mono<List<TeamResp>> nowTeam = nowTeam(circleId, classId);
//        //获得被移除的学生
//        Mono<List<String>> ids = teamService.changeStringStudent(students);
//        //组合Mono数据
//        Mono<Tuple2<List<TeamResp>, List<String>>> tuple2 = Mono.zip(nowTeam, ids);
//        //小组删除学生
//        return tuple2.map(tup -> {
//            //peek 增加 遍历小组 如果小组id相同 删除其中学生
//            return tup.getT1().stream().peek(team -> {
//                //对team的学生列表遍历 如果id一致  删除
//                if (team.getTeamId().equals(teamId)) {
//                    //对比学生id,一致 剔除
//                    team.setStudents(team.getStudents().stream().peek(now -> {
//                        if (tup.getT2().contains(now.getId())) {
//                            now.setId("");
//                        }
//                    }).filter(now -> isNotEmpty(now.getId())).collect(Collectors.toList()));
//
//                }
//
//
//            }).collect(Collectors.toList());
//        });
//    }

    /**
     * 至少每组两个人
     *
     * @param size
     * @param number
     * @return
     */
    private Mono<Boolean> allotVerify(final Long size, final Integer number) {
        return Mono.just(size)
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


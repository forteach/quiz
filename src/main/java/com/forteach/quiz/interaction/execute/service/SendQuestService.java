package com.forteach.quiz.interaction.execute.service;

import com.alibaba.fastjson.JSON;
import com.forteach.quiz.common.DataUtil;
import com.forteach.quiz.interaction.execute.config.BigQueKey;
import com.forteach.quiz.interaction.execute.service.record.InteractRecordQuestionsService;
import com.forteach.quiz.questionlibrary.domain.QuestionType;
import com.forteach.quiz.questionlibrary.repository.BigQuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;

/**
 * 课堂发布提问题目
 */
@Slf4j
@Service
public class SendQuestService {

    private final ReactiveStringRedisTemplate stringRedisTemplate;
    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;
    private final BigQuestionRepository bigQuestionRepository;
    private final InteractRecordQuestionsService interactRecordQuestionsService;
    private final InteractRecordExecuteService interactRecordExecuteService;
    public SendQuestService(ReactiveStringRedisTemplate stringRedisTemplate,
                            ReactiveHashOperations<String, String, String> reactiveHashOperations,
                            InteractRecordQuestionsService interactRecordQuestionsService,
                            InteractRecordExecuteService interactRecordExecuteService,
                            BigQuestionRepository bigQuestionRepository) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.bigQuestionRepository=bigQuestionRepository;
        this.interactRecordQuestionsService = interactRecordQuestionsService;
        this.interactRecordExecuteService = interactRecordExecuteService;
    }

    /**
     *
     * @param circleId   课堂编号
     * @param teacherId  课堂教师
     * @param questId    问题ID
     * @param interactive  //互动方式（举手、抢答等）
     * @param category //选取类别（个人、小组）
     * @param selected //选中人员 [逗号 分割](stu01,sut02)
     * @param cut //随机数
     * @return
     */
    public Mono<Boolean> sendQuestion(final String circleId,final String teacherId,final String questionType,final String questId, final String interactive,final String category,final String selected,final String cut) {

        //创建课堂提问的题目36分钟过期
        final Mono<Boolean> addQuestNow = addQuestNowInfo(circleId,teacherId,questId,questionType,interactive,category,selected,cut);

        //创建课堂问题列表记录
        final Mono<Boolean> createQuest = addQuestList( circleId, interactive, questId);

        //执行创建提问，并返回执行结果
        return addQuestNow.flatMap(r->createQuest)
                //创建mongo答题日志
                .flatMap(r->interactRecordQuestionsService.releaseQuestion(circleId, questId, selected, category, interactive));
    }

    /**
     *
     * @param circleId   课堂编号
     * @param teacherId  课堂教师
     * @param questId    问题ID
     * @param interactive  //互动方式（举手、抢答等）
     * @param category //选取类别（个人、小组）
     * @param cut //随机数
     * @return
     */
    public Mono<Boolean> raiseSendQuestion(final String circleId,final String teacherId,final String questionType,final String questId, final String interactive,final String category,final String cut) {

        //创建课堂提问的题目36分钟过期
        final Mono<Boolean> addQuestNow = addQuestNowInfo(circleId,teacherId,questId,questionType,interactive,category,"",cut);

        //创建课堂问题列表记录
        final Mono<Boolean> createQuest = addQuestList( circleId, interactive, questId);

        //执行创建提问，并返回执行结果
        return addQuestNow.flatMap(r->createQuest);
                //创建mongo答题日志
               // .flatMap(r->StrUtil.isBlank(selected)?Mono.just(true):interactRecordExecuteService.releaseQuestion(circleId, questId, selected, category, interactive));
    }

    /**
     *设置当前发送题目基本信息
     * @param circleId   课堂编号
     * @param teacherId  课堂教师
     * @param questId    问题ID
     * @param questionType    问题类型  提问、任务
     * @param interactive  //互动方式（举手、抢答等）
     * @param category //选取类别（个人、小组）
     * @param selected //选中人员 [逗号 分割](stu01,sut02)
     * @param cut //随机数
     * @return true or false
     */
    private Mono<Boolean> addQuestNowInfo(final String circleId,final String teacherId,final String questId,final String questionType, final String interactive,final String category,final String selected,final String cut){
       //TODO 需要调整final
        HashMap<String, String> map = new HashMap<>(10);
        //当前课堂ID
        map.put("circleId",circleId);
        //当前课堂教师ID
        map.put("teacherId",teacherId);
        //题目类型
        map.put("questionType", questionType);
        //题目编号
        map.put("questionId", questId);
        //互动方式（举手、抢答等）
        map.put("interactive", interactive);
        //选取类别（个人、小组）
        map.put("category", category);
        //选中人员 [逗号 分割]
        map.put("selected", selected);
        //随机数
        map.put("cut", cut);
        //创建时间
        map.put("time", DataUtil.format(new Date()));
        //创建课堂提问的题目2小时过期
       return reactiveHashOperations.putAll(BigQueKey.questionsIdNow(circleId), map)
                //设置题目信息
               .flatMap(r->setQuestInfo(questId))
               //key:circleId+"now"
               .filterWhen(r->stringRedisTemplate.expire(BigQueKey.questionsIdNow(circleId), Duration.ofSeconds(60*60*2)));

    }

    /**
     * 设置当前题目内容到Redis
     * @param questionId
     * @return
     */
    private Mono<Boolean> setQuestInfo(final String questionId){
        //存储当前所发布的题目信息
       final String key=BigQueKey.questionsNow(questionId);
       return stringRedisTemplate.hasKey(key)
               .flatMap(r->r.booleanValue()?Mono.just(true):bigQuestionRepository.findById(questionId)
                                                            .flatMap(obj->
                                                                    stringRedisTemplate.opsForValue().set(key,JSON.toJSONString(obj),Duration.ofSeconds(60*60*2))));
    }

    /**
     * 添加当前发布题目辅助键信息
     * @param circleId
     * @param interactive
     * @param questId
     * @return
     */
    private Mono<Boolean> addQuestList(final String circleId,final String interactive,final String questId){
        //创建交互题目的互动方式的先后顺序发布列表
        return stringRedisTemplate.opsForList().leftPush(BigQueKey.askTypeQuestionsId(QuestionType.TiWen.name(),  circleId,  interactive), questId)
                //创建交互题目发布哈希列表
                .flatMap(l->stringRedisTemplate.opsForSet().add(BigQueKey.askTypeQuestionsId(QuestionType.TiWen.name(),  circleId),questId)
                        .filterWhen(l1->stringRedisTemplate.expire(BigQueKey.askTypeQuestionsId(QuestionType.TiWen.name(),  circleId), Duration.ofSeconds(60*60*2))))
                .flatMap(item -> {
//                    //获得当前题目ID和创建新的发布题目Key=题目ID
                            return  stringRedisTemplate.hasKey(BigQueKey.askTypeQuestionsIdNow(QuestionType.TiWen.name(), circleId,  interactive))
                                    .flatMap(r->{
                                        if(!r.booleanValue()){
                                            //如果该键值不存在，就创建键值
                                           return stringRedisTemplate.opsForValue().set(BigQueKey.askTypeQuestionsIdNow(QuestionType.TiWen.name(), circleId,  interactive),questId,Duration.ofSeconds(60 * 60 * 2));
                                        }else{
                                            //获得当前键值，并设置新的键值
                                          return  stringRedisTemplate.opsForValue().getAndSet(BigQueKey.askTypeQuestionsIdNow(QuestionType.TiWen.name(), circleId,  interactive),questId)
                                                    .flatMap(old->stringRedisTemplate.opsForValue().set(BigQueKey.askTypeQuestionsIdPrve(QuestionType.TiWen.name(), circleId,interactive),old,Duration.ofSeconds(60 * 60 * 2)));
                                        }
                                    });
                        }
                )
                //设置题目列表的过期时间
                .filterWhen(ok->stringRedisTemplate.expire(BigQueKey.askTypeQuestionsId(QuestionType.TiWen.name(), circleId,  interactive), Duration.ofSeconds(60*60*2)));
        //更新当前题目和上一题的题目信息
    }

}

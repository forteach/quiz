package com.forteach.quiz.interaction.execute.service.SingleQue;

import com.alibaba.fastjson.JSON;
import com.forteach.quiz.common.DataUtil;
import com.forteach.quiz.interaction.execute.service.Key.SingleQueKey;
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
    public SendQuestService(ReactiveStringRedisTemplate stringRedisTemplate,
                            ReactiveHashOperations<String, String, String> reactiveHashOperations,
                            InteractRecordQuestionsService interactRecordQuestionsService,
                            BigQuestionRepository bigQuestionRepository) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.bigQuestionRepository=bigQuestionRepository;
        this.interactRecordQuestionsService = interactRecordQuestionsService;
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

        //创建课堂提问的题目2小时过期
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
//                .flatMap(r-> StrUtil.isBlank(selected)?Mono.just(true):interactRecordExecuteService.releaseQuestion(circleId, questId, selected, category, interactive));
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
    private Mono<Boolean> addQuestNowInfo(final String circleId,final String teacherId,final String questId,String questionType, final String interactive,final String category,final String selected,final String cut){
       //TODO 需要调整final
        HashMap<String, String> map = new HashMap<>(10);
        map.put("circleId",circleId);//当前课堂ID
        map.put("teacherId",teacherId);//当前课堂教师ID
        map.put("questionType", questionType);//题目类型
        map.put("questionId", questId);//题目编号
        map.put("interactive", interactive);  //互动方式（举手、抢答等）
        map.put("category", category);//选取类别（个人、小组）
        map.put("selected", selected);//选中人员 [逗号 分割]
        map.put("noRreceiveSelected", selected);//选中未确认收到推送题目标记的人员 [逗号 分割]
        map.put("cut", cut);//随机数
        map.put("time", DataUtil.format(new Date()));//创建时间
        //创建课堂提问的题目2小时过期
       return reactiveHashOperations.putAll(SingleQueKey.questionsIdNow(circleId), map)
                //设置题目信息
               .flatMap(r->setQuestInfo(questId))
               .filterWhen(r->stringRedisTemplate.expire(SingleQueKey.questionsIdNow(circleId), Duration.ofSeconds(60*60*2)));

    }

    /**
     * 设置当前题目内容到Redis
     * @param questionId
     * @return
     */
    private Mono<Boolean> setQuestInfo(final String questionId){
        //存储当前所发布的题目信息
       final String key=SingleQueKey.questionsNow(questionId);
       return stringRedisTemplate.hasKey(key)
               .flatMap(r->r?Mono.just(true):bigQuestionRepository.findById(questionId)
                       .flatMap(obj-> stringRedisTemplate.opsForValue().set(key,JSON.toJSONString(obj),Duration.ofSeconds(60*60*2))));
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
        return stringRedisTemplate.opsForList().leftPush(SingleQueKey.askTypeQuestionsId(QuestionType.TiWen.name(),  circleId,  interactive), questId)
                //创建交互题目发布哈希列表
                .flatMap(l->stringRedisTemplate.opsForSet().add(SingleQueKey.askTypeQuestionsId(QuestionType.TiWen.name(),  circleId),questId)
                        .filterWhen(l1->stringRedisTemplate.expire(SingleQueKey.askTypeQuestionsId(QuestionType.TiWen.name(),  circleId), Duration.ofSeconds(60*60*2))))
                .flatMap(item -> {
//                    //获得当前题目ID和创建新的发布题目Key=题目ID
                            return  stringRedisTemplate.hasKey(SingleQueKey.askTypeQuestionsIdNow(QuestionType.TiWen.name(), circleId,  interactive))
                                    .flatMap(r->{
                                        if(!r){
                                            //如果该键值不存在，就创建键值
                                           return stringRedisTemplate.opsForValue().set(SingleQueKey.askTypeQuestionsIdNow(QuestionType.TiWen.name(), circleId,  interactive),questId,Duration.ofSeconds(60 * 60 * 2));
                                        }else{
                                            //获得当前键值，并设置新的键值
                                          return  stringRedisTemplate.opsForValue().getAndSet(SingleQueKey.askTypeQuestionsIdNow(QuestionType.TiWen.name(), circleId,  interactive),questId)
                                                    .flatMap(old->stringRedisTemplate.opsForValue().set(SingleQueKey.askTypeQuestionsIdPrve(QuestionType.TiWen.name(), circleId,interactive),old,Duration.ofSeconds(60 * 60 * 2)));
                                        }
                                    });
                        }
                )
                //设置题目列表的过期时间
                .filterWhen(ok->stringRedisTemplate.expire(SingleQueKey.askTypeQuestionsId(QuestionType.TiWen.name(), circleId,  interactive), Duration.ofSeconds(60*60*2)));
        //更新当前题目和上一题的题目信息
    }

}
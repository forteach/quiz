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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;

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
    public Mono<Boolean> sendQuestion(String circleId,String teacherId,String questionType,String questId, String interactive,String category,String selected,String cut) {

        //创建课堂提问的题目36分钟过期
        Mono<Boolean> addQuestNow = addQuestNowInfo(circleId,teacherId,questionType,questId,interactive,category,selected,cut);

        //创建课堂问题列表记录
        Mono<Boolean> createQuest = addQuestList( circleId, interactive, questId);

//        //创建提问题目,并保存题目回答的学生(学生编号逗号分隔)
//        Mono<Boolean> createQuID =stringRedisTemplate.opsForValue()
//                .set(BigQueKey.askTypeQuestionsId(QuestionType.TiWen, giveVo,giveVo.getQuestionId()),giveVo.getSelected(), Duration.ofSeconds(60 * 60 * 2))
//                .flatMap(item -> MyAssert.isFalse(item, DefineCode.ERR0013, "redis操作错误"))
//                .filterWhen(ok->stringRedisTemplate.opsForValue()
//                        //记录当前题目的成员回答类型（个人、小组）和互动方式（选人，抢答）
//                        .set(BigQueKey.askTypeQuestionsIdType(giveVo.getCircleId(),giveVo.getQuestionId()),giveVo.getCategory().concat(",").concat(giveVo.getInteractive()), Duration.ofSeconds(60 * 60 * 2)));


//        //删除抢答答案（删除课堂提问的题目ID的回答信息）
//        Mono<Boolean> removeRace = stringRedisTemplate.opsForValue().delete(giveVo.getRaceAnswerFlag())
//                //删除信息没找到键值失败，同样返回TRUE
//                .flatMap(ok->Mono.just(true));

        //执行创建提问，并返回执行结果
        return Flux.concat(addQuestNow,createQuest)
                .count()
                //创建改题目的回答者信息
                .flatMap(ct-> interactRecordQuestionsService.releaseQuestion(circleId, questId, selected, category, interactive));
    }

    /**
     *设置当前发送题目基本信息
     * @param circleId   课堂编号
     * @param teacherId  课堂教师
     * @param questId    问题ID
     * @param questionType    问题ID
     * @param interactive  //互动方式（举手、抢答等）
     * @param category //选取类别（个人、小组）
     * @param selected //选中人员 [逗号 分割](stu01,sut02)
     * @param cut //随机数
     * @return true or false
     */
    private Mono<Boolean> addQuestNowInfo(final String circleId,final String teacherId,final String questId,String questionType, final String interactive,final String category,final String selected,final String cut){
        HashMap<String, String> map = new HashMap<>(8);
        map.put("circleId",circleId);//当前课堂ID
        map.put("teacherId",teacherId);//当前课堂教师ID
        map.put("questionType", questionType);//题目编号
        map.put("questionId", questId);//题目编号
        map.put("interactive", interactive);  //互动方式（举手、抢答等）
        map.put("category", category);//选取类别（个人、小组）
        map.put("selected", selected);//选中人员 [逗号 分割]
        map.put("cut", cut);//随机数
        map.put("time", DataUtil.format(new Date()));//创建时间
        //当前课堂ID
        map.put("circleId",circleId);
        //当前课堂教师ID
        map.put("teacherId",teacherId);
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
       return reactiveHashOperations.putAll(BigQueKey.QuestionsIdNow(circleId), map)
               .flatMap(r->setQuestInfo(questId))
               //key:circleId+"now"
               .filterWhen(r->stringRedisTemplate.expire(BigQueKey.QuestionsIdNow(circleId), Duration.ofSeconds(60*60*2)));

    }

    /**
     * 设置当前题目内容到Redis
     * @param questionId
     * @return
     */
    private Mono<Boolean> setQuestInfo(final String questionId){
        //存储当前所发布的题目信息
        final String key=BigQueKey.QuestionsNow(questionId);
       return stringRedisTemplate.hasKey(key)
               .flatMap(r->r.booleanValue()?Mono.just(true):bigQuestionRepository.findById(questionId).flatMap(obj-> stringRedisTemplate.opsForValue().set(key,JSON.toJSONString(obj),Duration.ofSeconds(60*60*2))));
    }

    /**
     * 添加当前发布题目辅助键信息
     * @param circleId
     * @param interactive
     * @param questId
     * @return
     */
    private Mono<Boolean> addQuestList(String circleId,String interactive,String questId){
        //创建交互题目的互动方式的先后顺序发布列表
        return stringRedisTemplate.opsForList().leftPush(BigQueKey.askTypeQuestionsId(QuestionType.TiWen.name(),  circleId,  interactive), questId)
                //创建交互题目发布列表
                .flatMap(l->stringRedisTemplate.opsForSet().add(BigQueKey.askTypeQuestionsId(QuestionType.TiWen.name(),  circleId))
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
                                    })
                                    .filterWhen(r->stringRedisTemplate.expire(BigQueKey.askTypeQuestionsIdNow(QuestionType.TiWen.name(), circleId,  interactive),Duration.ofSeconds(60 * 60 * 2)));
                        }
                )
                //设置题目列表的过期时间
                .filterWhen(ok->stringRedisTemplate.expire(BigQueKey.askTypeQuestionsId(QuestionType.TiWen.name(), circleId,  interactive), Duration.ofSeconds(60*60*2)));
        //更新当前题目和上一题的题目信息
    }

}

package com.forteach.quiz.interaction.execute.service;

import com.alibaba.fastjson.JSON;
import com.forteach.quiz.common.DataUtil;
import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.interaction.execute.config.BigQueKey;
import com.forteach.quiz.questionlibrary.domain.QuestionType;
import com.forteach.quiz.questionlibrary.repository.BigQuestionRepository;
import com.forteach.quiz.service.CorrectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
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
    private final InteractRecordExecuteService interactRecordExecuteService;
    private final CorrectService correctService;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final BigQuestionRepository bigQuestionRepository;

    public SendQuestService(ReactiveStringRedisTemplate stringRedisTemplate,
                                      ReactiveHashOperations<String, String, String> reactiveHashOperations,
                                      InteractRecordExecuteService interactRecordExecuteService,
                                      CorrectService correctService,
                                      BigQuestionRepository bigQuestionRepository,
                                      ReactiveMongoTemplate reactiveMongoTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.correctService = correctService;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.bigQuestionRepository=bigQuestionRepository;
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
    public Mono<Boolean> sendQuestion(String circleId,String teacherId,String questId, String interactive,String category,String selected,String cut) {

        //创建课堂提问的题目36分钟过期
        Mono<Boolean> addQuestNow = addQuestNowInfo(circleId,teacherId,questId,interactive,category,selected,cut);

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
                .flatMap(ct-> interactRecordExecuteService.releaseQuestion(circleId, questId, selected, category, interactive));
    }


    /**
     *设置当前发送题目基本信息
     * @param circleId   课堂编号
     * @param teacherId  课堂教师
     * @param questId    问题ID
     * @param interactive  //互动方式（举手、抢答等）
     * @param category //选取类别（个人、小组）
     * @param selected //选中人员 [逗号 分割](stu01,sut02)
     * @param cut //随机数
     * @return true or false
     */
    private Mono<Boolean> addQuestNowInfo(final String circleId,final String teacherId,final String questId, final String interactive,final String category,final String selected,final String cut){
        HashMap<String, String> map = new HashMap<>(8);
        map.put("circleId",circleId);//当前课堂ID
        map.put("teacherId",teacherId);//当前课堂教师ID
        map.put("questionId", questId);//题目编号
        map.put("interactive", interactive);  //互动方式（举手、抢答等）
        map.put("category", category);//选取类别（个人、小组）
        map.put("selected", selected);//选中人员 [逗号 分割]
        map.put("cut", cut);//随机数
        map.put("time", DataUtil.format(new Date()));//创建时间
        //创建课堂提问的题目2小时过期
       return reactiveHashOperations.putAll(BigQueKey.QuestionsIdNow(circleId), map)
               .flatMap(r->setQuestInfo(questId))
               //key:circleId+"now"
               .filterWhen(r->stringRedisTemplate.expire(BigQueKey.QuestionsIdNow(circleId), Duration.ofSeconds(60*60*2)));

    }

    //设置当前题目内容到Redis
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
        return stringRedisTemplate.opsForList().leftPush(BigQueKey.askTypeQuestionsId(QuestionType.TiWen,  circleId,  interactive), questId)
                .flatMap(item -> {
//                    //获得当前题目ID和创建新的发布题目Key=题目ID
                            return  stringRedisTemplate.hasKey(BigQueKey.askTypeQuestionsIdNow(QuestionType.TiWen, circleId,  interactive))
                                    .flatMap(r->{
                                        if(!r.booleanValue()){
//                                            System.out.println("00000000000000000000000000000");
                                            //如果该键值不存在，就创建键值
                                           return stringRedisTemplate.opsForValue().set(BigQueKey.askTypeQuestionsIdNow(QuestionType.TiWen, circleId,  interactive),questId,Duration.ofSeconds(60 * 60 * 2));
                                        }else{
                                            //获得当前键值，并设置新的键值
                                          return  stringRedisTemplate.opsForValue().getAndSet(BigQueKey.askTypeQuestionsIdNow(QuestionType.TiWen, circleId,  interactive),questId)
                                                    .flatMap(old->stringRedisTemplate.opsForValue().set(BigQueKey.askTypeQuestionsIdPrve(QuestionType.TiWen, circleId,interactive),old,Duration.ofSeconds(60 * 60 * 2)));
                                        }
                                    })
                                    .filterWhen(r->stringRedisTemplate.expire(BigQueKey.askTypeQuestionsIdNow(QuestionType.TiWen, circleId,  interactive),Duration.ofSeconds(60 * 60 * 2)));
                        }
                )
                //设置题目列表的过期时间
                .filterWhen(ok->stringRedisTemplate.expire(BigQueKey.askTypeQuestionsId(QuestionType.TiWen, circleId,  interactive), Duration.ofSeconds(60*60*2)));
        //更新当前题目和上一题的题目信息
    }
}

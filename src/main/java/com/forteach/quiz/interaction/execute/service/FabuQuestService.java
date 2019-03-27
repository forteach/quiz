package com.forteach.quiz.interaction.execute.service;

import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.interaction.execute.config.BigQueKey;
import com.forteach.quiz.questionlibrary.repository.BigQuestionRepository;
import com.forteach.quiz.service.CorrectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.joining;

/**
 * 当前课堂已发布的题目列表
 */

@Slf4j
@Service
public class FabuQuestService {

    private final ReactiveStringRedisTemplate stringRedisTemplate;
    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;
    private final InteractRecordExecuteService interactRecordExecuteService;
    private final CorrectService correctService;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final BigQuestionRepository bigQuestionRepository;

    public FabuQuestService(ReactiveStringRedisTemplate stringRedisTemplate,
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
     *获得当前题目活动列表
     * @param circleId   课堂编号
     * @return
     */
    public Mono<List<Object>> getFaBuQuestNow(final String circleId) {
        //获得已经发布的题目列表
        Mono<List<String>> faBuList= reactiveHashOperations.get((BigQueKey.questionsIdNow(circleId)),"questionType")
                .flatMap(qtype->stringRedisTemplate.opsForSet().members(BigQueKey.askTypeQuestionsId(qtype,  circleId))
                        .collectList());
        //获得当前题目的ID
        Mono<String> nowQuest=reactiveHashOperations.get(BigQueKey.questionsIdNow(circleId),"questionId");

        return  Flux.concat(faBuList,nowQuest).collectList();
    }

    /**
     * 当前题目回答情况，删除推送选人回答的学生ID
     * @param stuId
     * @return
     */
    public Mono<Boolean> delSelectStuId(final String stuId,final String circleId){

        final String key=BigQueKey.questionsIdNow(circleId);//获得当前逗号分隔的选人数据
        final Mono<Boolean> isSelelcted=reactiveHashOperations.hasKey(key,"selected");
        return  isSelelcted
                .flatMap(k->{
                    if(k.booleanValue()){
                      return  reactiveHashOperations.get(key,"selected")
                                //过滤掉当前的已回答的学生，并从新生成字符串数据
                                .flatMap(str-> Mono.just(getSelected(str,stuId)))
                                .flatMap(r->reactiveHashOperations.put(key,"selected",r));
                    }else{
                      return  MyAssert.isFalse(false, DefineCode.ERR0014,"未找到当前课堂选人的缓存数据,或许是课堂数据过期");
                    }
                });

    }

    //筛选字符串中所选人员
    public String getSelected(String str,String filterStr){
        System.out.println(str);
       final String newStr = Arrays.stream(str.split(","))
                .filter(id->!id.equals(filterStr))
                .filter(Objects::nonNull)
                .map(str1->str1.concat(","))
                .collect(joining());
        System.out.println(newStr);
        return newStr;
    }

}

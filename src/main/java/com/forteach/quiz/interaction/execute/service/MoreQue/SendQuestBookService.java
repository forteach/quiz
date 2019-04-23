package com.forteach.quiz.interaction.execute.service.MoreQue;

import com.forteach.quiz.common.DataUtil;
import com.forteach.quiz.interaction.execute.service.Key.MoreQueKey;
import com.forteach.quiz.interaction.execute.service.record.InteractRecordExerciseBookService;
import com.forteach.quiz.questionlibrary.repository.BigQuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.*;


/**
 * 课堂发布练习册题目
 */
@Slf4j
@Service
public class SendQuestBookService {

    private final ReactiveStringRedisTemplate stringRedisTemplate;
    private final ReactiveMongoTemplate mongoTemplate;
    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;
    private final BigQuestionRepository bigQuestionRepository;
    private final InteractRecordExerciseBookService interactRecordExerciseBookService;

    public SendQuestBookService(ReactiveStringRedisTemplate stringRedisTemplate,
                                ReactiveMongoTemplate mongoTemplate,
                                ReactiveHashOperations<String, String, String> reactiveHashOperations,
                                InteractRecordExerciseBookService interactRecordExerciseBookService,
                                BigQuestionRepository bigQuestionRepository) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.mongoTemplate=mongoTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.bigQuestionRepository=bigQuestionRepository;
        this.interactRecordExerciseBookService = interactRecordExerciseBookService;
    }

    /**
     *
     * @param circleId   课堂编号
     * @param teacherId  课堂教师
     * @param questIds    练习册问题ID
     * @param category //选取类别（个人、小组）
     * @param selected //选中人员
     * @return
     */
    public Mono<List<Boolean>> sendQuestionBook(String circleId, String teacherId, String questionType, String questIds, String category, String selected) {

        //创建课堂练习册题目的题目2小时过期
       Mono<Boolean> addQuestBookNowMap = addQuestBookNow(circleId,teacherId,questIds,questionType,category,selected);

        //创建课堂问题列表记录
        Mono<Boolean> BookMap = createQuestBookMap( circleId, questIds);

        Mono<Boolean> BookList=createQuestBookList( circleId, questIds);

        //执行创建提问，并返回执行结果
        return    Flux.concat(addQuestBookNowMap,BookMap,BookList).collectList();
    }


    /**
     *设置当前发送题目基本信息
     * @param circleId   课堂编号
     * @param teacherId  课堂教师
     * @param questionType    问题类型  提问、任务
     * @param category //选取类别（个人、小组）
     * @param selected //选中人员 [逗号 分割](stu01,sut02)
     * @return true or false
     */
    private Mono<Boolean> addQuestBookNow(final String circleId,final String teacherId,final String questIds,String questionType, final String category,final String selected){
        HashMap<String, String> book = new HashMap<>(8);
        //当前课堂ID
        book.put("circleId",circleId);
        //当前课堂教师ID
        book.put("teacherId",teacherId);
        //题目类型
        book.put("questionType", questionType);
        //练习册题目编号（逗号分隔）
        book.put("questionId", questIds);
        //选取类别（个人、小组）
        book.put("category", category);
        //选中人员 [逗号 分割]
        book.put("selected", selected);
        //题目数量
        book.put("questionCount",String.valueOf(questIds.split(",").length));
        //创建时间
        book.put("time", DataUtil.format(new Date()));
        //创建课堂练习册的题目2小时过期
       return  reactiveHashOperations.putAll(MoreQueKey.questionsBookNow(circleId), book)
               .filterWhen(r->stringRedisTemplate.expire(MoreQueKey.questionsBookNow(circleId), Duration.ofSeconds(60*60*2)));
    }

    /**
     * 创建练习册列表
     * @param circleId
     * @param questionIds
     * @return
     */
    private  Mono<Boolean>  createQuestBookMap(String circleId,String questionIds){

        HashMap<String, String> questInfo = new HashMap<>();
        Arrays.stream(questionIds.split(",")).forEach(qid-> questInfo.put(qid,""));
        return reactiveHashOperations.putAll(MoreQueKey.bookQuestionMap(circleId), questInfo)
                .filterWhen(r->stringRedisTemplate.expire(MoreQueKey.bookQuestionMap(circleId), Duration.ofSeconds(60*60*2)));
    }

    /**
     * 创建练习测题目
     * @param circleId
     * @param questionIds
     * @return
     */
    private Mono<Boolean> createQuestBookList(String circleId,String questionIds){
        List<String> strs =Arrays.asList(questionIds.split(","));
        return stringRedisTemplate.opsForList().rightPushAll(MoreQueKey.bookTypeQuestionsList("",circleId),strs)
                .flatMap(ok->Mono.just(true))
                 .filterWhen(ok->stringRedisTemplate.expire(MoreQueKey.bookTypeQuestionsList("",circleId), Duration.ofSeconds(60*60*2)));
    }

}

package com.forteach.quiz.interaction.execute.service;

import com.alibaba.fastjson.JSON;
import com.forteach.quiz.common.DataUtil;
import com.forteach.quiz.interaction.execute.config.BigQueKey;
import com.forteach.quiz.interaction.execute.service.record.InteractRecordExerciseBookService;
import com.forteach.quiz.questionlibrary.repository.BigQuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课堂发布练习册题目
 */
@Slf4j
@Service
public class SendQuestBookService {

    private final ReactiveStringRedisTemplate stringRedisTemplate;
    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;
    private final BigQuestionRepository bigQuestionRepository;
    private final InteractRecordExerciseBookService interactRecordExerciseBookService;

    public SendQuestBookService(ReactiveStringRedisTemplate stringRedisTemplate,
                                ReactiveHashOperations<String, String, String> reactiveHashOperations,
                                InteractRecordExerciseBookService interactRecordExerciseBookService,
                                BigQuestionRepository bigQuestionRepository) {
        this.stringRedisTemplate = stringRedisTemplate;
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
    public Mono<Boolean> sendQuestionBook(String circleId, String teacherId, String questionType, String questIds, String category, String selected) {

        //创建课堂练习册题目的题目2小时过期
        Mono<Boolean> addQuestBookNowMap = addQuestBookNow(circleId,teacherId,questIds,questionType,category,selected);

        //创建课堂问题列表记录
        Mono<Boolean> createQuestBookList = createQuestBook( circleId, questIds);

        //执行创建提问，并返回执行结果
        return addQuestBookNowMap.map(r->createQuestBookList)
                //  TODO 创建mongo答题日志???
                .flatMap(r->interactRecordExerciseBookService.interactiveBook(circleId, questIds, selected, category));
//                .flatMap(r->Mono.just(true));
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
        book.put("circleId",circleId);//当前课堂ID
        book.put("teacherId",teacherId);//当前课堂教师ID
        book.put("questionType", questionType);//题目类型
        book.put("questionId", questIds);//练习册题目编号（逗号分隔）
        book.put("category", category);//选取类别（个人、小组）
        book.put("selected", selected);//选中人员 [逗号 分割]
        book.put("questionCount",String.valueOf(questIds.split(",").length));//题目数量
        book.put("time", DataUtil.format(new Date()));//创建时间
        //创建课堂练习册的题目2小时过期
       return reactiveHashOperations.putAll(BigQueKey.questionsBookNow(circleId), book)
                //设置题目信息
               .map(r->setQuestInfo(questIds))
               //key:circleId+"now"
               .flatMap(r->stringRedisTemplate.expire(BigQueKey.questionsBookNow(circleId), Duration.ofSeconds(60*60*2)));

    }

    /**
     * 设置练习册题目内容到Redis
     * @param questionIds
     * @return
     */
    private List<Mono<Boolean>> setQuestInfo(final String questionIds){
        return Arrays.asList(questionIds.split(","))
                //根据练习册题目ID，获得题目内容
                .stream().map(bigQuestionRepository::findById)
                //设置题目内容
                .map(mobj->mobj.flatMap(obj-> stringRedisTemplate.opsForValue().set(BigQueKey.bookQuestionsNow(obj.getId()),JSON.toJSONString(obj),Duration.ofSeconds(60*60*2))))
                .collect(Collectors.toList());
    }

    /**
     * 添加当前发布题目辅助键信息
     * @param circleId
     * @param questId
     * @return
     */
    private Mono<Boolean> createQuestBook(String circleId,String questId){
        //创建交互题目的互动方式的先后顺序发布列表
        return stringRedisTemplate.opsForList().leftPush(BigQueKey.bookTypeQuestionsList(circleId), questId)
                //设置题目列表的过期时间
                .flatMap(ok->stringRedisTemplate.expire(BigQueKey.bookTypeQuestionsList(circleId), Duration.ofSeconds(60*60*2)));
        //更新当前题目和上一题的题目信息
    }

}

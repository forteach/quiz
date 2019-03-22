package com.forteach.quiz.interaction.execute.service;

import com.forteach.quiz.common.DataUtil;
import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.exceptions.AskException;
import com.forteach.quiz.exceptions.ExamQuestionsException;
import com.forteach.quiz.interaction.execute.config.BigQueKey;
import com.forteach.quiz.interaction.execute.domain.AskAnswer;
import com.forteach.quiz.interaction.execute.service.record.InsertInteractRecordService;
import com.forteach.quiz.questionlibrary.domain.QuestionType;
import com.forteach.quiz.service.CorrectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import static com.forteach.quiz.common.Dic.*;

@Slf4j
@Service
public class SendAnswerService {

    private final ReactiveStringRedisTemplate stringRedisTemplate;
    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;
    private final CorrectService correctService;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final InsertInteractRecordService insertInteractRecordService;

    public SendAnswerService(ReactiveStringRedisTemplate stringRedisTemplate,
                             ReactiveHashOperations<String, String, String> reactiveHashOperations,
                             CorrectService correctService,
                             InsertInteractRecordService insertInteractRecordService,
                             ReactiveMongoTemplate reactiveMongoTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.correctService = correctService;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.insertInteractRecordService = insertInteractRecordService;
    }


    /**
     * 学生问题回答
     * @param circleId 课堂ID
     * @param examineeId  学生ID
     * @param questId 问题ID
     * @param answer 回答内容
     * @param cut 随机数
     * @return
     */
    public Mono<Boolean> sendAnswer(String circleId,String examineeId,String questId,String answer,String cut) {

        return Mono.just(answer)
                //验证当前回答的题目和参与回答的人员
                .transform(an->filterSelectVerify(circleId,examineeId,questId))
                .flatMap(typeName -> {
                        switch (typeName) {
                            //抢答
                            case ASK_INTERACTIVE_RACE:
                                return sendRace(circleId,examineeId,questId,answer, ASK_INTERACTIVE_SELECT);
                            case ASK_INTERACTIVE_RAISE:
                                return sendRaise(circleId,examineeId,questId,answer, ASK_INTERACTIVE_SELECT);
                            case ASK_INTERACTIVE_SELECT:
                                //选人回答问题
                                return sendSelect(circleId,examineeId,questId,answer, ASK_INTERACTIVE_SELECT);
                            case ASK_INTERACTIVE_VOTE:
                                return Mono.empty();
                            default:
                                throw new ExamQuestionsException("非法参数 错误的数据类型");
                        }

                })
                //设置学生回答题目答案
                .filterWhen(right -> reactiveHashOperations.put(BigQueKey.answerTypeQuestionsId(circleId,questId,QuestionType.TiWen.name()),examineeId,answer)
                                     .flatMap(r-> {
                                         //设置已经回答的学生列表
                                                //移除该学生本题回答历史记录
                                                 return stringRedisTemplate.opsForList().remove(BigQueKey.answerTypeQuestStuList(circleId, questId, QuestionType.TiWen.name()),0, examineeId)
                                                         //将答题信息添加至列表尾部
                                                         .flatMap(r1-> stringRedisTemplate.opsForList().rightPush(BigQueKey.answerTypeQuestStuList(circleId, questId, QuestionType.TiWen.name()), examineeId)
                                                                 .flatMap(r2-> Mono.just(true))
                                                                         .filterWhen(r2->stringRedisTemplate.expire(BigQueKey.answerTypeQuestStuList(circleId,questId,QuestionType.TiWen.name()), Duration.ofSeconds(60*60*2)))
                                                         )
                                                         //记录该题目的提交记录
                                             .filterWhen(r1->stringRedisTemplate.opsForSet().add(BigQueKey.tiJiaoanswerTypeQuestStuSet(circleId, questId, QuestionType.TiWen.name()),examineeId)
                                                     .flatMap(r11->stringRedisTemplate.expire(BigQueKey.tiJiaoanswerTypeQuestStuSet(circleId, questId, QuestionType.TiWen.name()), Duration.ofSeconds(60*60*2))));
    }
                                             )
                                    .filterWhen(ok->stringRedisTemplate.expire(BigQueKey.answerTypeQuestionsId(circleId,questId,QuestionType.TiWen.name()), Duration.ofSeconds(60*60*2)))
                )
                //设置学生回答题目的批改结果
                .filterWhen(right -> { return reactiveHashOperations.put(BigQueKey.piGaiTypeQuestionsId(circleId,questId,QuestionType.TiWen.name()),examineeId,String.valueOf(2))
                                    .flatMap(ok->stringRedisTemplate.expire(BigQueKey.piGaiTypeQuestionsId(circleId,questId,QuestionType.TiWen.name()), Duration.ofSeconds(60*60*2)))
                    ;})
                //记录学生回答MONGO记录
               .filterWhen(right -> insertInteractRecordService.answer(circleId, questId, examineeId, answer, String.valueOf(right)));
    }

    /**
     * 获得课堂当前题目的互动类型，验证题目id和参与人员
     * @param circleId
     * @param examineeId
     * @param questId
     * @return
     */

    private Mono<String> filterSelectVerify(String circleId,String examineeId, final String questId ) {
        //获得题目互动类型方式
        return  reactiveHashOperations.get(BigQueKey.QuestionsIdNow(circleId), "interactive")
                //获得题目当前id与所回答的题目比对
                .filterWhen(r->reactiveHashOperations.get(BigQueKey.QuestionsIdNow(circleId), "questionId").flatMap(qid-> MyAssert.isFalse(questId.equals(qid),DefineCode.ERR0002,"题目信息错误")))
                //回答学生是否在所选范围内
                .filterWhen(r->reactiveHashOperations.get(BigQueKey.QuestionsIdNow(circleId), "selected").flatMap(sid-> MyAssert.isFalse( isSelected(sid, examineeId),DefineCode.ERR0002,"未选择该名学生回答")));

    }


    /**
     * 判断学生是否被选中
     *
     * @return
     */
    private Boolean isSelected(final String selectId, final String examineeId) {
        return Arrays.asList(selectId.split(",")).contains(examineeId);
    }

    /**
     * 选人 回答
     *
     * @return
     */
    private Mono<Boolean> sendSelect(String circleId,String examineeId,String questId,String answer, final String type) {

        //创建学生回答顺序列表

        //TODO 发布题目答案对比 需要改成Redis，现在未改动
        return correctService.correcting(questId, answer)
                .flatMap(f -> {
                    //查找学生需要回答的题目
                    Query query = Query.query(
                            Criteria.where("circleId").is(circleId)
                                    .and("questionId").is(questId)
                                    .and("examineeId").is(examineeId));
                    //更新题目答案
                    Update update = Update.update("answer", answer)
                    .set("interactive", type)
                    .set("right", String.valueOf(f))
                    .set("uDate", DataUtil.format(new Date()));

                    return reactiveMongoTemplate.upsert(query, update, AskAnswer.class).flatMap(result -> {
                        if (result.wasAcknowledged()) {
                            return Mono.just(f);
                        } else {
                            return Mono.error(new AskException("Mongo操作失败"));
                        }
                    });
                });
    }


    /**
     * 抢答（不能重复回答，只能提交一次答题结果）
     * TODO 报错临时注解
     * @return
     */
    private Mono<Boolean> sendRace(String circleId,String examineeId,String questId,String answer, final String type) {
        //判断本次是否已经提交过该题目
        return  stringRedisTemplate.hasKey(BigQueKey.tiJiaoanswerTypeQuestStuSet(circleId, questId, QuestionType.TiWen.name()))
                .flatMap(flag -> {
                    MyAssert.isTrue(flag.booleanValue(),DefineCode.ERR0011,"该学生已提交，将不能再提交");
                    return sendSelect(circleId,examineeId,questId,answer, type);
                });
    }

    /**
     * TODO 报错临时注解
     * 举手 回答
     */
    private Mono<Boolean> sendRaise(String circleId,String examineeId,String questId,String answer, final String type) {
       return Mono.just(circleId).flatMap(interactAnswerVo -> sendSelect(circleId,examineeId,questId,answer, type));

    }
}

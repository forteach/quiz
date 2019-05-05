package com.forteach.quiz.interaction.execute.service.SingleQue;

import com.forteach.quiz.common.DataUtil;
import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.exceptions.AskException;
import com.forteach.quiz.exceptions.ExamQuestionsException;
import com.forteach.quiz.interaction.execute.domain.AskAnswer;
import com.forteach.quiz.interaction.execute.service.Key.AchieveAnswerKey;
import com.forteach.quiz.interaction.execute.service.Key.SingleQueKey;
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
    public Mono<Boolean> sendAnswer(final String circleId,final String examineeId,final String questId,final String answer,final String cut) {

        return Mono.just(answer)
                //验证当前回答的题目和参与回答的人员
                .transform(an->filterSelectVerify(circleId,examineeId,questId))
                .flatMap(typeName -> {
                        switch (typeName) {
                            //抢答
                            case ASK_INTERACTIVE_RACE:
                                return sendRace(circleId,examineeId,questId,answer, ASK_INTERACTIVE_RACE);
                                //举手
                            case ASK_INTERACTIVE_RAISE:
                                return sendRaise(circleId,examineeId,questId,answer, ASK_INTERACTIVE_RAISE);
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
                .filterWhen(right -> reactiveHashOperations.put(AchieveAnswerKey.answerTypeQuestionsId(circleId,questId,QuestionType.TiWen.name()),examineeId,answer)
                                     .flatMap(r-> {
                                         //设置已经回答的学生列表
                                                //移除该学生本题回答历史记录
                                                 return stringRedisTemplate.opsForList().remove(AchieveAnswerKey.answerTypeQuestStuList(circleId, questId, QuestionType.TiWen.name()),0, examineeId)
                                                         //将答题信息添加至列表尾部
                                                         .flatMap(r1-> stringRedisTemplate.opsForList().rightPush(AchieveAnswerKey.answerTypeQuestStuList(circleId, questId, QuestionType.TiWen.name()), examineeId)
                                                                 .flatMap(r2-> Mono.just(true))
                                                                         .filterWhen(r2->stringRedisTemplate.expire(AchieveAnswerKey.answerTypeQuestStuList(circleId,questId,QuestionType.TiWen.name()), Duration.ofSeconds(60*60*2)))
                                                         )
                                                         //记录该题目的提交记录
                                             .filterWhen(r1->stringRedisTemplate.opsForSet().add(AchieveAnswerKey.tiJiaoanswerTypeQuestStuSet(circleId, questId, QuestionType.TiWen.name()),examineeId)
                                                     .flatMap(r11->stringRedisTemplate.expire(AchieveAnswerKey.tiJiaoanswerTypeQuestStuSet(circleId, questId, QuestionType.TiWen.name()), Duration.ofSeconds(60*60*2))));
    }
                                             )
                                    .filterWhen(ok->stringRedisTemplate.expire(AchieveAnswerKey.answerTypeQuestionsId(circleId,questId,QuestionType.TiWen.name()), Duration.ofSeconds(60*60*2)))
                )
                //设置学生回答题目的批改结果
                .filterWhen(right ->  reactiveHashOperations.put(AchieveAnswerKey.piGaiTypeQuestionsId(circleId,questId,QuestionType.TiWen.name()),examineeId,String.valueOf(right))
                                    .flatMap(ok->stringRedisTemplate.expire(AchieveAnswerKey.piGaiTypeQuestionsId(circleId,questId,QuestionType.TiWen.name()), Duration.ofSeconds(60*60*2)))
                    );
//                //记录学生回答MONGO记录
//               .filterWhen(right -> insertInteractRecordService.answer(circleId,QuestionType.TiWen.name(), questId, examineeId, answer, right)
//                       .flatMap(f -> MyAssert.isFalse(f, DefineCode.ERR0012, "保存mongodb记录失败")));
    }

    /**
     * 获得课堂当前题目的互动类型，验证题目id和参与人员
     * @param circleId
     * @param examineeId
     * @param questId
     * @return
     */
    private Mono<String> filterSelectVerify(final String circleId,final String examineeId, final String questId ) {
        //获得题目互动类型方式
        return  reactiveHashOperations.get(SingleQueKey.questionsIdNow(circleId), "interactive")
                //获得题目当前id与所回答的题目比对
                .filterWhen(r->reactiveHashOperations.get(SingleQueKey.questionsIdNow(circleId), "questionId")
                        .flatMap(qid-> MyAssert.isFalse(questId.equals(qid),DefineCode.ERR0002,"练习册题目信息不存在")))
                //回答学生是否在所选范围内
                .filterWhen(r->reactiveHashOperations.get(SingleQueKey.questionsIdNow(circleId), "selected")
                        .flatMap(sid-> MyAssert.isFalse( isSelected(sid, examineeId),DefineCode.ERR0002,"未选择该学生回答")));

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
    private Mono<Boolean> sendSelect(final String circleId,final String examineeId,final String questId,final String answer, final String type) {

        //创建学生回答顺序列表

        //TODO 发布题目答案对比 需要改成Redis，现在未改动
        return correctService.correcting(SingleQueKey.questionsNow(questId),questId, answer)
                .filterWhen(r->cleanAnswerHasJoinStu(circleId,examineeId,questId));
    }

    private Mono<Boolean> cleanAnswerHasJoinStu(final String circleId,final String examineeId,final String questId){
       String key= AchieveAnswerKey.cleanAnswerHasJoin(circleId,questId);
       String[] strarrays = new String[]{examineeId};
       return stringRedisTemplate.opsForSet().remove(key,strarrays)
               .flatMap(r-> {
                   System.out.println("r-----------" + r);
                   return Mono.just(true);
               });
    }


    /**
     * 抢答（不能重复回答，只能提交一次答题结果）
     * TODO 报错临时注解
     * @return
     */
    private Mono<Boolean> sendRace(final String circleId,final String examineeId,final String questId,final String answer, final String type) {
        //判断本次是否已经提交过该题目
        return  stringRedisTemplate.opsForSet().isMember(AchieveAnswerKey.tiJiaoanswerTypeQuestStuSet(circleId, questId, QuestionType.TiWen.name()),examineeId)
                .flatMap(flag -> {
                    MyAssert.isTrue(flag.booleanValue(),DefineCode.ERR0011,"该学生已提交，将不能再提交");
                    return sendSelect(circleId,examineeId,questId,answer, type);
                });
    }

    /**
     * TODO 报错临时注解
     * 举手 回答
     */
    private Mono<Boolean> sendRaise(final String circleId,final String examineeId,final String questId,final String answer, final String type) {
       return Mono.just(circleId).flatMap(interactAnswerVo -> sendSelect(circleId,examineeId,questId,answer, type));

    }
}

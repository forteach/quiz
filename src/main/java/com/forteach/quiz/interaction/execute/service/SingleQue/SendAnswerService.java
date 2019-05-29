package com.forteach.quiz.interaction.execute.service.SingleQue;

import com.alibaba.fastjson.JSON;
import com.forteach.quiz.common.DataUtil;
import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.exceptions.ExamQuestionsException;
import com.forteach.quiz.interaction.execute.domain.AskAnswer;
import com.forteach.quiz.interaction.execute.service.Key.AchieveAnswerKey;
import com.forteach.quiz.interaction.execute.service.Key.SingleQueKey;
import com.forteach.quiz.interaction.execute.web.vo.DataDatumVo;
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
import java.util.List;


@Slf4j
@Service
public class SendAnswerService {

    private final ReactiveMongoTemplate mongoTemplate;
    private final ReactiveStringRedisTemplate stringRedisTemplate;
    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;
    private final CorrectService correctService;

    public SendAnswerService(ReactiveStringRedisTemplate stringRedisTemplate,
                             ReactiveMongoTemplate mongoTemplate,
                             ReactiveHashOperations<String, String, String> reactiveHashOperations,
                             CorrectService correctService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.mongoTemplate = mongoTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.correctService = correctService;
    }


    /**
     * 学生问题回答
     * @param circleId 课堂ID
     * @param examineeId  学生ID
     * @param questId 问题ID
     * @param answer 回答内容
     * @param questionType  提问、练习、任务
     * @return
     */
    public Mono<Boolean> sendAnswer(final String circleId,final String examineeId,final String questId,final String answer,final String questionType,final List<DataDatumVo> fileList) {

        return Mono.just(answer)
                //验证当前回答的题目和参与回答的人员
                .transform(an->filterSelectVerify(circleId,examineeId,questId))
                .flatMap(typeName -> {
                        switch (typeName) {
                            //抢答
                            case AchieveAnswerKey.ASK_INTERACTIVE_RACE:
                                return sendRace(circleId,examineeId,questId,answer,questionType);
                                //举手
                            case AchieveAnswerKey.ASK_INTERACTIVE_RAISE:
                                return sendRaise(circleId,examineeId,questId,answer,questionType);
                            case AchieveAnswerKey.ASK_INTERACTIVE_SELECT:
                                //选人回答问题
                                return sendSelect(circleId,examineeId,questId,answer,questionType);
                            case AchieveAnswerKey.ASK_INTERACTIVE_VOTE:
                                return Mono.empty();
                            default:
                                throw new ExamQuestionsException("非法参数 错误的数据类型");
                        }

                })
                //设置学生回答题目答案
                .filterWhen(right -> reactiveHashOperations.put(AchieveAnswerKey.answerTypeQuestionsId(circleId,questId,questionType),examineeId,answer)
                                      //添加回答信息的附件内容
                                     .flatMap(r->addAnswerFiles(circleId,examineeId,questId,questionType,fileList))
                                     .flatMap(r-> {
                                         //设置已经回答的学生列表
                                                //移除该学生本题回答历史记录
                                                 return stringRedisTemplate.opsForList().remove(AchieveAnswerKey.answerTypeQuestStuList(circleId, questId, questionType),0, examineeId)
                                                         //将答题信息添加至列表尾部
                                                         .flatMap(r1-> stringRedisTemplate.opsForList().rightPush(AchieveAnswerKey.answerTypeQuestStuList(circleId, questId, questionType), examineeId)
                                                                 .flatMap(r2-> Mono.just(true))
                                                                         .filterWhen(r2->stringRedisTemplate.expire(AchieveAnswerKey.answerTypeQuestStuList(circleId,questId,questionType), Duration.ofSeconds(60*60*2)))
                                                         )
                                                         //记录该题目的提交记录
                                             .filterWhen(r1->stringRedisTemplate.opsForSet().add(AchieveAnswerKey.tiJiaoanswerTypeQuestStuSet(circleId, questId,questionType),examineeId)
                                                     .flatMap(r11->stringRedisTemplate.expire(AchieveAnswerKey.tiJiaoanswerTypeQuestStuSet(circleId, questId, questionType), Duration.ofSeconds(60*60*2))));
    }
                                             )
                                    .filterWhen(ok->stringRedisTemplate.expire(AchieveAnswerKey.answerTypeQuestionsId(circleId,questId,questionType), Duration.ofSeconds(60*60*2)))
                )
                //设置学生回答题目的批改结果
                .filterWhen(right ->  reactiveHashOperations.put(AchieveAnswerKey.piGaiTypeQuestionsId(circleId,questId,questionType),examineeId,String.valueOf(right))
                                    .flatMap(ok->stringRedisTemplate.expire(AchieveAnswerKey.piGaiTypeQuestionsId(circleId,questId,questionType), Duration.ofSeconds(60*60*2)))
                    )
                //记录学生回答MONGO记录
               .filterWhen(right -> answer(circleId,questionType, questId, examineeId, answer,fileList, right)
                       .flatMap(f -> MyAssert.isFalse(f, DefineCode.ERR0012, "保存mongodb记录失败")));
    }

    //添加redis题目回答的附件信息
    private Mono<Boolean> addAnswerFiles(final String circleId,final String examineeId,final String questId,final String questionType,final List<DataDatumVo> fileList){
        if((fileList!=null)&&(fileList.size()>0)){
            return Mono.just(JSON.toJSONString(fileList)).flatMap(jsonStr->
                        reactiveHashOperations.put(AchieveAnswerKey.answerFileTypeQuestionsId(circleId,questId,questionType),examineeId,jsonStr)
                                .filterWhen(ok->stringRedisTemplate.expire(AchieveAnswerKey.answerFileTypeQuestionsId(circleId,questId,questionType), Duration.ofSeconds(60*60*2)))
                    .flatMap(r->Mono.just(true)));
        }else{
            return Mono.just(true);
        }


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
                        .flatMap(qid-> MyAssert.isFalse(questId.equals(qid),DefineCode.ERR0002,"回答题目信息不相符")))
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
    private Mono<Boolean> sendSelect(final String circleId,final String examineeId,final String questId,final String answer,final String questionType) {

        //创建学生回答顺序列表

        return correctService.correcting(SingleQueKey.questionsNow(questId),questId, answer)
                //清除改题目已经回答推送的记录
                .filterWhen(r->cleanAnswerHasJoinStu(circleId,examineeId,questId,questionType));
    }

    private Mono<Boolean> cleanAnswerHasJoinStu(final String circleId,final String examineeId,final String questId,final String questionType){
       String key= AchieveAnswerKey.cleanAnswerHasJoin(circleId,questId,questionType);
       //从已经回答推送的列表移除
       String[] strarrays = new String[]{examineeId};
       return stringRedisTemplate.opsForSet().remove(key,strarrays)
               .flatMap(r->
//                   System.out.println("r-----------" + r);
                    Mono.just(true)
               );
    }


    /**
     * 抢答（不能重复回答，只能提交一次答题结果）
     * TODO 报错临时注解
     * @return
     */
    private Mono<Boolean> sendRace(final String circleId,final String examineeId,final String questId,final String answer, final String questionType) {
        //判断本次是否已经提交过该题目
        return  stringRedisTemplate.opsForSet().isMember(AchieveAnswerKey.tiJiaoanswerTypeQuestStuSet(circleId, questId, questionType),examineeId)
                .flatMap(flag -> {
                    MyAssert.isTrue(flag.booleanValue(),DefineCode.ERR0011,"该学生已提交，将不能再提交");
                    return sendSelect(circleId,examineeId,questId,answer,questionType);
                });
    }

    /**
     * TODO 报错临时注解
     * 举手 回答
     */
    private Mono<Boolean> sendRaise(final String circleId,final String examineeId,final String questId,final String answer, final String questionType) {
       return Mono.just(circleId).flatMap(interactAnswerVo -> sendSelect(circleId,examineeId,questId,answer,questionType));

    }

    /**
     * 学生回答问题时 加入记录
     *
     * @param circleId
     * @param questionId
     * @param studentId
     * @param answer
     * @param right
     * @return
     */
    public Mono<Boolean> answer(final String circleId, final String type, final String questionId, final String studentId, final String answer, final List<DataDatumVo> fileList,final Boolean right) {

        //查找学生需要回答的题目
        Query query = Query.query(
                Criteria.where("circleId").is(circleId)
                        .and("questionId").is(questionId)
                        .and("examineeId").is(studentId));
        //更新题目答案
        Update update = Update.update("answer", answer)
                .set("questionType", type)
                .set("right", String.valueOf(right))
                .set("fileList",fileList)
                .set("uDate", DataUtil.format(new Date()));

        return mongoTemplate.upsert(query, update, AskAnswer.class).flatMap(result -> Mono.just(result.wasAcknowledged()));
//);

    }
}

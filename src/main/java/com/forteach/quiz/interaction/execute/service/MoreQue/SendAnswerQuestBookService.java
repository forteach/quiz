package com.forteach.quiz.interaction.execute.service.MoreQue;

import com.alibaba.fastjson.JSON;
import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.interaction.execute.domain.ActivityAskAnswer;
import com.forteach.quiz.interaction.execute.service.Key.AchieveAnswerKey;
import com.forteach.quiz.interaction.execute.service.Key.MoreQueKey;
import com.forteach.quiz.interaction.execute.service.Key.SingleQueKey;
import com.forteach.quiz.interaction.execute.web.vo.DataDatumVo;
import com.forteach.quiz.interaction.execute.web.vo.InteractiveSheetAnsw;
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
import java.util.List;

@Slf4j
@Service
public class SendAnswerQuestBookService {

    private final ReactiveStringRedisTemplate stringRedisTemplate;
    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;
    private final CorrectService correctService;
    private final ReactiveMongoTemplate reactiveMongoTemplate;


    public SendAnswerQuestBookService(ReactiveStringRedisTemplate stringRedisTemplate,
                                      ReactiveHashOperations<String, String, String> reactiveHashOperations,
                                      CorrectService correctService,
                                      ReactiveMongoTemplate reactiveMongoTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.correctService = correctService;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }


    /**
     * 学生问题回答
     * @param circleId 课堂ID
     * @param examineeId  学生ID
     * @param questId 问题ID
     * @param answer 回答内容
     * @return
     */
    public Mono<Boolean> sendAnswer(String interactive,final String questionType,final String circleId,final String examineeId,final String questId,final String answer,final List<DataDatumVo> fileList) {

        return Mono.just(answer)
                //验证当前回答的题目和参与回答的人员
                .transform(an->filterSelectVerify(questionType,circleId,examineeId,questId))
                //学生回答问题
                .flatMap(typeName -> sendSelect(circleId,examineeId,questId,answer,questionType))
                //设置学生练习册回答题目答案
                .filterWhen(right -> reactiveHashOperations.put(AchieveAnswerKey.answerTypeQuestionsId(circleId,questId,questionType),examineeId,answer)
                                //添加回答信息的附件内容
                                     .flatMap(r->addAnswerFiles(circleId,examineeId,questId,questionType,fileList))
                                     .flatMap(r-> {
                                         //设置已经回答的学生列表
                                                //移除该学生本题回答历史记录
                                                 return stringRedisTemplate.opsForList().remove(AchieveAnswerKey.answerTypeQuestStuList(circleId, questId, questionType),0, examineeId)
                                                         //将答题学生ID添加至列表尾部
                                                         .flatMap(r1-> stringRedisTemplate.opsForList().rightPush(AchieveAnswerKey.answerTypeQuestStuList(circleId, questId, questionType), examineeId)
                                                                 .flatMap(r2-> Mono.just(true))
                                                                         .filterWhen(r2->stringRedisTemplate.expire(AchieveAnswerKey.answerTypeQuestStuList(circleId,questId,questionType), Duration.ofSeconds(60*60*2)))
                                                         )
                                                         //记录该题目的提交记录
                                             .filterWhen(r1->stringRedisTemplate.opsForSet().add(AchieveAnswerKey.tiJiaoanswerTypeQuestStuSet(circleId, questId, questionType),examineeId)
                                                     .flatMap(r11->stringRedisTemplate.expire(AchieveAnswerKey.tiJiaoanswerTypeQuestStuSet(circleId, questId, questionType), Duration.ofSeconds(60*60*2))));
    }
                                             )
                                    .filterWhen(ok->stringRedisTemplate.expire(AchieveAnswerKey.answerTypeQuestionsId(circleId,questId,questionType), Duration.ofSeconds(60*60*2)))
                )
                //设置学生回答题目的批改结果
                .filterWhen(right -> {
                    System.out.println(String.valueOf(right));
                           return  reactiveHashOperations.put(AchieveAnswerKey.piGaiTypeQuestionsId(circleId, questId, questionType), examineeId, String.valueOf(right))
                                    .flatMap(ok -> stringRedisTemplate.expire(AchieveAnswerKey.piGaiTypeQuestionsId(circleId, questId, questionType), Duration.ofSeconds(60 * 60 * 2)));
                        }
                    )
                //设置MONGO的题目回答值
                .filterWhen(r->sendValue(interactive,circleId,questionType,examineeId,questId,answer,fileList,r.toString()));
                //记录学生回答MONGO记录
//               .filterWhen(right -> insertInteractRecordService.answer(circleId, questId, examineeId, answer, String.valueOf(right))
//                       .flatMap(f -> MyAssert.isFalse(f, DefineCode.ERR0012, "保存mongodb记录失败")));
    }



    /**
     * 获得课堂当前题目的互动类型，验证题目id和参与人员
     * @param questionType   活动类型  练习、任务
     * @param circleId
     * @param examineeId
     * @param questId
     * @return
     */
    private Mono<Boolean> filterSelectVerify(final String questionType,final String circleId,final String examineeId, final String questId ) {
        //判断联系册是否包含题目Id
        return reactiveHashOperations.hasKey(MoreQueKey.bookQuestionMap(questionType,circleId),questId)
                        .flatMap(r-> MyAssert.isFalse(r,DefineCode.ERR0002,"练习册题目信息不存在"))
                //回答学生是否在所选范围内
                .filterWhen(r->reactiveHashOperations.get(MoreQueKey.questionsBookNowMap(questionType,circleId), "selected")
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

        //TODO 发布题目答案对比
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
     * 练习册回答信息信息写入
     *
     * @return
     */
    private Mono<Boolean> sendValue(final String interactive, final String circleId,final String questionType,final String examineeId,final String questId,final String answer,final List<DataDatumVo> fileList,final String answerRight) {

       return findExists(interactive,circleId,questionType,examineeId,questId)
               .flatMap(r->saveOrupdate(r.booleanValue(),interactive,circleId,questionType,examineeId,questId,answer,fileList,answerRight))
               .flatMap(r-> Mono.just(r));
    }

    public Mono<Boolean> findExists(final String interactive,final String circleId,final String questionType,final String examineeId,final String questId){
        Query query = Query.query(
                Criteria.where("circleId").is(circleId)
                        .and("examineeId").is(examineeId)
                        .and("questionType").is(questionType)  //练习、提问
                        .and("answList.questionId").is(questId));
        return reactiveMongoTemplate.exists(query,ActivityAskAnswer.class);
    }

    //添加题目回答信息
    private Mono<Boolean> saveOrupdate(final boolean r,final String interactive,final String circleId,final String questionType,final String examineeId,final String questId,final String answer,final List<DataDatumVo> fileList,final String answerRight){
        if(!r){
            return add(interactive,circleId,questionType,examineeId,questId,answer,fileList,answerRight);
        }else{
            System.out.println("***** circleId");
            final Query query = Query.query(
                    Criteria.where("circleId").is(circleId)
                            .and("examineeId").is(examineeId)
                            .and("questionType").is(questionType)  //练习、提问
                            .and("answList.questionId").is(questId));
            Update update = new Update()
                    .pull("answList", new InteractiveSheetAnsw(questId));
            return reactiveMongoTemplate.updateFirst(query,update,ActivityAskAnswer.class)
                    //删除记录成功后
                    .filter(r1->r1.wasAcknowledged())
                    //添加新的信息
                    .flatMap(r1->add(interactive,circleId,questionType,examineeId,questId,answer,fileList,answerRight));
        }
    }

    //修改Mongo题目信息
    private Mono<Boolean> add(final String interactive,final String circleId,final String questionType,final String examineeId,final String questId,final String answer,final List<DataDatumVo> fileList,final String answerRight){

        Query query = Query.query(
                Criteria.where("circleId").is(circleId)
                        .and("examineeId").is(examineeId)
                        .and("interactive").is(interactive)
                        //练习、任务
                        .and("questionType").is(questionType));
        Update update = new Update()
                .addToSet("answList", new InteractiveSheetAnsw(questId,answer,fileList,answerRight));
        return reactiveMongoTemplate.upsert(query,update,ActivityAskAnswer.class)
                .flatMap(r1->Mono.just(r1.wasAcknowledged()));
    }


    //添加redis题目回答的附件信息
    private Mono<Boolean> addAnswerFiles(final String circleId,final String examineeId,final String questId,final String questionType,final List<DataDatumVo> fileList) {
        if ((fileList != null) && (fileList.size() > 0)) {
            return Mono.just(JSON.toJSONString(fileList)).flatMap(jsonStr ->
                    reactiveHashOperations.put(AchieveAnswerKey.answerFileTypeQuestionsId(circleId, questId, questionType), examineeId, jsonStr)
                            .filterWhen(ok -> stringRedisTemplate.expire(AchieveAnswerKey.answerFileTypeQuestionsId(circleId, questId, questionType), Duration.ofSeconds(60 * 60 * 2)))
                            .flatMap(r -> Mono.just(true)));
        } else {
            return Mono.just(true);
        }
    }
}

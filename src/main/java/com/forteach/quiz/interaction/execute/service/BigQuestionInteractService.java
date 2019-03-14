package com.forteach.quiz.interaction.execute.service;

import com.forteach.quiz.exceptions.AskException;
import com.forteach.quiz.exceptions.ExamQuestionsException;
import com.forteach.quiz.interaction.execute.config.BigQueKey;
import com.forteach.quiz.interaction.execute.domain.ActivityAskAnswer;
import com.forteach.quiz.interaction.execute.domain.AskAnswer;
import com.forteach.quiz.interaction.execute.service.record.InsertInteractRecordService;
import com.forteach.quiz.interaction.execute.service.record.InteractRecordExerciseBookService;
import com.forteach.quiz.interaction.execute.service.record.InteractRecordQuestionsService;
import com.forteach.quiz.interaction.execute.web.vo.BigQuestionGiveVo;
import com.forteach.quiz.interaction.execute.web.vo.InteractiveSheetVo;
import com.forteach.quiz.interaction.execute.web.vo.MoreGiveVo;
import com.forteach.quiz.questionlibrary.domain.QuestionType;
import com.forteach.quiz.service.CorrectService;
import com.forteach.quiz.web.vo.InteractAnswerVo;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;

import static com.forteach.quiz.common.Dic.*;
import static com.forteach.quiz.interaction.execute.config.BigQueKey.CLASSROOM_ASK_QUESTIONS_ID;


/**
 * @Description: 提问交互
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/27  15:00
 */
@Slf4j
@Service
public class BigQuestionInteractService {

    private final ReactiveStringRedisTemplate stringRedisTemplate;
    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;
    private final CorrectService correctService;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final InsertInteractRecordService insertInteractRecordService;
    private final InteractRecordQuestionsService interactRecordQuestionsService;
    private final InteractRecordExerciseBookService interactRecordExerciseBookService;

    public BigQuestionInteractService(ReactiveStringRedisTemplate stringRedisTemplate,
                                      ReactiveHashOperations<String, String, String> reactiveHashOperations,
                                      CorrectService correctService,
                                      InsertInteractRecordService insertInteractRecordService,
                                      InteractRecordExerciseBookService interactRecordExerciseBookService,
                                      InteractRecordQuestionsService interactRecordQuestionsService,
                                      ReactiveMongoTemplate reactiveMongoTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.correctService = correctService;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.interactRecordExerciseBookService = interactRecordExerciseBookService;
        this.interactRecordQuestionsService = interactRecordQuestionsService;
        this.insertInteractRecordService = insertInteractRecordService;
    }

//    /**
//     * 课堂提问发题
//     *
//     * @param giveVo
//     * @return
//     */
//    public Mono<Boolean> sendQuestion(final BigQuestionGiveVo giveVo) {
//
////        HashMap<String, String> map = new HashMap<>(6);
////        map.put("questionId", giveVo.getQuestionId());//题目编号
////        map.put("interactive", giveVo.getInteractive());  //互动方式（举手、抢答等）
////        map.put("category", giveVo.getCategory());//选取类别（个人、小组）
////        map.put("selected", giveVo.getSelected());//选中人员 [逗号 分割]
////        map.put("cut", giveVo.getCut());//是否切题
////        map.put("time", DataUtil.format(new Date()));//创建时间
//
//        //创建课堂提问的题目36分钟过期
////        Mono<Boolean> set = reactiveHashOperations.putAll(askQuestionsId(QuestionType.BigQuestion, giveVo.getCircleId()), map);
//;
//        //创建课堂提问类型，不同互动方式的提问缓存集合
//        Mono<Boolean> createQu = stringRedisTemplate.opsForList().leftPush(BigQueKey.askTypeQuestionsId(QuestionType.TiWen, giveVo), giveVo.getQuestionId())
//                .flatMap(item -> {
////                    //创建当前题目
//                         return stringRedisTemplate.opsForValue().set(BigQueKey.askTypeQuestionsIdNow(QuestionType.TiWen, giveVo),giveVo.getQuestionId(),Duration.ofSeconds(60 * 60 * 2));
//                        }
//                )
//                .filterWhen(ok->stringRedisTemplate.expire(BigQueKey.askTypeQuestionsId(QuestionType.TiWen, giveVo), Duration.ofSeconds(60*60*2)))
//                //更新当前题目和上一题的题目信息
//                .filterWhen(ok->  stringRedisTemplate.opsForValue().getAndSet(BigQueKey.askTypeQuestionsIdNow(QuestionType.TiWen, giveVo),giveVo.getQuestionId())
//                                                .flatMap(old-> stringRedisTemplate.opsForValue().set(BigQueKey.askTypeQuestionsIdPrve(QuestionType.TiWen, giveVo),old,Duration.ofSeconds(60 * 60 * 2)))
//                        )
//                        //设置当前的题目编号为新值
//                        .filterWhen(str->stringRedisTemplate.expire(BigQueKey.askTypeQuestionsIdNow(QuestionType.TiWen, giveVo),Duration.ofSeconds(60 * 60 * 2)))
//                        //如果本题和迁移题（redis里的）前题目id，不一致视为换题，进行清理
//                       .filterWhen(str->clearCut(giveVo));
//
//        //创建提问题目,并保存题目回答的学生(学生编号逗号分隔)
//        Mono<Boolean> createQuID =stringRedisTemplate.opsForValue()
//                  .set(BigQueKey.askTypeQuestionsId(QuestionType.TiWen, giveVo,giveVo.getQuestionId()),giveVo.getSelected(), Duration.ofSeconds(60 * 60 * 2))
//                  .flatMap(item -> MyAssert.isFalse(item, DefineCode.ERR0013, "redis操作错误"))
//                  .filterWhen(ok->stringRedisTemplate.opsForValue()
//                          //记录当前题目的成员回答类型（个人、小组）和互动方式（选人，抢答）
//                          .set(BigQueKey.askTypeQuestionsIdType(giveVo.getCircleId(),giveVo.getQuestionId()),giveVo.getCategory().concat(",").concat(giveVo.getInteractive()), Duration.ofSeconds(60 * 60 * 2)));
//
//
//        //删除抢答答案（删除课堂提问的题目ID的回答信息）
//        Mono<Boolean> removeRace = stringRedisTemplate.opsForValue().delete(giveVo.getRaceAnswerFlag())
//                //删除信息没找到键值失败，同样返回TRUE
//                .flatMap(ok->Mono.just(true));
//
//        //执行创建提问，并返回执行结果
//        return Flux.concat(createQu,createQuID,removeRace)
//                .count()
//                .flatMap(ct-> MyAssert.isTrue(ct<3, DefineCode.ERR0013, "创建提问失败"))
//                //创建改题目的回答者信息
//               .filterWhen(obj -> interactRecordExecuteService.releaseQuestion(giveVo.getCircleId(), giveVo.getQuestionId(), giveVo.getSelected(), giveVo.getCategory(), giveVo.getInteractive()));
//    }



//    /**
//     * 发起提问举手
//     * 清空老师端显示的举手学生
//     *
//     * @param
//     * @return
//     */
//
//    public Mono<Long> launchRaise(final AskLaunchVo askLaunchVo) {
//        Mono<Long> deleteDistinctKey = stringRedisTemplate.delete(askLaunchVo.getRaiseDistinctKey());
//        Mono<Long> deleteRaiseExmKey = stringRedisTemplate.delete(askLaunchVo.getRaiseKey(), "examinee");
//        return deleteDistinctKey.zipWith(deleteRaiseExmKey, (d, e) -> d + e);
//    }

//
//    /**
//     * 学生进行举手
//     * 最后记录
//     *
//     * @return
//     */
//    public Mono<Long> raiseHand(final RaisehandVo raisehandVo) {
//        Mono<Long> set = stringRedisTemplate.opsForSet().add(raisehandVo.getRaiseKey(), raisehandVo.getExamineeId());
//        Mono<Boolean> time = stringRedisTemplate.expire(raisehandVo.getRaiseKey(), Duration.ofSeconds(60 * 60 * 10));
//        Mono<String> questionId = askQuestionId(raisehandVo.getAskKey(QuestionType.TiWen));
//        return set.zipWith(time, (c, t) -> t ? c : -1)
//                .filterWhen(obj -> questionId.flatMap(qid -> interactRecordExecuteService.raiseHand(raisehandVo.getCircleId(), raisehandVo.getExamineeId(), qid)));
//    }


    /**
     * 课堂发布练习册提问
     *  TODO 临时报错注解
     * @param giveVo
     * @return
     */
    public Mono<Long> sendInteractiveBook(final MoreGiveVo giveVo) {
        HashMap<String, String> map = new HashMap<>(10);
        map.put("questionId", giveVo.getQuestionId());
        map.put("category", giveVo.getCategory());
        map.put("selected", giveVo.getSelected());
        map.put("cut", giveVo.getCut());


        Mono<Boolean> set = reactiveHashOperations.putAll(askQuestionsId(QuestionType.LianXi, giveVo.getCircleId()), map);
        Mono<Boolean> time = stringRedisTemplate.expire(askQuestionsId(QuestionType.LianXi, giveVo.getCircleId()), Duration.ofSeconds(60 * 60 * 10));

        //TODO 未记录
        return Flux.concat(set, time).filter(flag -> !flag)
                .filterWhen(obj -> interactRecordExerciseBookService.interactiveBook(giveVo))
                .count();

    }
    /**
     * 如果前后两题一致
     * @param giveVo
     * @return
     */
    private Mono<Boolean> clearCut(final BigQuestionGiveVo giveVo) {
        return stringRedisTemplate.opsForValue().get(BigQueKey.askTypeQuestionsIdPrve(QuestionType.TiWen, giveVo.getCircleId(), giveVo.getInteractive()))
                .zipWith(Mono.just(giveVo.getQuestionId()), String::equals)
                .flatMap(flag -> {
                    if (!flag) {
                        //清除提问标识
                        //前后两题一致,删除该题型在这个课堂的前缀数据？？？
                        return stringRedisTemplate.opsForValue().delete(giveVo.getExamineeIsReplyKey(QuestionType.TiWen));

                    }
                    return Mono.just(true);
                });
    }

    /**
     * 课堂发布练习册提问
     *  TODO 临时报错注解
     * @param giveVo
     * @return
     */
    public Mono<Long> sendQuestion(final BigQuestionGiveVo giveVo) {

        HashMap<String, String> map = new HashMap<>(10);
        map.put("questionId", giveVo.getQuestionId());
        map.put("interactive", giveVo.getInteractive());
        map.put("category", giveVo.getCategory());
        map.put("selected", giveVo.getSelected());
        map.put("cut", giveVo.getCut());

        //如果本题和redis里的题目id不一致 视为换题 进行清理
        Mono<Boolean> clearCut = clearCut(giveVo);

        Mono<Boolean> set = reactiveHashOperations.putAll(askQuestionsId(QuestionType.TiWen, giveVo.getCircleId()), map);
        Mono<Boolean> time = stringRedisTemplate.expire(askQuestionsId(QuestionType.TiWen, giveVo.getCircleId()), Duration.ofSeconds(60 * 60 * 10));

        //删除抢答答案
        Mono<Boolean> removeRace = stringRedisTemplate.opsForValue().delete(giveVo.getRaceAnswerFlag());

        return Flux.concat(set, time, removeRace, clearCut).filter(flag -> !flag)
                .filterWhen(obj -> interactRecordQuestionsService.releaseQuestion(giveVo.getCircleId(), giveVo.getQuestionId(), giveVo.getSelected(), giveVo.getCategory(), giveVo.getInteractive()))
                .count();
    }

//    private Mono<Boolean> clearCut(final BigQuestionGiveVo giveVo) {
//        return reactiveHashOperations.get(askQuestionsId(QuestionType.TiWen, giveVo.getCircleId()), "questionId")
//                .zipWith(Mono.just(giveVo.getQuestionId()), String::equals)
//                .flatMap(flag -> {
//                    if (flag) {
//                        //清除提问标识
//                        return Mono.just(false);
//                    }
//                    return stringRedisTemplate.opsForValue().delete(giveVo.getExamineeIsReplyKey(QuestionType.TiWen));
//                });
//    }


    /**
     * 提交答案
     * 提交答案前判断题目id 是否与当前id一致
     * 提交后判断对错
     * 提交后在当前课堂里添加是否回答
     *
     * @return
     */

    public Mono<String> sendAnswer(final InteractAnswerVo answerVo) {

        return Mono.just(answerVo)
                .transform(this::filterSelectVerify)
                .flatMap(answer -> askInteractiveType(answer.getAskKey(QuestionType.TiWen)))
                .zipWhen(type -> sendAnswerVerify(answerVo.getAskKey(QuestionType.TiWen), answerVo.getQuestionId(), answerVo.getCut()))
                .flatMap(tuple2 -> {
                    if (tuple2.getT2()) {
                        switch (tuple2.getT1()) {
                            case ASK_INTERACTIVE_RACE:
                                return sendRace(answerVo);
                            case ASK_INTERACTIVE_RAISE:
                                return sendRaise(answerVo);
                            case ASK_INTERACTIVE_SELECT:
                                return sendSelect(answerVo, ASK_INTERACTIVE_SELECT);
                            case ASK_INTERACTIVE_VOTE:
                                return Mono.empty();
                            default:
                                throw new ExamQuestionsException("非法参数 错误的数据类型");
                        }
                    } else {
                        return Mono.error(new AskException("请重新刷新获取最新题目"));
                    }
                })
                .filterWhen(right -> setRedis(answerVo.getExamineeIsReplyKey(QuestionType.TiWen), answerVo.getExamineeId(), answerVo.getAskKey(QuestionType.TiWen)))
                .filterWhen(right -> insertInteractRecordService.answer(answerVo.getCircleId(), answerVo.getQuestionId(), answerVo.getExamineeId(), answerVo.getAnswer(), right));
    }



    /**
     * 验证课堂提问选中
     *
     * @param answerVo
     * @return
     */
    private Mono<InteractAnswerVo> filterSelectVerify(final Mono<InteractAnswerVo> answerVo) {
        return answerVo.zipWhen(answer -> selectVerify(answer.getAskKey(QuestionType.TiWen), answer.getExamineeId()))
                .flatMap(tuple2 -> {
                    if (tuple2.getT2()) {
                        return Mono.just(tuple2.getT1());
                    } else {
                        return Mono.error(new AskException("该题未被选中 不能答题"));
                    }
                });
    }

    /**
     * 验证练习册发放选中
     * @param answerVo
     * @return
     */
    private Mono<InteractiveSheetVo> filterSheetSelectVerify(final Mono<InteractiveSheetVo> answerVo) {
        return answerVo.zipWhen(answer -> selectVerify(answer.getAskKey(QuestionType.LianXi), answer.getExamineeId()))
                .flatMap(tuple2 -> {
                    if (tuple2.getT2()) {
                        return Mono.just(tuple2.getT1());
                    } else {
                        return Mono.error(new AskException("该题未被选中 不能答题"));
                    }
                });
    }

    /**
     * 通过 提问key,判断是否是选择
     *
     * @param askKey
     * @return
     */
    private Mono<Boolean> selectVerify(final String askKey, final String examineeId) {
        return reactiveHashOperations.get(askKey, "selected")
                .map(selectId ->
                        isSelected(selectId, examineeId));
    }

    /**
     * 验证提交的答案信息
     *
     * @return
     */
    private Mono<Boolean> sendAnswerVerify(final String askId, final String oQuestionId, final String oCut) {
        Mono<String> questionId = reactiveHashOperations.get(askId, "questionId");
        Mono<String> cut = reactiveHashOperations.get(askId, "cut");
        return cut.zipWith(questionId, (c, q) -> c.equals(oCut) && q.equals(oQuestionId));
    }
    /**
     * 抢答 回答
     * @return
     */
    private Mono<String> sendRace(final InteractAnswerVo interactAnswerVo) {
        return stringRedisTemplate.hasKey(interactAnswerVo.getRaceAnswerFlag())
                .flatMap(flag -> {
                    if (flag) {
                        return Mono.error(new AskException("该抢答题已被回答"));
                    }
                    return sendSelect(interactAnswerVo, ASK_INTERACTIVE_RACE)
                            .filterWhen(askAnswer -> {
                                if (askAnswer != null) {
                                    return stringRedisTemplate.opsForValue().set(interactAnswerVo.getRaceAnswerFlag(), STATUS_SUCCESS, Duration.ofSeconds(60 * 60));
                                } else {
                                    return Mono.empty();
                                }
                            });
                });
    }

    /**
     * 举手 回答
     */
    private Mono<String> sendRaise(final InteractAnswerVo interactVo) {
        return Mono.just(interactVo).flatMap(interactAnswerVo -> sendSelect(interactAnswerVo, ASK_INTERACTIVE_RAISE));
    }


    /**
     * 选人 回答
     *
     * @return
     */
    private Mono<String> sendSelect(final InteractAnswerVo interactAnswerVo, final String type) {
        if (log.isDebugEnabled()) {
            log.debug("选人 回答 参数 interactAnswerVo : {}, type : {}", interactAnswerVo, type);
        }
        return correctService.correcting(interactAnswerVo.getQuestionId(), interactAnswerVo.getAnswer())
                .flatMap(f -> {
                    Query query = Query.query(
                            Criteria.where("circleId").is(interactAnswerVo.getCircleId())
                                    .and("questionId").is(interactAnswerVo.getQuestionId())
                                    .and("examineeId").is(interactAnswerVo.getExamineeId()));

                    Update update = Update.update("answer", interactAnswerVo.getAnswer());
                    update.set("interactive", type);
                    update.set("right", String.valueOf(f));
                    update.set("uDate", new Date());

                    return reactiveMongoTemplate.upsert(query, update, AskAnswer.class).flatMap(result -> {
                        if (result.wasAcknowledged()) {
                            return Mono.just(f);
                        } else {
                            return Mono.error(new AskException("操作失败"));
                        }
                    });
                });
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
     * 课堂互动的hash前缀
     *
     * @return
     */
    private String askQuestionsId(final QuestionType type, final String circleId) {
        return CLASSROOM_ASK_QUESTIONS_ID.concat(type.name()).concat(circleId);
    }


    /**
     * 获取提问类型
     *
     * @param askKey
     * @return
     */
    private Mono<String> askInteractiveType(final String askKey) {
        return reactiveHashOperations.get(askKey, "interactive");
    }


    private Mono<Boolean> setRedis(final String redisKey, final String value, final String askKey) {

        Mono<Long> set = stringRedisTemplate.opsForSet().add(redisKey, value);
        Mono<Boolean> time = stringRedisTemplate.expire(redisKey, Duration.ofSeconds(60 * 60 * 10));

        return set.zipWith(time, (c, t) -> t ? c : -1).map(monoLong -> monoLong != -1)
                .filterWhen(take -> reactiveHashOperations.increment(askKey, "answerFlag", 1).map(Objects::nonNull));
    }

    /**
     * 获得课堂的提问id
     */
    private Mono<String> askQuestionId(final String askKey) {
        return reactiveHashOperations.get(askKey, "questionId");
    }


    /**
     * 提交答案
     *
     * @param sheetVo
     * @return
     */
    public Mono<String> sendExerciseBookAnswer(final InteractiveSheetVo sheetVo) {
        return Mono.just(sheetVo)
                .transform(this::filterSheetSelectVerify)
                .filterWhen(shee ->
                        sendAnswerVerifyMore(shee.getAskKey(QuestionType.LianXi), shee.getAnsw().getQuestionId(), shee.getCut())
                )
                .filterWhen(
                        set -> sendValue(sheetVo))
                .filterWhen(
                        right -> setRedis(sheetVo.getExamineeIsReplyKey(QuestionType.LianXi), sheetVo.getExamineeId(), sheetVo.getAskKey(QuestionType.LianXi)))
                // Todo 提交答案保存
                .filterWhen(right -> insertInteractRecordService.pushMongo(sheetVo, "exerciseBooks"))
//                .map(InteractiveSheetVo::getCut);
                .thenReturn(sheetVo.getCut());
    }

    /**
     * 直接累积答案 不用给分
     *
     * @return
     */
    private Mono<Boolean> sendValue(final InteractiveSheetVo sheetVo) {

        Query query = Query.query(
                Criteria.where("circleId").is(sheetVo.getCircleId())
                        .and("examineeId").is(sheetVo.getExamineeId())
                        .and("libraryType").is(QuestionType.LianXi));

        Update update = new Update();
        sheetVo.getAnsw().setDate(new Date());
        update.addToSet("answList", sheetVo.getAnsw());

        return reactiveMongoTemplate.upsert(query, update, ActivityAskAnswer.class).map(UpdateResult::wasAcknowledged);
    }

    /**
     * 验证提交的答案信息
     * 判断发布的id集与提交的答案 如果出现差集 不能提交 (回答的问题id,存在没有发布的问题列表 )
     *
     * @return
     */
    private Mono<Boolean> sendAnswerVerifyMore(final String askId, final String oQuestionId, final String oCut) {

        Mono<List<String>> questionId = reactiveHashOperations.get(askId, "questionId").map(ids -> Arrays.asList(ids.split(",")));


        Mono<String> cut = reactiveHashOperations.get(askId, "cut");
        //如果差集不等于0 验证不通过
        Mono<Boolean> questionVerify = questionId.map(list -> list.contains(oQuestionId));

        Mono<Boolean> cutVerify = cut.zipWith(Mono.just(oCut), String::equals);

        return Flux.concat(questionVerify, cutVerify).filter(flag -> !flag).count().flatMap(c -> {
            if (c == 0) {
                return Mono.just(true);
            } else {
                return Mono.just(false);
            }
        });
    }

}

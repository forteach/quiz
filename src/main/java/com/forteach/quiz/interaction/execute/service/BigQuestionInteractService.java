package com.forteach.quiz.interaction.execute.service;

import com.forteach.quiz.common.DataUtil;
import com.forteach.quiz.exceptions.AskException;
import com.forteach.quiz.interaction.execute.domain.ActivityAskAnswer;
import com.forteach.quiz.interaction.execute.service.record.InsertInteractRecordService;
import com.forteach.quiz.interaction.execute.service.record.InteractRecordExerciseBookService;
import com.forteach.quiz.interaction.execute.web.vo.InteractiveSheetVo;
import com.forteach.quiz.interaction.execute.web.vo.MoreGiveVo;
import com.forteach.quiz.questionlibrary.domain.QuestionType;
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
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final InsertInteractRecordService insertInteractRecordService;
    private final InteractRecordExerciseBookService interactRecordExerciseBookService;

    public BigQuestionInteractService(ReactiveStringRedisTemplate stringRedisTemplate,
                                      ReactiveHashOperations<String, String, String> reactiveHashOperations,
                                      InsertInteractRecordService insertInteractRecordService,
                                      InteractRecordExerciseBookService interactRecordExerciseBookService,
                                      ReactiveMongoTemplate reactiveMongoTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.interactRecordExerciseBookService = interactRecordExerciseBookService;
        this.insertInteractRecordService = insertInteractRecordService;
    }
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


    private Mono<Boolean> setRedis(final String redisKey, final String value, final String askKey) {

        Mono<Long> set = stringRedisTemplate.opsForSet().add(redisKey, value);
        Mono<Boolean> time = stringRedisTemplate.expire(redisKey, Duration.ofSeconds(60 * 60 * 10));

        return set.zipWith(time, (c, t) -> t ? c : -1).map(monoLong -> monoLong != -1)
                .filterWhen(take -> reactiveHashOperations.increment(askKey, "answerFlag", 1).map(Objects::nonNull));
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
        sheetVo.getAnsw().setDate(DataUtil.format(new Date()));
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

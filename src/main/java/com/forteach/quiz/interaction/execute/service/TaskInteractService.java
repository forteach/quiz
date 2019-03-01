package com.forteach.quiz.interaction.execute.service;

import com.forteach.quiz.exceptions.AskException;
import com.forteach.quiz.interaction.execute.domain.ActivityAskAnswer;
import com.forteach.quiz.interaction.execute.web.vo.InteractiveSheetVo;
import com.forteach.quiz.interaction.execute.web.vo.MoreGiveVo;
import com.forteach.quiz.questionlibrary.domain.QuestionType;
import com.forteach.quiz.questionlibrary.repository.TaskQuestionRepository;
import com.mongodb.client.result.UpdateResult;
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

import static com.forteach.quiz.common.KeyStorage.CLASSROOM_ASK_QUESTIONS_ID;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/14  15:10
 */
@Service
public class TaskInteractService {

    private final ReactiveStringRedisTemplate stringRedisTemplate;

    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    private final TaskQuestionRepository questionRepository;

    private final InteractRecordExecuteService interactRecordExecuteService;

    public TaskInteractService(ReactiveStringRedisTemplate stringRedisTemplate,
                               ReactiveHashOperations<String, String, String> reactiveHashOperations,
                               ReactiveMongoTemplate reactiveMongoTemplate,
                               TaskQuestionRepository questionRepository,
                               InteractRecordExecuteService interactRecordExecuteService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reactiveHashOperations = reactiveHashOperations;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.questionRepository = questionRepository;
        this.interactRecordExecuteService = interactRecordExecuteService;
    }

    /**
     * 发布问卷问题
     *
     * @param giveVo
     * @return
     */
    public Mono<Long> sendQuestion(final MoreGiveVo giveVo) {

        HashMap<String, String> map = new HashMap<>(10);
        map.put("questionId", giveVo.getQuestionId());
        map.put("category", giveVo.getCategory());
        map.put("selected", giveVo.getSelected());
        map.put("cut", giveVo.getCut());

        //如果本题和redis里的题目id不一致 视为换题 进行清理
        Mono<Boolean> clearCut = clearCut(giveVo);

        Mono<Boolean> set = reactiveHashOperations.putAll(askQuestionsId(QuestionType.TaskQuestion, giveVo.getCircleId()), map);
        Mono<Boolean> time = stringRedisTemplate.expire(askQuestionsId(QuestionType.TaskQuestion, giveVo.getCircleId()), Duration.ofSeconds(60 * 60 * 10));

        //TODO 未记录
        return Flux.concat(set, time, clearCut).filter(flag -> !flag).count()
                .filterWhen(obj -> interactRecordExecuteService.releaseInteractRecord(giveVo.getCircleId(), giveVo.getQuestionId(), giveVo.getSelected(), giveVo.getCategory(), "interacts"));

    }

    private Mono<Boolean> clearCut(final MoreGiveVo giveVo) {
        return reactiveHashOperations.get(askQuestionsId(QuestionType.TaskQuestion, giveVo.getCircleId()), "questionId")
                .zipWith(Mono.just(giveVo.getQuestionId()), String::equals)
                .flatMap(flag -> {
                    if (flag) {
                        //清除提问标识
                        return Mono.just(false);
                    }
                    return stringRedisTemplate.opsForValue().delete(giveVo.getExamineeIsReplyKey(QuestionType.TaskQuestion));
                });
    }

    /**
     * 根据课堂 获取去重key
     *
     * @return
     */
    private String askQuestionsId(QuestionType type, String circleId) {
        return CLASSROOM_ASK_QUESTIONS_ID.concat(type.name()).concat(circleId);
    }


    /**
     * 提交答案
     *
     * @param sheetVo
     * @return
     */
    public Mono<String> sendAnswer(final InteractiveSheetVo sheetVo) {
        return Mono.just(sheetVo)
                .transform(this::filterSelectVerify)
                .filterWhen(shee -> sendAnswerVerifyMore(shee.getAskKey(QuestionType.TaskQuestion), shee.getAnsw().getQuestionId(), shee.getCut()))
                .filterWhen(set -> sendValue(sheetVo))
                .filterWhen(right -> setRedis(sheetVo.getExamineeIsReplyKey(QuestionType.TaskQuestion), sheetVo.getExamineeId(), sheetVo.getAskKey(QuestionType.TaskQuestion)))
                //保存到记录mongodb 集合
                .filterWhen(tack -> interactRecordExecuteService.pushMongo(tack, "interacts"))
                .map(InteractiveSheetVo::getCut);
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
                        .and("libraryType").is(QuestionType.TaskQuestion));

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

        //判断提交的问题数量是否相同
        return Flux.concat(questionVerify, cutVerify).filter(flag -> !flag).count().flatMap(c -> {
            if (c == 0) {
                return Mono.just(true);
            } else {
                return Mono.just(false);
            }
        });
    }

    /**
     * 过滤查询选择题
     * @param answerVo
     * @return
     */
    private Mono<InteractiveSheetVo> filterSelectVerify(final Mono<InteractiveSheetVo> answerVo) {
        return answerVo.zipWhen(answer -> selectVerify(answer.getAskKey(QuestionType.TaskQuestion), answer.getExamineeId()))
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
                .map(selectId -> isSelected(selectId, examineeId));
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
     * 设置提交答案的 redis 信息　保存时间是两个小时
     * @param redisKey
     * @param value
     * @param askKey
     * @return
     */
    private Mono<Boolean> setRedis(final String redisKey, final String value, final String askKey) {

        Mono<Long> set = stringRedisTemplate.opsForSet().add(redisKey, value);
        Mono<Boolean> time = stringRedisTemplate.expire(redisKey, Duration.ofSeconds(60 * 60 * 10));

        return set.zipWith(time, (c, t) -> t ? c : -1)
                .map(monoLong -> monoLong != -1)
                .filterWhen(take -> reactiveHashOperations.increment(askKey, "answerFlag", 1)
                        .map(Objects::nonNull));
    }
}

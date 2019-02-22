package com.forteach.quiz.questionlibrary.service;

import com.forteach.quiz.questionlibrary.domain.base.QuestionExamEntity;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import static com.forteach.quiz.common.Dic.MONGDB_ID;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/8  10:00
 */
@Service
public class KeywordService<T extends QuestionExamEntity> {


    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public KeywordService(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    /**
     * 增加关键字
     *
     * @param keyword
     * @param bigQuestionId
     * @return
     */
    public Mono<Boolean> increase(final String[] keyword, final String bigQuestionId) {

        Criteria criteria = new Criteria();
        criteria.and(MONGDB_ID).is(bigQuestionId);

        Update update = new Update();
        update.set("keyword", keyword);

        return reactiveMongoTemplate.upsert(Query.query(criteria), update, entityClass()).map(UpdateResult::isModifiedCountAvailable);
    }

    /**
     * 删除关键字
     *
     * @param keyword
     * @param bigQuestionId
     * @return
     */
    public Mono<Boolean> undock(final String[] keyword, final String bigQuestionId) {

        Criteria criteria = new Criteria();
        criteria.and(MONGDB_ID).is(bigQuestionId);

        Update update = new Update();
        update.pullAll("keyword", keyword);

        return reactiveMongoTemplate.upsert(Query.query(criteria), update, entityClass()).map(UpdateResult::isModifiedCountAvailable);
    }

    /**
     * 获取关键字存在的问题id
     *
     * @param keyword
     * @return
     */
    @SuppressWarnings(value = "all")
    public Flux<String> keywordQuestion(final String[] keyword) {

        Query query = new Query();

        Criteria criteria = new Criteria();
        criteria.and("keyword").all(keyword);

        query.fields().include(MONGDB_ID);

        return reactiveMongoTemplate.find(Query.query(criteria), entityClass()).map(QuestionExamEntity::getId);

    }

    public Flux<T> keywordFilter(final Flux<T> questionFlux, final String[] keyword) {

        if (keyword == null || keyword.length < 1) {
            return questionFlux;
        }

        final Mono<List<String>> keywordList = keywordQuestion(keyword).collectList();

        return questionFlux
                .filterWhen(flux -> questionFlux.zipWith(keywordList, (questions, keywords) ->
                        keywords.contains(flux.getId())
                ));
    }

    /**
     * 获取泛型的class
     *
     * @return
     */
    private Class<T> entityClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

}

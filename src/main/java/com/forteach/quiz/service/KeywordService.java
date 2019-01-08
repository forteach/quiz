package com.forteach.quiz.service;

import com.forteach.quiz.domain.BigQuestion;
import com.forteach.quiz.repository.BigQuestionRepository;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.forteach.quiz.common.Dic.MONGDB_ID;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/8  10:00
 */
@Service
public class KeywordService {

    private final BigQuestionRepository repository;

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public KeywordService(BigQuestionRepository repository, ReactiveMongoTemplate reactiveMongoTemplate) {
        this.repository = repository;
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

        return reactiveMongoTemplate.upsert(Query.query(criteria), update, BigQuestion.class).map(UpdateResult::isModifiedCountAvailable);
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

        return reactiveMongoTemplate.upsert(Query.query(criteria), update, BigQuestion.class).map(UpdateResult::isModifiedCountAvailable);
    }

    /**
     * 获取关键字存在的问题id
     *
     * @param keyword
     * @return
     */
    public Flux<String> keywordQuestion(final String[] keyword) {

        Query query = new Query();

        Criteria criteria = new Criteria();
        criteria.and("keyword").all(keyword);

        query.fields().include(MONGDB_ID);

        return reactiveMongoTemplate.find(Query.query(criteria), BigQuestion.class).map(BigQuestion::getId);

    }

    public Flux<BigQuestion> keywordFilter(final Flux<BigQuestion> questionFlux, final String[] keyword) {

        if (keyword == null || keyword.length < 1) {
            return questionFlux;
        }

        final Mono<List<String>> keywordList = keywordQuestion(keyword).collectList();

        return questionFlux
                .filterWhen(flux -> questionFlux.zipWith(keywordList, (questions, keywords) ->
                        keywords.contains(flux.getId())
                ));
    }

}

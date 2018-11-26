package com.forteach.quiz.service;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/21  9:43
 */
public class ScreenService {

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public ScreenService(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }


}

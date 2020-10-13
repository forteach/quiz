package com.forteach.quiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/9 10:52
 */
@EnableScheduling
@EnableWebFlux
@SpringBootApplication
@EnableMongoAuditing
@EnableReactiveMongoRepositories
public class QuizApplication {

    public static void main(String[] args) {

        SpringApplication app = new SpringApplication(QuizApplication.class);
        app.run(args);

    }

}

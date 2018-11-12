package com.forteach.quiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/9 10:52
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class QuizApplication {

    public static void main(String[] args) {

        SpringApplication app = new SpringApplication(QuizApplication.class);
        app.run(args);

    }
}

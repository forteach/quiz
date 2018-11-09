package com.forteach.quiz.web;

import com.forteach.quiz.common.WebResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/9 11:25
 */
@Slf4j
@RestController
public class DemoController {

    @GetMapping(value = "/error",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Mono<String> exceptionReturn(){
        return Mono.error(new RuntimeException("test error"));
    }

    @GetMapping(value = "/helloWord",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Mono<WebResult> testReturn(){
        return WebResult.okResultMono(1000);
    }

}

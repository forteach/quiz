package com.forteach.quiz.config;

import com.forteach.quiz.common.WebResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/9 11:19
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public Mono<WebResult> serverExceptionHandler(Exception ex) {
        log.error(ex.getMessage(),ex);
        return WebResult.failResultMono();
    }

}

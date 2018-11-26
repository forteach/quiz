package com.forteach.quiz.config;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.exceptions.ProblemSetExceptions;
import io.lettuce.core.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
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
    public Mono<WebResult> serverExceptionHandler(ServerWebExchange exchange, Exception e) {
        log.error("全局异常拦截器  {}   请求地址 {}  /  异常信息  {}", e, exchange.getRequest().getPath(), e.getMessage());
        return WebResult.failResultMono();
    }

    @ExceptionHandler(ProblemSetExceptions.class)
    public Mono<WebResult> serverExceptionHandler(ServerWebExchange exchange, ProblemSetExceptions e) {
        log.error("全局异常拦截器  练习册异常 {}   请求地址 {}  /  异常信息  {}", e, exchange.getRequest().getPath(), e.getMessage());
        return Mono.just(WebResult.failResult(9200, e.getMessage()));
    }

    @ExceptionHandler(RedisException.class)
    public Mono<WebResult> serverExceptionHandler(ServerWebExchange exchange, RedisException e) {
        log.error("全局异常拦截器  redis 异常 {}   请求地址 {}  /  异常信息  {}", e, exchange.getRequest().getPath(), e.getMessage());
        return WebResult.failResultMono();
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Mono<WebResult> methodArgumentNotValidException(ServerWebExchange exchange, MethodArgumentNotValidException e) {
        log.error("全局异常拦截器 参数校验不正确  {} 请求地址 {}  /  异常信息  {}", e, exchange.getRequest().getPath(), e.getMessage());
        return WebResult.failResultMono(9000);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public Mono<WebResult> httpMessageNotReadableException(ServerWebExchange exchange, HttpMessageNotReadableException e) {
        log.error("全局异常拦截器 请求参数格式不正确  {} 请求地址 {}  /  异常信息  {}", e, exchange.getRequest().getPath(), e.getMessage());
        return WebResult.failResultMono(9100);
    }

}

package com.forteach.quiz.filter;

import com.forteach.quiz.handlers.ErrorHandler;
import com.forteach.quiz.handlers.JwtHandler;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-2-20 09:55
 * @version: 1.0
 * @description:
 */
public class TokenFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final JwtHandler jwtHandler;
    private final ErrorHandler errorHandler;

    private TokenFilterFunction (JwtHandler jwtHandler, ErrorHandler errorHandler){
        this.jwtHandler = jwtHandler;
        this.errorHandler = errorHandler;
    }

    /**
     * 对用户的token 进行过滤和验证
     * @param request
     * @param next
     * @return
     */
    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {
        return jwtHandler.verify(request).flatMap(f -> {
            if (f) {
                return next.handle(request);
            }else {
                return ServerResponse.badRequest()
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .body(Mono.just("token 无效"), String.class);

            }
        }).onErrorResume(errorHandler::throwableError);
    }
}

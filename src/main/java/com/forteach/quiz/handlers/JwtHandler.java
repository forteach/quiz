package com.forteach.quiz.handlers;

import com.forteach.quiz.service.TokenService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-2-20 11:28
 * @version: 1.0
 * @description:
 */
@Component
public class JwtHandler {

    private final TokenService tokenService;

    private JwtHandler(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public Mono<Boolean> verify(ServerRequest request) {
        return tokenService.check(request);
    }
}

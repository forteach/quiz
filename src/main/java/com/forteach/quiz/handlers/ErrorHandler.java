package com.forteach.quiz.handlers;

import com.forteach.quiz.domain.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-2-20 10:56
 * @version: 1.0
 * @description:
 */
@Component
@Slf4j
public class ErrorHandler {

    private static final String ERROR_RAISED = "error raised";

    public Mono<ServerResponse> throwableError(final Throwable error) {
        log.error(ERROR_RAISED, error);
        return Mono.just(error).transform(this::getResponse);
    }

    private  <T extends Throwable> Mono<ServerResponse> getResponse(final Mono<T> monoError) {
        return monoError.transform(ThrowableTranslator::translate)
                .flatMap(translation -> ServerResponse
                        .status(translation.getHttpStatus())
                        .body(Mono.just(new ErrorResponse(translation.getMessage())), ErrorResponse.class));
    }
}

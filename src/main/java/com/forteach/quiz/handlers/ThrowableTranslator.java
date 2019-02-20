package com.forteach.quiz.handlers;

import com.forteach.quiz.exceptions.TokenException;
import io.lettuce.core.RedisException;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-2-20 10:58
 * @version: 1.0
 * @description:
 */
class ThrowableTranslator {
    private final HttpStatus httpStatus;
    private final String message;

    private ThrowableTranslator(final Throwable throwable) {
        this.httpStatus = getStatus(throwable);
        this.message = throwable.getMessage();
    }

    /**
     * 根据不同的错误 给予不同的错误码
     *
     * @param error
     * @return
     */
    private HttpStatus getStatus(final Throwable error) {
        if (error instanceof RedisException) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else if (error instanceof TokenException) {
            return HttpStatus.BAD_REQUEST;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    HttpStatus getHttpStatus() {
        return httpStatus;
    }

    String getMessage() {
        return message;
    }

    static <T extends Throwable> Mono<ThrowableTranslator> translate(final Mono<T> throwable) {
        return throwable.flatMap(error -> Mono.just(new ThrowableTranslator(error)));
    }
}

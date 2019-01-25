package com.forteach.quiz.exceptions;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/19  16:03
 */
@Slf4j
public class PointerException extends RuntimeException {
    public PointerException() {
    }

    public PointerException(String message) {
        super(message);
        log.error("PointerException ==> message : {}", message);
    }

    public PointerException(String message, Throwable cause) {
        super(message, cause);
        log.error("PointerException ==> message : {}, cause : {}", message, cause);
    }

    public PointerException(Throwable cause) {
        super(cause);
        log.error("PointerException ==> cause : {}", cause);
    }

    public PointerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        log.error("PointerException ==> message : {}, cause : {}, enableSuppression : {}, writableStackTrace : {}", message, cause, enableSuppression, writableStackTrace);
    }
}

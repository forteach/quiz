package com.forteach.quiz.exceptions;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/5  5:35
 */
@Slf4j
public class CustomException extends RuntimeException {
    public CustomException() {
    }

    public CustomException(String message) {
        super(message);
        log.error("CustomException ==> message : {}", message);
    }

    public CustomException(String message, Throwable cause) {
        super(message, cause);
        log.error("CustomException ==> message : {}, cause : {}", message, cause);
    }

    public CustomException(Throwable cause) {
        super(cause);
        log.error("CustomException ==> cause : {}", cause);
    }

    public CustomException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        log.error("CustomException ==> message : {}, cause : {}, enableSuppression : {}, writableStackTrace : {}", message, cause, enableSuppression, writableStackTrace);
    }
}

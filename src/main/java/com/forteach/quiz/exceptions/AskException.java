package com.forteach.quiz.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/3  14:24
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
public class AskException extends RuntimeException {

    public AskException() {
    }

    public AskException(String message) {
        super(message);
        log.error("AskException ==> message : {}", message);
    }

    public AskException(String message, Throwable cause) {
        super(message, cause);
        log.error("AskException ==> message : {}, cause : {}", message, cause);
    }

    public AskException(Throwable cause) {
        super(cause);
        log.error("AskException ==> cause : {}", cause);
    }

    public AskException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        log.error("AskException ==> message : {}, cause : {}, enableSuppression : {}, writableStackTrace : {}", message, cause, enableSuppression, writableStackTrace);
    }
}

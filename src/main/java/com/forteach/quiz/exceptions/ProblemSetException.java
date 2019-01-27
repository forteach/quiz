package com.forteach.quiz.exceptions;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 自定义更新异常
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/20  15:44
 */
@Slf4j
public class ProblemSetException extends RuntimeException {
    public ProblemSetException() {
    }

    public ProblemSetException(String message) {
        super(message);
        log.error("ProblemSetException ==> message : {}", message);
    }

    public ProblemSetException(String message, Throwable cause) {
        super(message, cause);
        log.error("ProblemSetException ==> message : {}, cause : {}", message, cause);
    }

    public ProblemSetException(Throwable cause) {
        super(cause);
        log.error("ProblemSetException ==> cause : {}", cause);
    }

    public ProblemSetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        log.error("ProblemSetException ==> message : {}, cause : {}, enableSuppression : {}, writableStackTrace : {}", message, cause, enableSuppression, writableStackTrace);
    }
}

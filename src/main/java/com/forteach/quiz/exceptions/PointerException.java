package com.forteach.quiz.exceptions;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/19  16:03
 */
public class PointerException extends RuntimeException {
    public PointerException() {
    }

    public PointerException(String message) {
        super(message);
    }

    public PointerException(String message, Throwable cause) {
        super(message, cause);
    }

    public PointerException(Throwable cause) {
        super(cause);
    }

    public PointerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

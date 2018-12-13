package com.forteach.quiz.exceptions;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/20  15:44
 */
public class ProblemSetException extends RuntimeException {
    public ProblemSetException() {
    }

    public ProblemSetException(String message) {
        super(message);
    }

    public ProblemSetException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProblemSetException(Throwable cause) {
        super(cause);
    }

    public ProblemSetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

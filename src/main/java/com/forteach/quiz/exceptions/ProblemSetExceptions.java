package com.forteach.quiz.exceptions;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/20  15:44
 */
public class ProblemSetExceptions extends RuntimeException {
    public ProblemSetExceptions() {
    }

    public ProblemSetExceptions(String message) {
        super(message);
    }

    public ProblemSetExceptions(String message, Throwable cause) {
        super(message, cause);
    }

    public ProblemSetExceptions(Throwable cause) {
        super(cause);
    }

    public ProblemSetExceptions(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

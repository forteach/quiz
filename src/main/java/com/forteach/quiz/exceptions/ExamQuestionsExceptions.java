package com.forteach.quiz.exceptions;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  0:03
 */
public class ExamQuestionsExceptions extends RuntimeException{


    public ExamQuestionsExceptions() {
    }

    public ExamQuestionsExceptions(String message) {
        super(message);
    }

    public ExamQuestionsExceptions(String message, Throwable cause) {
        super(message, cause);
    }

    public ExamQuestionsExceptions(Throwable cause) {
        super(cause);
    }

    public ExamQuestionsExceptions(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

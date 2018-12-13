package com.forteach.quiz.exceptions;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  0:03
 */
public class ExamQuestionsException extends RuntimeException {


    public ExamQuestionsException() {
    }

    public ExamQuestionsException(String message) {
        super(message);
    }

    public ExamQuestionsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExamQuestionsException(Throwable cause) {
        super(cause);
    }

    public ExamQuestionsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

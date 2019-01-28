package com.forteach.quiz.exceptions;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  0:03
 */
@Slf4j
public class ExamQuestionsException extends RuntimeException {


    public ExamQuestionsException() {
    }

    public ExamQuestionsException(String message) {
        super(message);
        log.error("ExamQuestionsException ==> message : {}", message);
    }

    public ExamQuestionsException(String message, Throwable cause) {
        super(message, cause);
        log.error("ExamQuestionsException ==> message : {}, cause : {}", message, cause);
    }

    public ExamQuestionsException(Throwable cause) {
        super(cause);
        log.error("ExamQuestionsException ==> cause : {}", cause);
    }

    public ExamQuestionsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        log.error("ExamQuestionsException ==> message : {}, cause : {}, enableSuppression : {}, writableStackTrace : {}", message, cause, enableSuppression, writableStackTrace);
    }
}

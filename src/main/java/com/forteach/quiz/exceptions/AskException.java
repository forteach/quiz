package com.forteach.quiz.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/3  14:24
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AskException extends RuntimeException {

    public AskException() {
    }

    public AskException(String message) {
        super(message);
    }

    public AskException(String message, Throwable cause) {
        super(message, cause);
    }

    public AskException(Throwable cause) {
        super(cause);
    }

    public AskException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

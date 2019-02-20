package com.forteach.quiz.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @Description:
 * @version: V1.0
 * @author: liu zhenming
 * @Email: 1119264845@qq.com
 * @Date: 2018-08-21 16:19
 */
public class ErrorResponse {
    private final String error;

    @JsonCreator
    public ErrorResponse(@JsonProperty("error") final String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}

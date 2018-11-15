package com.forteach.quiz.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  0:15
 */
@Data
public abstract class AbstractExam {

    @Id
    protected String id;

    protected Double score;

    /**
     * 创作老师
     */
    protected String teacherId;
}

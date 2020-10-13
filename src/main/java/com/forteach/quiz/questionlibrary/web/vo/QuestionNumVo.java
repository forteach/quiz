package com.forteach.quiz.questionlibrary.web.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ：zhang10092009@hotmail.com
 * @date ：Created in 2020/9/25 16:44
 * @description：习题统计
 * @modified By：
 * @version: V1.0
 */
@Data
public class QuestionNumVo implements Serializable {
    private String courseId;
    private Long questionNum;
}
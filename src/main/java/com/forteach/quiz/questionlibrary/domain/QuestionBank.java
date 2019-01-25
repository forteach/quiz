package com.forteach.quiz.questionlibrary.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Description: 提问问题结构
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/14  22:58
 */
@Data
@Document(collection = "questionBank")
public class QuestionBank {

    /**
     *
     */
    private String id;

    /**
     * 教师信息集合list
     */
    private List<String> teachers;


}

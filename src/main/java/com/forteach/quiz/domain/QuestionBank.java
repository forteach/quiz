package com.forteach.quiz.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/14  22:58
 */
@Data
@Document(collection = "questionBank")
public class QuestionBank {

    private String id;

    private List<String> teachers;


}

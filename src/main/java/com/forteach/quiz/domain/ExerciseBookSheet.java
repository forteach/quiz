package com.forteach.quiz.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/19  9:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "exerciseBookSheet")
public class ExerciseBookSheet extends BaseEntity {

    /**
     * 学生id
     */
    private String examineeId;

    /**
     * 练习册id
     */
    private String exerciseBookId;

    /**
     * 参与方式
     */
    private String participate;

    private List<Answ> answ;




}

package com.forteach.quiz.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Description: 练习册类型：1、预习练习册 2、课堂练习册3、课后作业册
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  16:35
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "exerciseBook")
public class ExerciseBook extends BaseEntity {

    protected int exeBookType;

    protected String teacherId;

    protected String exeBookName;

    protected List questionChildren;

    public ExerciseBook() {
    }

    public ExerciseBook(int exeBookType, String teacherId, String exeBookName, List questionChildren) {
        this.exeBookType = exeBookType;
        this.teacherId = teacherId;
        this.exeBookName = exeBookName;
        this.questionChildren = questionChildren;
    }
}

package com.forteach.quiz.problemsetlibrary.domain;

import com.forteach.quiz.problemsetlibrary.domain.base.ExerciseBook;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/13  21:24
 */
@EqualsAndHashCode(callSuper = true)
@Document(collection = "brainstormQuestionExerciseBook")
@Data
public class BrainstormQuestionExerciseBook extends ExerciseBook {
}

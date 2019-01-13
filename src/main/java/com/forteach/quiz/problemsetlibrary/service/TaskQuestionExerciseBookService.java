package com.forteach.quiz.problemsetlibrary.service;

import com.forteach.quiz.problemsetlibrary.domain.BigQuestionExerciseBook;
import com.forteach.quiz.problemsetlibrary.repository.base.ExerciseBookMongoRepository;
import com.forteach.quiz.problemsetlibrary.service.base.BaseExerciseBookServiceImpl;
import com.forteach.quiz.questionlibrary.domain.BigQuestion;
import com.forteach.quiz.questionlibrary.service.base.BaseQuestionServiceImpl;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/13  22:43
 */
@Service
public class TaskQuestionExerciseBookService extends BaseExerciseBookServiceImpl<BigQuestionExerciseBook, BigQuestion> {

    public TaskQuestionExerciseBookService(ExerciseBookMongoRepository<BigQuestionExerciseBook> repository,
                                           ReactiveMongoTemplate template,
                                           BaseQuestionServiceImpl<BigQuestion> questionRepository) {
        super(repository, template, questionRepository);
    }


}

package com.forteach.quiz.questionlibrary.service;

import com.forteach.quiz.questionlibrary.domain.TaskQuestion;
import com.forteach.quiz.questionlibrary.reflect.QuestionReflect;
import com.forteach.quiz.questionlibrary.repository.base.QuestionMongoRepository;
import com.forteach.quiz.questionlibrary.service.base.BaseQuestionServiceImpl;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/11  16:08
 */
@Service
public class TaskQuestionService extends BaseQuestionServiceImpl<TaskQuestion> {

    public TaskQuestionService(QuestionMongoRepository<TaskQuestion> repository,
                               ReactiveMongoTemplate reactiveMongoTemplate,
                               QuestionReflect questionReflect) {

        super(repository, reactiveMongoTemplate, questionReflect);
    }

}

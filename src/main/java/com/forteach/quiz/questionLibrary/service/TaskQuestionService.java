package com.forteach.quiz.questionLibrary.service;

import com.forteach.quiz.questionLibrary.domain.TaskQuestion;
import com.forteach.quiz.questionLibrary.reflect.QuestionReflect;
import com.forteach.quiz.questionLibrary.repository.base.QuestionMongoRepository;
import com.forteach.quiz.questionLibrary.service.base.BaseBaseQuestionServiceImpl;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/11  16:08
 */
@Service
public class TaskQuestionService extends BaseBaseQuestionServiceImpl<TaskQuestion> {

    public TaskQuestionService(QuestionMongoRepository<TaskQuestion> repository, KeywordService<TaskQuestion> keywordService,
                               ReactiveMongoTemplate reactiveMongoTemplate, QuestionReflect questionReflect) {

        super(repository, keywordService, reactiveMongoTemplate, questionReflect);
    }
}

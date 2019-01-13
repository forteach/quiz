package com.forteach.quiz.problemsetlibrary.service;

import com.forteach.quiz.problemsetlibrary.domain.TaskQuestionProblemSet;
import com.forteach.quiz.problemsetlibrary.repository.base.ProblemSetMongoRepository;
import com.forteach.quiz.problemsetlibrary.service.base.BaseProblemSetServiceImpl;
import com.forteach.quiz.questionlibrary.domain.TaskQuestion;
import com.forteach.quiz.questionlibrary.repository.base.QuestionMongoRepository;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/13  18:56
 */
@Service
public class TaskQuestionProblemSetService extends BaseProblemSetServiceImpl<TaskQuestionProblemSet, TaskQuestion> {

    public TaskQuestionProblemSetService(ReactiveMongoTemplate reactiveMongoTemplate,
                                         ProblemSetMongoRepository<TaskQuestionProblemSet> repository,
                                         QuestionMongoRepository<TaskQuestion> questionRepository) {
        super(reactiveMongoTemplate, repository, questionRepository);
    }
}

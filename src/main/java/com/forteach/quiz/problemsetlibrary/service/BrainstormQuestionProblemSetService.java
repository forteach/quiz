package com.forteach.quiz.problemsetlibrary.service;

import com.forteach.quiz.problemsetlibrary.domain.BrainstormQuestionProblemSet;
import com.forteach.quiz.problemsetlibrary.repository.BrainstormQuestionProblemSetRepository;
import com.forteach.quiz.problemsetlibrary.service.base.BaseProblemSetServiceImpl;
import com.forteach.quiz.questionlibrary.domain.BrainstormQuestion;
import com.forteach.quiz.questionlibrary.repository.base.QuestionMongoRepository;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/13  18:57
 */
@Service
public class BrainstormQuestionProblemSetService extends BaseProblemSetServiceImpl<BrainstormQuestionProblemSet, BrainstormQuestion> {


    public BrainstormQuestionProblemSetService(ReactiveMongoTemplate reactiveMongoTemplate,
                                               BrainstormQuestionProblemSetRepository repository,
                                               QuestionMongoRepository<BrainstormQuestion> questionRepository) {
        super(reactiveMongoTemplate, repository, questionRepository);
    }


}

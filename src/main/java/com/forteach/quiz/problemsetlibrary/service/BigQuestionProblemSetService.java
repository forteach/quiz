package com.forteach.quiz.problemsetlibrary.service;

import com.forteach.quiz.problemsetlibrary.domain.BigQuestionProblemSet;
import com.forteach.quiz.problemsetlibrary.repository.BigQuestionProblemSetRepository;
import com.forteach.quiz.problemsetlibrary.service.base.BaseProblemSetServiceImpl;
import com.forteach.quiz.questionlibrary.domain.BigQuestion;
import com.forteach.quiz.questionlibrary.repository.base.QuestionMongoRepository;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/11  16:18
 */
@Service
public class BigQuestionProblemSetService extends BaseProblemSetServiceImpl<BigQuestionProblemSet, BigQuestion> {

    public BigQuestionProblemSetService(ReactiveMongoTemplate reactiveMongoTemplate,
                                        BigQuestionProblemSetRepository repository,
                                        QuestionMongoRepository<BigQuestion> questionRepository) {
        super(reactiveMongoTemplate, repository, questionRepository);
    }
}

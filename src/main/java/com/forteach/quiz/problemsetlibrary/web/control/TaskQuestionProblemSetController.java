package com.forteach.quiz.problemsetlibrary.web.control;

import com.forteach.quiz.problemsetlibrary.domain.BrainstormQuestionExerciseBook;
import com.forteach.quiz.problemsetlibrary.domain.BrainstormQuestionProblemSet;
import com.forteach.quiz.problemsetlibrary.service.base.BaseExerciseBookService;
import com.forteach.quiz.problemsetlibrary.service.base.BaseProblemSetService;
import com.forteach.quiz.problemsetlibrary.web.control.base.BaseProblemSetController;
import com.forteach.quiz.questionlibrary.domain.BrainstormQuestion;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/13  23:00
 */
@Slf4j
@RestController
@Api(value = "任务库 练习册,题集相关", tags = {"练习册,题集相关操作"})
@RequestMapping(path = "/taskExerciseBook", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TaskQuestionProblemSetController extends BaseProblemSetController<BrainstormQuestionProblemSet, BrainstormQuestion, BrainstormQuestionExerciseBook> {

    public TaskQuestionProblemSetController(BaseProblemSetService<BrainstormQuestionProblemSet, BrainstormQuestion> service,
                                            BaseExerciseBookService<BrainstormQuestionExerciseBook, BrainstormQuestion> exerciseBookService) {
        super(service, exerciseBookService);
    }
}

package com.forteach.quiz.questionLibrary.web.control;

import com.forteach.quiz.questionLibrary.domain.SurveyQuestion;
import com.forteach.quiz.questionLibrary.service.base.BaseQuestionService;
import com.forteach.quiz.questionLibrary.web.control.base.BaseQuestionController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/11  16:29
 */
@Slf4j
@RestController
@Api(value = "调查的 题目", tags = {"调查的 题库内容操作"})
@RequestMapping(path = "/surveyQuestion", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class SurveyQuestionController extends BaseQuestionController<SurveyQuestion> {

    public SurveyQuestionController(BaseQuestionService<SurveyQuestion> service) {
        super(service);
    }
}

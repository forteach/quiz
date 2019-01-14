package com.forteach.quiz.questionlibrary.web.control;

import com.forteach.quiz.questionlibrary.domain.SurveyQuestion;
import com.forteach.quiz.questionlibrary.service.KeywordService;
import com.forteach.quiz.questionlibrary.service.base.BaseQuestionService;
import com.forteach.quiz.questionlibrary.web.control.base.BaseObjectiveController;
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
@Api(value = "问卷 题目", tags = {"问卷库 题库内容操作"})
@RequestMapping(path = "/surveyQuestion", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class SurveyQuestionController extends BaseObjectiveController<SurveyQuestion> {

    public SurveyQuestionController(BaseQuestionService<SurveyQuestion> service, KeywordService<SurveyQuestion> keywordService) {
        super(service, keywordService);
    }

}

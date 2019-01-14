package com.forteach.quiz.questionlibrary.web.control;

import com.forteach.quiz.questionlibrary.domain.TaskQuestion;
import com.forteach.quiz.questionlibrary.service.KeywordService;
import com.forteach.quiz.questionlibrary.service.base.BaseQuestionService;
import com.forteach.quiz.questionlibrary.web.control.base.BaseQuestionController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/11  16:06
 */
@Slf4j
@RestController
@Api(value = "任务库 题目", tags = {"任务库 题库内容操作"})
@RequestMapping(path = "/taskQuestion", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TaskQuestionController extends BaseQuestionController<TaskQuestion> {

    public TaskQuestionController(BaseQuestionService<TaskQuestion> service, KeywordService<TaskQuestion> keywordService) {
        super(service, keywordService);
    }
}

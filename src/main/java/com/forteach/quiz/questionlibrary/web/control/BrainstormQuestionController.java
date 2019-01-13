package com.forteach.quiz.questionlibrary.web.control;

import com.forteach.quiz.questionlibrary.domain.BrainstormQuestion;
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
 * @date: 2019/1/11  16:28
 */
@Slf4j
@RestController
@Api(value = "头脑风暴的问题 题目", tags = {"头脑风暴的问题 题库内容操作"})
@RequestMapping(path = "/brainstormQuestion", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class BrainstormQuestionController extends BaseQuestionController<BrainstormQuestion> {

    public BrainstormQuestionController(BaseQuestionService<BrainstormQuestion> service) {
        super(service);
    }
}
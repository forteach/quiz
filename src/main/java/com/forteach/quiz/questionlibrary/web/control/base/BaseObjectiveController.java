package com.forteach.quiz.questionlibrary.web.control.base;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.questionlibrary.domain.base.QuestionExamEntity;
import com.forteach.quiz.questionlibrary.domain.question.ChoiceQst;
import com.forteach.quiz.questionlibrary.domain.question.TrueOrFalse;
import com.forteach.quiz.questionlibrary.service.KeywordService;
import com.forteach.quiz.questionlibrary.service.base.BaseQuestionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/14  16:28
 */
public abstract class BaseObjectiveController <T extends QuestionExamEntity> extends BaseQuestionController<T>{

    public BaseObjectiveController(BaseQuestionService<T> service, KeywordService<T> keywordService) {
        super(service, keywordService);
    }

    /**
     * 编辑判断题
     *
     * @param trueOrFalse
     * @return BigQuestion
     */
    @PostMapping("/trueOrFalse/edit")
    @ApiOperation(value = "编辑判断题", notes = "新增数据时 不添加id 修改时数据添加id")
    public Mono<WebResult> editTrueOrFalse(@Valid @RequestBody T trueOrFalse) {
        return service.editQuestion(trueOrFalse, TrueOrFalse.class).map(WebResult::okResult);
    }

    /**
     * 编辑选择题
     *
     * @param bigQuestion
     * @return BigQuestion
     */
    @PostMapping("/choiceQst/edit")
    @ApiOperation(value = "编辑选择题", notes = "新增数据时 不添加id 修改时数据添加id")
    public Mono<WebResult> editChoiceQst(@Valid @RequestBody T bigQuestion) {
        return service.editQuestion(bigQuestion, ChoiceQst.class).map(WebResult::okResult);
    }

}

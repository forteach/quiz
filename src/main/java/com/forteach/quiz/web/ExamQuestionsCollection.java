package com.forteach.quiz.web;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.domain.BigQuestion;
import com.forteach.quiz.domain.ChoiceQst;
import com.forteach.quiz.domain.Design;
import com.forteach.quiz.domain.TrueOrFalse;
import com.forteach.quiz.service.ExamQuestionsService;
import com.forteach.quiz.web.vo.QuestionBankVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/13  10:43
 */
@Slf4j
@RestController
@RequestMapping(path = "/question", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ExamQuestionsCollection extends BaseController {

    private final ExamQuestionsService examQuestionsService;

    public ExamQuestionsCollection(ExamQuestionsService examQuestionsService) {
        this.examQuestionsService = examQuestionsService;
    }

    /**
     * 编辑简答思考题
     *
     * @param design
     * @return BigQuestion
     */
    @PostMapping("/design/edit")
    public Mono<WebResult> editDesign(@RequestBody BigQuestion<Design> design) {
        return examQuestionsService.editDesign(design).map(WebResult::okResult);
    }

    /**
     * 编辑判断题
     *
     * @param trueOrFalse
     * @return BigQuestion
     */
    @PostMapping("/trueOrFalse/edit")
    public Mono<WebResult> editTrueOrFalse(@RequestBody BigQuestion<TrueOrFalse> trueOrFalse) {
        return examQuestionsService.editTrueOrFalse(trueOrFalse).map(WebResult::okResult);
    }

    /**
     * 编辑选择题
     *
     * @param bigQuestion
     * @return BigQuestion
     */
    @PostMapping("/choiceQst/edit")
    public Mono<WebResult> editChoiceQst(@RequestBody BigQuestion<ChoiceQst> bigQuestion) {
        return examQuestionsService.editChoiceQst(bigQuestion).map(WebResult::okResult);
    }

    @GetMapping("/delete/{id}")
    public Mono<WebResult> delQuestions(@PathVariable String id) {
        return Mono.just(WebResult.okResult(examQuestionsService.delQuestions(id)));
    }


    @PostMapping("/association/add")
    public Mono<WebResult> associationAdd(@RequestBody QuestionBankVo questionBankVo) {
        return examQuestionsService.questionBankAssociationAdd(questionBankVo.getId(), questionBankVo.getTeacher()).map(WebResult::okResult);
    }

//    @PostMapping()
//    public Mono<WebResult>

}

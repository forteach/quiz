package com.forteach.quiz.web;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.domain.BigQuestion;
import com.forteach.quiz.domain.ChoiceQst;
import com.forteach.quiz.domain.Design;
import com.forteach.quiz.domain.TrueOrFalse;
import com.forteach.quiz.service.ExamQuestionsService;
import com.forteach.quiz.web.vo.QuestionBankVo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/13  10:43
 */
@Slf4j
@RestController
@Api(value = "题目", tags = {"题库内容操作"})
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "relate", value = "此操作是否修改到快照", dataType = "string", required = true, defaultValue = "0"),
            @ApiImplicitParam(name = "examChildren", value = "保存,修改的简答题对象", dataType = "json", required = true),
    })
    @ApiOperation(value = "编辑简答思考题", notes = "新增数据时 不添加id 修改时数据添加id")
    public Mono<WebResult> editDesign(@Valid @RequestBody @ApiParam(value = "简答思考题", required = true) BigQuestion<Design> design) {
        return examQuestionsService.editDesign(design).map(WebResult::okResult);
    }

    /**
     * 编辑判断题
     *
     * @param trueOrFalse
     * @return BigQuestion
     */
    @PostMapping("/trueOrFalse/edit")
    @ApiOperation(value = "编辑判断题", notes = "新增数据时 不添加id 修改时数据添加id")
    public Mono<WebResult> editTrueOrFalse(@Valid @RequestBody BigQuestion<TrueOrFalse> trueOrFalse) {
        return examQuestionsService.editTrueOrFalse(trueOrFalse).map(WebResult::okResult);
    }

    /**
     * 编辑选择题
     *
     * @param bigQuestion
     * @return BigQuestion
     */
    @PostMapping("/choiceQst/edit")
    @ApiOperation(value = "编辑选择题", notes = "新增数据时 不添加id 修改时数据添加id")
    public Mono<WebResult> editChoiceQst(@Valid @RequestBody BigQuestion<ChoiceQst> bigQuestion) {
        return examQuestionsService.editChoiceQst(bigQuestion).map(WebResult::okResult);
    }

    @GetMapping("/delete/{id}")
    @ApiOperation(value = "删除题目", notes = "新增数据时 不添加id 修改时数据添加id")
    public Mono<WebResult> delQuestions(@Valid @PathVariable String id) {
        return Mono.just(WebResult.okResult(examQuestionsService.delQuestions(id)));
    }


    @PostMapping("/association/add")
    @ApiOperation(value = "题目分享", notes = "传入被分享教师与题目id")
    public Mono<WebResult> associationAdd(@Valid @RequestBody QuestionBankVo questionBankVo) {
        return examQuestionsService.questionBankAssociationAdd(questionBankVo.getId(), questionBankVo.getTeacher()).map(WebResult::okResult);
    }

}

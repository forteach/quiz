package com.forteach.quiz.problemsetlibrary.web.control;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.problemsetlibrary.domain.BigQuestionExerciseBook;
import com.forteach.quiz.problemsetlibrary.domain.BigQuestionProblemSet;
import com.forteach.quiz.problemsetlibrary.service.BigQuestionExerciseBookService;
import com.forteach.quiz.problemsetlibrary.service.base.BaseProblemSetService;
import com.forteach.quiz.problemsetlibrary.web.control.base.BaseProblemSetController;
import com.forteach.quiz.questionlibrary.domain.BigQuestion;
import com.forteach.quiz.web.vo.PreviewChangeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/13  23:01
 */
@Slf4j
@RestController
@Api(value = "考题库 练习册,题集相关", tags = {"练习册,题集相关操作"})
@RequestMapping(path = "/exerciseBook", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class BigQuestionProblemSetController extends BaseProblemSetController<BigQuestionProblemSet, BigQuestion, BigQuestionExerciseBook> {

    private final BigQuestionExerciseBookService exerciseBookService;

    public BigQuestionProblemSetController(BaseProblemSetService<BigQuestionProblemSet, BigQuestion> service,
                                           BigQuestionExerciseBookService exerciseBookService,
                                           BigQuestionExerciseBookService exerciseBookService1) {

        super(service, exerciseBookService);
        this.exerciseBookService = exerciseBookService1;
    }

    /**
     * 编辑练习册 预习类型
     *
     * @return
     */
    @ApiOperation(value = "编辑练习册 预习类型", notes = "编辑练习册 预习类型")
    @PostMapping("/edit/preview")
    public Mono<WebResult> editPreview(@Valid @ApiParam(name = "delVo", value = "通过题目id与挂接信息进行编辑练习册", required = true) @RequestBody PreviewChangeVo changeVo) {
        return exerciseBookService.editPreview(changeVo).map(WebResult::okResult);
    }


}

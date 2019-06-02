package com.forteach.quiz.problemsetlibrary.web.control;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.problemsetlibrary.domain.BigQuestionExerciseBook;
import com.forteach.quiz.problemsetlibrary.domain.BigQuestionProblemSet;
import com.forteach.quiz.problemsetlibrary.service.BigQuestionExerciseBookService;
import com.forteach.quiz.problemsetlibrary.service.base.BaseProblemSetService;
import com.forteach.quiz.problemsetlibrary.web.control.base.BaseProblemSetController;
import com.forteach.quiz.questionlibrary.domain.BigQuestion;
import com.forteach.quiz.service.TokenService;
import com.forteach.quiz.web.vo.PreviewChangeVo;
import io.swagger.annotations.*;
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
@Api(value = "考题库 练习册,题集相关", tags = {"考题库 练习册,题集相关操作"})
@RequestMapping(path = "/exerciseBook", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class BigQuestionProblemSetController extends BaseProblemSetController<BigQuestionProblemSet, BigQuestion, BigQuestionExerciseBook> {

    private final BigQuestionExerciseBookService exerciseBookService;

    public BigQuestionProblemSetController(BaseProblemSetService<BigQuestionProblemSet, BigQuestion> service,
                                           BigQuestionExerciseBookService exerciseBookService, TokenService tokenService) {

        super(service, exerciseBookService, tokenService);
        this.exerciseBookService = exerciseBookService;
    }

    /**
     * 编辑练习册 预习类型
     *
     * @return
     */
    @ApiOperation(value = "编辑练习册 预习类型", notes = "编辑练习册 预习类型")
    @PostMapping("/edit/preview")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "课堂练习  before/预习 now/课堂 before,now/全部", name = "preview", dataType = "string", example = "1", paramType = "form"),
            @ApiImplicitParam(value = "章节id", name = "chapterId", example = "章节id", dataType = "string", paramType = "form"),
            @ApiImplicitParam(value = "课程id", name = "courseId", example = "章节id", dataType = "string", paramType = "form"),
            @ApiImplicitParam(value = "需要修改的题目id", name = "targetId", example = "需要修改的题目id ... ", paramType = "form")
    })
    public Mono<WebResult> editPreview(@Valid @ApiParam(name = "delVo", value = "通过题目id与挂接信息进行编辑练习册", required = true) @RequestBody PreviewChangeVo changeVo) {
        return exerciseBookService.editPreview(changeVo).map(WebResult::okResult);
    }


}

package com.forteach.quiz.web;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.domain.ExerciseBookSheet;
import com.forteach.quiz.service.ProblemSetService;
import com.forteach.quiz.web.vo.ExerciseBookAttributeVo;
import com.forteach.quiz.web.vo.ExerciseBookQuestionVo;
import com.forteach.quiz.web.vo.ExerciseBookVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/19  10:21
 */
@Slf4j
@RestController
@RequestMapping(path = "/exerciseBook", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ExerciseBookCollection extends BaseController {

    private final ProblemSetService problemSetService;

    public ExerciseBookCollection(ProblemSetService problemSetService) {
        this.problemSetService = problemSetService;
    }

    /**
     * 选择大题编辑练习册
     *
     * @param exerciseBookVo
     * @return
     */
    @PostMapping("/build")
    public Mono<WebResult> buildExerciseBook(@RequestBody ExerciseBookVo exerciseBookVo) {
        return problemSetService.buildExerciseBook(exerciseBookVo).map(WebResult::okResult);
    }

    /**
     * 修改练习册属性
     *
     * @param exerciseBookAttributeVo
     * @return
     */
    @PostMapping("/edit/Attribute")
    public Mono<WebResult> editexerciseBookAttribute(@RequestBody ExerciseBookAttributeVo exerciseBookAttributeVo) {
        return problemSetService.editexerciseBookAttribute(exerciseBookAttributeVo).map(WebResult::okResult);
    }

    /**
     * 通过id查找练习册
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Mono<WebResult> getExerciseBook(@PathVariable String id) {
        return problemSetService.getExerciseBook(id).map(WebResult::okResult);
    }

    /**
     * 修改答案
     *
     * @param exerciseBookSheet
     * @return
     */
    @PostMapping("/edit/answ")
    public Mono<WebResult> editExerciseBookSheet(@RequestBody ExerciseBookSheet exerciseBookSheet) {
        return problemSetService.editExerciseBookSheet(exerciseBookSheet).map(WebResult::okResult);
    }

    @GetMapping("/delete/{id}")
    public Mono<WebResult> delExerciseBook(@PathVariable String id) {
        return Mono.just(WebResult.okResult(problemSetService.delExerciseBook(id)));
    }

    @PostMapping("/edit/questions")
    public Mono<WebResult> changeExerciseBookQuestions(@RequestBody ExerciseBookQuestionVo exerciseBookQuestionVo) {
        return problemSetService.changeExerciseBookQuestions(exerciseBookQuestionVo).map(WebResult::okResult);
    }

}

package com.forteach.quiz.web;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.service.ProblemSetService;
import com.forteach.quiz.web.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * @Description: 练习册相关
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
     * @param exerciseBookSheetVo
     * @return
     */
    @PostMapping("/edit/answ")
    public Mono<WebResult> editExerciseBookSheet(@RequestBody ExerciseBookSheetVo exerciseBookSheetVo) {
        return problemSetService.editExerciseBookSheet(exerciseBookSheetVo).map(WebResult::okResult);
    }

    /**
     * 提交答案
     *
     * @param exerciseBookSheetVo
     * @return
     */
    @PostMapping("/commit/answ")
    public Mono<WebResult> commitExerciseBookSheet(@RequestBody ExerciseBookSheetVo exerciseBookSheetVo) {
        return problemSetService.commitExerciseBookSheet(exerciseBookSheetVo).map(WebResult::okResult);
    }

    /**
     * 提交练习册 主观题 批改
     *
     * @param exerciseBookSheetVo
     * @return
     */
    @PostMapping("/correct/subjective")
    public Mono<WebResult> correctExerciseBookSheet(@RequestBody ExerciseBookSheetVo exerciseBookSheetVo) {
        return problemSetService.correctExerciseBookSheet(exerciseBookSheetVo).map(WebResult::okResult);
    }

    /**
     * 删除练习册
     *
     * @param id
     * @return
     */
    @GetMapping("/delete/{id}")
    public Mono<WebResult> delExerciseBook(@PathVariable String id) {
        return Mono.just(WebResult.okResult(problemSetService.delExerciseBook(id)));
    }

    /**
     * 生成作业册
     *
     * @param problemSetBackupVo
     * @return
     */
    @PostMapping("/edit/homework")
    public Mono<WebResult> editProblemSetBackup(@RequestBody ProblemSetBackupVo problemSetBackupVo) {
        return problemSetService.editProblemSetBackup(problemSetBackupVo).map(WebResult::okResult);
    }

    /**
     * 更改练习册的题目 并修改到题库中
     *
     * @param exerciseBookQuestionVo
     * @return
     */
    @PostMapping("/edit/questions")
    public Mono<WebResult> changeExerciseBookQuestions(@RequestBody ExerciseBookQuestionVo exerciseBookQuestionVo) {
        return problemSetService.changeExerciseBookQuestions(exerciseBookQuestionVo).map(WebResult::okResult);
    }

}

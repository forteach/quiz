package com.forteach.quiz.problemsetlibrary.web.control.base;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.problemsetlibrary.domain.base.ExerciseBook;
import com.forteach.quiz.problemsetlibrary.domain.base.ProblemSet;
import com.forteach.quiz.problemsetlibrary.service.base.BaseExerciseBookService;
import com.forteach.quiz.problemsetlibrary.service.base.BaseProblemSetService;
import com.forteach.quiz.problemsetlibrary.web.req.ExerciseBookReq;
import com.forteach.quiz.problemsetlibrary.web.req.ProblemSetReq;
import com.forteach.quiz.problemsetlibrary.web.vo.ProblemSetVo;
import com.forteach.quiz.questionlibrary.domain.base.QuestionExamEntity;
import com.forteach.quiz.web.vo.DelExerciseBookPartVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/13  21:14
 */
public abstract class BaseProblemSetController<T extends ProblemSet, R extends QuestionExamEntity, E extends ExerciseBook> {

    protected final BaseExerciseBookService<E, R> exerciseBookService;
    private final BaseProblemSetService<T, R> service;

    public BaseProblemSetController(BaseProblemSetService<T, R> service,
                                    BaseExerciseBookService<E, R> exerciseBookService) {
        this.service = service;
        this.exerciseBookService = exerciseBookService;
    }

    /**
     * 编辑题集
     *
     * @param problemSet
     * @return
     */
    @PostMapping("/build")
    @ApiOperation(value = "编辑题集", notes = "修改或增加题集")
    public Mono<WebResult> buildExerciseBook(@Valid @RequestBody T problemSet) {
        return service.buildExerciseBook(problemSet).map(WebResult::okResult);
    }

    /**
     * 删除题集
     *
     * @param id
     * @return
     */
    @GetMapping("/delete/{id}")
    @ApiOperation(value = "删除题集", notes = "通过id 删除题集")
    public Mono<WebResult> delExerciseBook(@Valid @PathVariable String id) {
        return service.delExerciseBook(id).map(WebResult::okResult);
    }

    /**
     * 通过id查找题集
     *
     * @param id
     * @return
     */
    @GetMapping("/findOne/{id}")
    @ApiOperation(value = "查找题集详细信息", notes = "通过id查找题集,仅展示包括的题目id")
    public Mono<WebResult> findOne(@Valid @PathVariable String id) {
        return service.findOne(id).map(WebResult::okResult);
    }

    /**
     * 通过id查找题集及包含的题目全部信息
     */
    @GetMapping("/findDetailed/{id}")
    @ApiOperation(value = "通过id查找题集及包含的题目全部信息", notes = "通过id查找题集及包含的题目全部信息")
    public Mono<WebResult> findAllDetailed(@Valid @PathVariable String id) {
        return service.findAllDetailed(id).map(WebResult::okResult);
    }

    /**
     * 挂接课堂练习题目
     *
     * @param problemSetVo
     * @return
     */
    @PostMapping("/generate/practice")
    @ApiOperation(value = "生成挂接课堂练习题", notes = "根据id集,生成挂接课堂练习题 传入id修改,不传入id 新增")
    public Mono<WebResult> buildExerciseBook(@Valid @RequestBody ProblemSetVo problemSetVo) {
        return exerciseBookService.buildBook(problemSetVo).map(WebResult::okResult);
    }

    /**
     * 查询题集分页信息
     *
     * @param sortVo
     * @return
     */
    @ApiOperation(value = "题集 分页信息", notes = "查询题集分页信息")
    @PostMapping("/findAll/detailed")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页从0开始", required = true, dataType = "int", type = "int", example = "0"),
            @ApiImplicitParam(name = "size", value = "每页数量", required = true, dataType = "int", type = "int", example = "10"),
            @ApiImplicitParam(value = "排序规则", dataType = "string", name = "sorting", example = "cTime", required = true),
            @ApiImplicitParam(value = "sort", name = "排序方式", dataType = "int", example = "1")
    })
    public Mono<WebResult> findProblemSet(@Valid @ApiParam(name = "sortVo", value = "题目分页查询", required = true) @RequestBody ProblemSetReq sortVo) {
        return service.findProblemSet(sortVo).collectList().map(WebResult::okResult);
    }

    /**
     * 查找挂接的课堂练习题
     *
     * @param bookReq
     * @return
     */
    @ApiOperation(value = "查找挂接的课堂练习题", notes = "查找挂接的课堂练习题")
    @PostMapping("/findExerciseBook")
    public Mono<WebResult> findExerciseBook(@Valid @ApiParam(name = "ExerciseBookReq", value = "查找挂接的课堂练习题", required = true) @RequestBody ExerciseBookReq bookReq) {
        return exerciseBookService.findExerciseBook(bookReq).map(WebResult::okResult);
    }

    /**
     * 删除挂接的课堂练习题
     *
     * @return
     */
    @ApiOperation(value = "删除课堂练习题部分", notes = "删除课堂练习题部分")
    @PostMapping("/delete/exerciseBookPart")
    public Mono<WebResult> delExerciseBookPart(@Valid @ApiParam(name = "delVo", value = "通过题目id与挂接信息进行解除", required = true) @RequestBody DelExerciseBookPartVo delVo) {
        return exerciseBookService.delExerciseBookPart(delVo).map(WebResult::okResult);
    }

}

package com.forteach.quiz.web;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.domain.ProblemSet;
import com.forteach.quiz.service.ExerciseBookService;
import com.forteach.quiz.service.ProblemSetService;
import com.forteach.quiz.web.req.ExerciseBookReq;
import com.forteach.quiz.web.req.ProblemSetReq;
import com.forteach.quiz.web.vo.DelExerciseBookPartVo;
import com.forteach.quiz.web.vo.ExerciseBookVo;
import com.forteach.quiz.web.vo.PreviewChangeVo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * @Description: 练习册相关
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/19  10:21
 */
@Slf4j
@RestController
@Api(value = "练习册,题集相关", tags = {"练习册,题集相关操作"})
@RequestMapping(path = "/exerciseBook", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ExerciseBookCollection extends BaseController {

    private final ProblemSetService problemSetService;

    private final ExerciseBookService exerciseBookService;

    public ExerciseBookCollection(ProblemSetService problemSetService, ExerciseBookService exerciseBookService) {
        this.problemSetService = problemSetService;
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
    public Mono<WebResult> buildExerciseBook(@Valid @RequestBody ProblemSet problemSet) {
        return problemSetService.buildExerciseBook(problemSet).map(WebResult::okResult);
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
        return problemSetService.delExerciseBook(id).map(WebResult::okResult);
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
        return problemSetService.findOne(id).map(WebResult::okResult);
    }

    /**
     * 通过id查找题集及包含的题目全部信息
     */
    @GetMapping("/findDetailed/{id}")
    @ApiOperation(value = "通过id查找题集及包含的题目全部信息", notes = "通过id查找题集及包含的题目全部信息")
    public Mono<WebResult> findAllDetailed(@Valid @PathVariable String id) {
        return problemSetService.findAllDetailed(id).map(WebResult::okResult);
    }

    /**
     * 挂接课堂练习题目
     *
     * @param exerciseBookVo
     * @return
     */
    @PostMapping("/generate/practice")
    @ApiOperation(value = "生成挂接课堂练习题", notes = "根据id集,生成挂接课堂练习题 传入id修改,不传入id 新增")
    public Mono<WebResult> buildExerciseBook(@Valid @RequestBody ExerciseBookVo exerciseBookVo) {
        return exerciseBookService.buildBook(exerciseBookVo).map(WebResult::okResult);
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
        return problemSetService.findProblemSet(sortVo).collectList().map(WebResult::okResult);
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


//    /**
//     * 修改答案
//     *
//     * @param exerciseBookSheetVo
//     * @return
//     */
//    @PostMapping("/edit/answ")
//    public Mono<WebResult> editExerciseBookSheet(@RequestBody ExerciseBookSheetVo exerciseBookSheetVo) {
//        return problemSetService.editExerciseBookSheet(exerciseBookSheetVo).map(WebResult::okResult);
//    }

//    /**
//     * 提交答案
//     *
//     * @param exerciseBookSheetVo
//     * @return
//     */
//    @PostMapping("/commit/answ")
//    public Mono<WebResult> commitExerciseBookSheet(@RequestBody ExerciseBookSheetVo exerciseBookSheetVo) {
//        return problemSetService.commitExerciseBookSheet(exerciseBookSheetVo).map(WebResult::okResult);
//    }
//
//    /**
//     * 提交练习册 主观题 批改
//     *
//     * @param exerciseBookSheetVo
//     * @return
//     */
//    @PostMapping("/correct/subjective")
//    public Mono<WebResult> correctExerciseBookSheet(@RequestBody ExerciseBookSheetVo exerciseBookSheetVo) {
//        return problemSetService.correctExerciseBookSheet(exerciseBookSheetVo).map(WebResult::okResult);
//    }

//    @PostMapping("/rewrite")
//    public Mono<WebResult> associationAdd(@RequestBody RewriteVo rewriteVo) {
//        return problemSetService.sheetRewrite(rewriteVo).map(WebResult::okResult);
//    }

}
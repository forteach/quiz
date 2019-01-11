package com.forteach.quiz.questionlibrary.web.control;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.questionlibrary.domain.BigQuestion;
import com.forteach.quiz.questionlibrary.service.BigQuestionService;
import com.forteach.quiz.questionlibrary.service.base.BaseQuestionService;
import com.forteach.quiz.questionlibrary.web.control.base.BaseQuestionController;
import com.forteach.quiz.web.req.QuestionProblemSetReq;
import com.forteach.quiz.web.vo.AddChildrenVo;
import com.forteach.quiz.web.vo.UpdateChildrenVo;
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
@Api(value = "考试 练习 题目", tags = {"题库内容操作"})
@RequestMapping(path = "/question", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class BigQuestionsController extends BaseQuestionController<BigQuestion> {

    private final BigQuestionService bigQuestionService;

    public BigQuestionsController(BaseQuestionService<BigQuestion> service, BigQuestionService bigQuestionService) {
        super(service);
        this.bigQuestionService = bigQuestionService;
    }

    /**
     * 编辑大题
     *
     * @param bigQuestion
     * @return BigQuestion
     */
    @PostMapping("/bigQuestion/edit")
    @ApiOperation(value = "编辑大题", notes = "新增数据时 不添加id 修改时数据添加id")
    public Mono<WebResult> editBigQuestion(@Valid @RequestBody @ApiParam(value = "编辑大题", required = true) BigQuestion bigQuestion) {
        return bigQuestionService.editBigQuestion(bigQuestion).map(WebResult::okResult);
    }

    /**
     * 获得题集 返回 分页的题库信息与 练习册已选择的题目信息
     *
     * @return
     */
    @PostMapping("/findAll/problemSet")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页从0开始", required = true, dataType = "int", type = "int", example = "0"),
            @ApiImplicitParam(name = "size", value = "每页数量", required = true, dataType = "int", type = "int", example = "10"),
            @ApiImplicitParam(value = "排序规则", dataType = "string", name = "sorting", example = "cTime", required = true),
            @ApiImplicitParam(value = "sort", name = "排序方式", dataType = "int", example = "1")
    })
    @ApiOperation(value = "通过id查找题集及包含的题目全部信息", notes = "通过id查找题集及包含的题目全部信息")
    public Mono<WebResult> questionProblemSetReq(@Valid @RequestBody QuestionProblemSetReq questionProblemSetReq) {
        return bigQuestionService.questionProblemSet(questionProblemSetReq).map(WebResult::okResult);
    }

    @ApiOperation(value = "删除大体下某题目", notes = "删除大体下某题目")
    @PostMapping("/bigQuestion/partRemove/{id}")
    public Mono<WebResult> questionPartDetailed(@Valid @ApiParam(name = "根据大体下子项id删除", value = "根据大体下子项id删除", required = true) @PathVariable String id) {
        return bigQuestionService.deleteChildren(id).map(WebResult::okResult);
    }

    /**
     * 修改大体下某题目
     *
     * @return
     */
    @PostMapping("/bigQuestion/partUpdate")
    @ApiOperation(value = "修改大体下某题目", notes = "修改大体下某题目")
    public Mono<WebResult> questionPartUpdate(@Valid @RequestBody UpdateChildrenVo updateChildrenVo) {
        return bigQuestionService.updateChildren(updateChildrenVo.getChildrenId(), updateChildrenVo.getJson(), updateChildrenVo.getTeacherId()).map(WebResult::okResult);
    }

    /**
     * 增加大体下子项
     *
     * @return
     */
    @PostMapping("/bigQuestion/partAdd")
    @ApiOperation(value = "增加大体下子项", notes = "增加大体下子项")
    public Mono<WebResult> questionPartAdd(@Valid @RequestBody AddChildrenVo addChildrenVo) {
        return bigQuestionService.addChildren(addChildrenVo.getQuestionId(), addChildrenVo.getJson(), addChildrenVo.getTeacherId()).map(WebResult::okResult);
    }

}

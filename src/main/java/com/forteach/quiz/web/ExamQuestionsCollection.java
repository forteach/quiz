package com.forteach.quiz.web;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.domain.BigQuestion;
import com.forteach.quiz.domain.ChoiceQst;
import com.forteach.quiz.domain.Design;
import com.forteach.quiz.domain.TrueOrFalse;
import com.forteach.quiz.service.ExamQuestionsService;
import com.forteach.quiz.web.req.QuestionBankReq;
import com.forteach.quiz.web.req.QuestionProblemSetReq;
import com.forteach.quiz.web.vo.AddChildrenVo;
import com.forteach.quiz.web.vo.QuestionBankVo;
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
@Api(value = "题目", tags = {"题库内容操作"})
@RequestMapping(path = "/question", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ExamQuestionsCollection extends BaseController {

    private final ExamQuestionsService examQuestionsService;

    public ExamQuestionsCollection(ExamQuestionsService examQuestionsService) {
        this.examQuestionsService = examQuestionsService;
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
        return examQuestionsService.editBigQuestion(bigQuestion).map(WebResult::okResult);
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
        return examQuestionsService.delQuestions(id).map(WebResult::okResult);
    }


    @PostMapping("/association/add")
    @ApiOperation(value = "题目分享", notes = "传入被分享教师与题目id")
    public Mono<WebResult> associationAdd(@Valid @RequestBody QuestionBankVo questionBankVo) {
        return examQuestionsService.questionBankAssociationAdd(questionBankVo.getId(), questionBankVo.getTeacher()).map(WebResult::okResult);
    }

    @ApiOperation(value = "题目详细 分页信息", notes = "分页查询题目 详细")
    @PostMapping("/findAll/detailed")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页从0开始", required = true, dataType = "int", type = "int", example = "0"),
            @ApiImplicitParam(name = "size", value = "每页数量", required = true, dataType = "int", type = "int", example = "10"),
            @ApiImplicitParam(value = "排序规则", dataType = "string", name = "sorting", example = "cTime", required = true),
            @ApiImplicitParam(value = "sort", name = "排序方式", dataType = "int", example = "1")
    })
    public Mono<WebResult> findAllDetailed(@Valid @ApiParam(name = "sortVo", value = "题目分页查询", required = true) @RequestBody QuestionBankReq sortVo) {
        return examQuestionsService.findAllDetailed(sortVo).collectList().map(WebResult::okResult);
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
        return examQuestionsService.questionProblemSet(questionProblemSetReq).map(WebResult::okResult);
    }

    @ApiOperation(value = "根据id获取题目详细", notes = "详细")
    @PostMapping("/findOne/{id}")
    public Mono<WebResult> findAllDetailed(@Valid @ApiParam(name = "根据BigQuestionId查出详细信息", value = "根据id查出详细信息", required = true) @PathVariable String id) {
        return examQuestionsService.findOneDetailed(id).map(WebResult::okResult);
    }


    @ApiOperation(value = "删除大体下某题目", notes = "删除大体下某题目")
    @PostMapping("/bigQuestion/partRemove/{id}")
    public Mono<WebResult> questionPartDetailed(@Valid @ApiParam(name = "根据大体下子项id删除", value = "根据大体下子项id删除", required = true) @PathVariable String id) {
        return examQuestionsService.deleteChildren(id).map(WebResult::okResult);
    }

    /**
     * 修改大体下某题目
     *
     * @return
     */
    @PostMapping("/bigQuestion/partUpdate")
    @ApiOperation(value = "修改大体下某题目", notes = "修改大体下某题目")
    public Mono<WebResult> questionPartUpdate(@Valid @RequestBody UpdateChildrenVo updateChildrenVo) {
        return examQuestionsService.updateChildren(updateChildrenVo.getChildrenId(), updateChildrenVo.getJson(), updateChildrenVo.getTeacherId()).map(WebResult::okResult);
    }

    /**
     * 增加大体下子项
     *
     * @return
     */
    @PostMapping("/bigQuestion/partAdd")
    @ApiOperation(value = "增加大体下子项", notes = "增加大体下子项")
    public Mono<WebResult> questionPartAdd(@Valid @RequestBody AddChildrenVo addChildrenVo) {
        return examQuestionsService.addChildren(addChildrenVo.getQuestionId(), addChildrenVo.getJson(), addChildrenVo.getTeacherId()).map(WebResult::okResult);
    }

}

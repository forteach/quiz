package com.forteach.quiz.questionlibrary.web.control.base;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.questionlibrary.domain.base.QuestionExamEntity;
import com.forteach.quiz.questionlibrary.domain.question.ChoiceQst;
import com.forteach.quiz.questionlibrary.domain.question.Design;
import com.forteach.quiz.questionlibrary.domain.question.TrueOrFalse;
import com.forteach.quiz.questionlibrary.service.KeywordService;
import com.forteach.quiz.questionlibrary.service.base.BaseQuestionService;
import com.forteach.quiz.questionlibrary.web.req.QuestionBankReq;
import com.forteach.quiz.questionlibrary.web.vo.QuestionBankVo;
import com.forteach.quiz.web.vo.KeywordIncreaseVo;
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
 * @Description: 题目结构的通用Controller
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/11  14:05
 */
public abstract class BaseQuestionController<T extends QuestionExamEntity> {

    private final BaseQuestionService<T> service;

    private final KeywordService<T> keywordService;

    public BaseQuestionController(BaseQuestionService<T> service, KeywordService<T> keywordService) {
        this.service = service;
        this.keywordService = keywordService;
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
    public Mono<WebResult> editDesign(@Valid @RequestBody @ApiParam(value = "简答思考题", required = true) T design) {
        return service.editQuestion(design, Design.class).map(WebResult::okResult);
    }

    /**
     * 编辑判断题
     *
     * @param trueOrFalse
     * @return BigQuestion
     */
    @PostMapping("/trueOrFalse/edit")
    @ApiOperation(value = "编辑判断题", notes = "新增数据时 不添加id 修改时数据添加id")
    public Mono<WebResult> editTrueOrFalse(@Valid @RequestBody T trueOrFalse) {
        return service.editQuestion(trueOrFalse, TrueOrFalse.class).map(WebResult::okResult);
    }


    /**
     * 编辑选择题
     *
     * @param bigQuestion
     * @return BigQuestion
     */
    @PostMapping("/choiceQst/edit")
    @ApiOperation(value = "编辑选择题", notes = "新增数据时 不添加id 修改时数据添加id")
    public Mono<WebResult> editChoiceQst(@Valid @RequestBody T bigQuestion) {
        return service.editQuestion(bigQuestion, ChoiceQst.class).map(WebResult::okResult);
    }

    @GetMapping("/delete/{id}")
    @ApiOperation(value = "删除题目", notes = "新增数据时 不添加id 修改时数据添加id")
    public Mono<WebResult> delQuestions(@Valid @PathVariable String id) {
        return service.delQuestions(id).map(WebResult::okResult);
    }


    @PostMapping("/association/add")
    @ApiOperation(value = "题目分享", notes = "传入被分享教师与题目id")
    public Mono<WebResult> associationAdd(@Valid @RequestBody QuestionBankVo questionBankVo) {
        return service.questionBankAssociationAdd(questionBankVo.getId(), questionBankVo.getTeacher()).map(WebResult::okResult);
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
        return service.findAllDetailed(sortVo).collectList().map(WebResult::okResult);
    }

    @ApiOperation(value = "根据id获取题目详细", notes = "详细")
    @PostMapping("/findOne/{id}")
    public Mono<WebResult> findAllDetailed(@Valid @ApiParam(name = "根据BigQuestionId查出详细信息", value = "根据id查出详细信息", required = true) @PathVariable String id) {
        return service.findOneDetailed(id).map(WebResult::okResult);
    }

    /**
     * 增加关键字关联关系
     *
     * @return
     */
    @ApiOperation(value = "增加关键字关联关系", notes = "增加关键字关联关系")
    @PostMapping("/keyword/increase")
    public Mono<WebResult> increase(@ApiParam(value = "增加关键字关联关系", required = true) @RequestBody KeywordIncreaseVo increaseVo) {
        return keywordService.increase(increaseVo.getValue(), increaseVo.getBigQuestionId()).map(WebResult::okResult);
    }

    /**
     * 删除关键字
     *
     * @return
     */
    @ApiOperation(value = "删除关键字", notes = "删除关键字")
    @PostMapping("/keyword/undock")
    public Mono<WebResult> undock(@ApiParam(value = "删除关键字", required = true) @RequestBody KeywordIncreaseVo increaseVo) {
        return keywordService.undock(increaseVo.getValue(), increaseVo.getBigQuestionId()).map(WebResult::okResult);
    }

    /**
     * 查询关键字下id问题
     *
     * @return
     */
    @ApiOperation(value = "查询关键字下问题id", notes = "查询关键字下问题id")
    @PostMapping("/keyword/associated")
    public Mono<WebResult> associated(@ApiParam(value = "查询知识点下问题id", required = true) @RequestBody KeywordIncreaseVo increaseVo) {
        return keywordService.keywordQuestion(increaseVo.getValue()).collectList().map(WebResult::okResult);
    }


}

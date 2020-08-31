package com.forteach.quiz.questionlibrary.web.control.base;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.questionlibrary.domain.base.QuestionExamEntity;
import com.forteach.quiz.questionlibrary.domain.question.ChoiceQst;
import com.forteach.quiz.questionlibrary.domain.question.ChoiceQstOption;
import com.forteach.quiz.questionlibrary.domain.question.Design;
import com.forteach.quiz.questionlibrary.domain.question.TrueOrFalse;
import com.forteach.quiz.questionlibrary.service.KeywordService;
import com.forteach.quiz.questionlibrary.service.base.BaseQuestionService;
import com.forteach.quiz.service.TokenService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/14  16:39
 */
public abstract class BaseAllController <T extends QuestionExamEntity> extends BaseQuestionController<T>{
    public BaseAllController(BaseQuestionService<T> service, KeywordService<T> keywordService, TokenService tokenService) {
        super(service, keywordService, tokenService);
    }

    /**
     * 编辑判断题
     *
     * @param trueOrFalse
     * @return BigQuestion
     */
    @PostMapping("/trueOrFalse/edit")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "题目题干", name = "trueOrFalseInfo", required = true, example = "亚特兰蒂斯是否存在", paramType = "form"),
            @ApiImplicitParam(value = "题目答案", name = "trueOrFalseAnsw", required = true, example = "true", paramType = "form")
    })
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
    @ApiImplicitParams({
            @ApiImplicitParam(value = "题目题干", name = "choiceQstTxt", required = true, example = "1+1 = ?", paramType = "form"),
            @ApiImplicitParam(value = "题目答案", name = "choiceQstAnsw", required = true, example = "A", dataType = "string", paramType = "form"),
            @ApiImplicitParam(value = "单选与多选区分 single  multiple", name = "choiceType", required = true, example = "single", paramType = "form"),
            @ApiImplicitParam(value = "选项集", name = "optChildren", required = true, dataType = "list", dataTypeClass = ChoiceQstOption.class, paramType = "form")
    })
    public Mono<WebResult> editChoiceQst(@Valid @RequestBody T bigQuestion) {
        return service.editQuestion(bigQuestion, ChoiceQst.class).map(WebResult::okResult);
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
    public Mono<WebResult> editDesign(@Valid @RequestBody @ApiParam(value = "简答思考题", required = true) T design, @ApiIgnore ServerHttpRequest request) {
        tokenService.getTeacherId(request).ifPresent(design::setTeacherId);
        return service.editQuestion(design, Design.class).map(WebResult::okResult);
    }
}

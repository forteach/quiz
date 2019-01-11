package com.forteach.quiz.questionlibrary.domain.question;

import com.fasterxml.jackson.annotation.JsonView;
import com.forteach.quiz.web.vo.BigQuestionView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  10:10
 */
@Data
@ApiModel(value = "选择题选项", description = "ChoiceQst的子项")
public class ChoiceQstOption {

    @JsonView(BigQuestionView.Summary.class)
    @ApiModelProperty(value = "选择项id 新增时后台自动生成", name = "id", example = "632a005143cd4969ae07f5db0212773c")
    private String id;

    @JsonView(BigQuestionView.Summary.class)
    @ApiModelProperty(value = "选择项题干", name = "optTxt", required = true, example = "2")
    private String optTxt;

    @JsonView(BigQuestionView.Summary.class)
    @ApiModelProperty(value = "选择项 A,B,C等Value", name = "optValue", required = true, example = "A")
    private String optValue;

}

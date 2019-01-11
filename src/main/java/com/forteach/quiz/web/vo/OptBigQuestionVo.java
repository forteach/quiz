package com.forteach.quiz.web.vo;

import com.fasterxml.jackson.annotation.JsonView;
import com.forteach.quiz.questionlibrary.domain.BigQuestion;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/24  16:11
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "题对象", description = "通过 selected 判断是否选中")
public class OptBigQuestionVo extends BigQuestion {

    @JsonView(BigQuestionView.Summary.class)
    @ApiModelProperty(value = "是否被选中  1 : 被选中 / 2 : 未被选中", name = "selected", example = "1")
    private String selected;

    public OptBigQuestionVo() {
    }

    public OptBigQuestionVo(String selected, BigQuestion bigQuestion) {
        this.selected = selected;
        BeanUtils.copyProperties(bigQuestion, this);
    }
}

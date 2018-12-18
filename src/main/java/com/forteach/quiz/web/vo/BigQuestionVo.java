package com.forteach.quiz.web.vo;

import com.forteach.quiz.domain.BigQuestion;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/18  14:51
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BigQuestionVo extends BigQuestion {

    @ApiModelProperty(value = "题册排序用坐标", name = "index", example = "1")
    private int index;

    @ApiModelProperty(value = "题册用坐标", name = "preview", example = "1")
    private String preview;


    public BigQuestionVo(int index, BigQuestion bigQuestion) {
        this.index = index;
        BeanUtils.copyProperties(bigQuestion, this);
    }
}

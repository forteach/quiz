package com.forteach.quiz.web.vo;

import com.forteach.quiz.questionlibrary.domain.base.QuestionExamEntity;
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
public class BigQuestionVo<T extends QuestionExamEntity> extends QuestionExamEntity {

    /**
     * 课堂练习  before/预习 now/课堂 before,now/全部
     */
    @ApiModelProperty(value = "课堂练习  before/预习 now/课堂 before,now/全部", name = "preview", example = "1")
    private String preview;

    public BigQuestionVo() {
    }

    public BigQuestionVo(String preview, String index, T question) {
        BeanUtils.copyProperties(question, this);
        this.preview = preview;
        this.index = index;
    }
}

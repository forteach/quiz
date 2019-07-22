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

    public BigQuestionVo() {
    }

    public BigQuestionVo(String preview, String index, T question) {
        BeanUtils.copyProperties(question, this);
        this.index = index;
    }
}

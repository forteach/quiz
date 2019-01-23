package com.forteach.quiz.web.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.forteach.quiz.problemsetlibrary.domain.base.ProblemSet;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @Description: 详细的练习册信息
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/12  15:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "详细的练习册信息", description = "详细的练习册信息 包含完整的题目全部参数")
public class ProblemSetDet<T> extends ProblemSet {

    /**
     * 题目集 完整的题目信息
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<T> bigQuestionList;

    public ProblemSetDet(ProblemSet problemSet, List<T> bigQuestionList) {
        BeanUtils.copyProperties(problemSet, this);
        this.bigQuestionList = bigQuestionList;
    }
}

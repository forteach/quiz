package com.forteach.quiz.web.pojo;

import com.forteach.quiz.domain.ProblemSet;
import com.forteach.quiz.questionLibrary.domain.BigQuestion;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/12  15:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "详细的练习册信息", description = "详细的练习册信息 包含完整的题目全部参数")
public class ProblemSetDet extends ProblemSet {

    /**
     * 题目集 完整的题目信息
     */
    private List<BigQuestion> bigQuestionList;

    public ProblemSetDet(ProblemSet problemSet, List<BigQuestion> bigQuestionList) {
        this.bigQuestionList = bigQuestionList;
        BeanUtils.copyProperties(problemSet, this);
    }
}

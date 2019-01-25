package com.forteach.quiz.web.vo;

import com.forteach.quiz.problemsetlibrary.domain.base.ProblemSet;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/13  14:56
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "分页获取题库数据 并且获得题集关系", description = "获取到题集信息,交集信息,差集信息")
public class QuestionProblemSetVo {

    /**
     * 获得的题库数据
     */
    @ApiModelProperty(value = "获得的题库数据", name = "bigQuestionList")
    private List<?> bigQuestionList;

    /**
     * 题集
     */
    @ApiModelProperty(value = "题集", name = "problemSet")
    private ProblemSet problemSet;

    /**
     * 交集 (题集中包含的id)
     */
    @ApiModelProperty(value = "交集 (题集中包含的id)", name = "intersection")
    private List<String> intersection;

    /**
     * 差集 (题集中未包含的id)
     */
    @ApiModelProperty(value = "差集 (题集中未包含的id)", name = "difference")
    private List<String> difference;

}

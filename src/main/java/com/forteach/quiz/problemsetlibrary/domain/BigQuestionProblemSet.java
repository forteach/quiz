package com.forteach.quiz.problemsetlibrary.domain;

import com.forteach.quiz.problemsetlibrary.domain.base.ProblemSet;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/13  18:37
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "bigQuestionProblemSet")
@ApiModel(value = "题库 编辑题册对象", description = "如果新增 不添加id 如果修改 添加id")
public class BigQuestionProblemSet extends ProblemSet {

    /**
     * 题集类型：1、预习练习册 2、课堂练习册3、课后作业册
     */

    @ApiModelProperty(value = "题集类型", name = "exeBookType", example = "1、预习练习册 2、课堂练习册3、课后作业册")
    private int exeBookType;

}

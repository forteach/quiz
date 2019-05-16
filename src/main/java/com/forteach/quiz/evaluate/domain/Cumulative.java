package com.forteach.quiz.evaluate.domain;

import com.forteach.quiz.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Description: 对学生参与活动的奖励列表
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/11  14:14
 */
@Data
@Document(collection = "cumulative")
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@ApiModel(value = "奖励累加", description = "对学生进行奖励累加")
public class Cumulative extends BaseEntity {

    /**
     * 被累加的学生id
     */
//    @ApiModelProperty(value = "被累加的学生id", name = "studentId")
    private String studentId;

//    @ApiModelProperty(value = "题目Id", name = "questionId")
    private String questionId;

//    @ApiModelProperty(value = "题目互动活动 提问、任务", name = "questionType")
    private String questionType;

//    @ApiModelProperty(value = "修改前", name = "preValue")
    private String preValue;

//    @ApiModelProperty(value = "修改后", name = "nextValue")
    private String nextValue;

    /**
     * 增加值
     */
//    @ApiModelProperty(value = "增加值", name = "amount")
    private String amount;

    /**
     * 教师Id
     */
//    @ApiModelProperty(value = "教师id", name = "teacherId")
    private String teacherId;
}

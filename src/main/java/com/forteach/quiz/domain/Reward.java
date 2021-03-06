package com.forteach.quiz.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 学生的提问等被奖励  (小红花... ...)
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/11  14:01
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Reward extends BaseEntity {

    @ApiModelProperty(value = "学生id", name = "id")
    private String sutdentId;

    @ApiModelProperty(value = "获得的数量", name = "amount")
    private Double amount;

}

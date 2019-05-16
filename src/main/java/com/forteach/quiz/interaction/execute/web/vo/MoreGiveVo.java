package com.forteach.quiz.interaction.execute.web.vo;

import com.forteach.quiz.interaction.execute.domain.base.GiveVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/15  9:47
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "多个互动题目 发布课堂提问", description = "")
public class MoreGiveVo extends GiveVo {

    /**
     * 问题id集合
     */
    @ApiModelProperty(value = "问题id,多个id逗号分隔", name = "questionIds")
    private String questionId;

    @ApiModelProperty(value = "发布练习册的教师", name = "teacherId")
    private String teacherId;

    /**
     * 题目交互活动类型  TIWEN LIANXI RENWU
     */
    @ApiModelProperty(value = "切换提问类型过期标识  接收的该题cut", name = "cut")
    private String questionType;
}

package com.forteach.quiz.interaction.team.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import static com.forteach.quiz.common.Dic.ASK_GROUP;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/28  19:30
 */
@Data
@ApiModel(value = "小组增员或减员", description = "")
public class TeamChangeVo {

    /**
     * 课堂圈子id
     */
    @ApiModelProperty(value = "课堂圈子id", name = "circleId")
    private String circleId;

    /**
     * 小组id
     */
    @ApiModelProperty(value = "小组id", name = "teamId")
    private String teamId;

    /**
     * 学生id, 逗号分割
     */
    @ApiModelProperty(value = "被 新增或移除 小组的 学生id, 逗号分割", name = "students")
    private String students;

    /**
     * moreorLess 增加或减少学生
     * 1 : 增加
     * 2 : 减少
     */
    @ApiModelProperty(value = "1 : 增加  2 : 减少 ", name = "moreorLess")
    private String moreOrLess;

    public String getGroupKey() {
        return ASK_GROUP.concat(circleId);
    }
}

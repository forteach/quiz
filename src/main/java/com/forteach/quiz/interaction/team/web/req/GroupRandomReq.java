package com.forteach.quiz.interaction.team.web.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

import static com.forteach.quiz.common.Dic.ASK_GROUP;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/23  11:00
 */
@Data
@ApiModel(value = "随机分组", description = "随机分组的参数")
public class GroupRandomReq implements Serializable {

    /**
     * 课堂圈子id
     */
    @ApiModelProperty(value = "课堂圈子id/课程id", name = "circleId", dataType = "string", required = true)
    private String circleId;

    @ApiModelProperty(value = "班级id", name = "classId", dataType = "string", notes = "如果是课程必传")
    private String classId;

    /**
     * 共分几组
     */
    @ApiModelProperty(value = "要分分几个组", name = "number", dataType = "int", required = true)
    private Integer number;

    @ApiModelProperty(name = "expType", value = "分组的有效期 forever : 永久, temporarily : 临时", dataType = "string", required = true)
    private String expType;

    /**
     * 当前上课的老师id信息
     */
    @ApiModelProperty(hidden = true)
    private String teacherId;

    public static String groupKey(String circleId, String classId) {
        return circleId.concat(ASK_GROUP).concat(classId);
    }

    public String getGroupKey() {
        return circleId.concat(ASK_GROUP).concat(classId);
    }

}

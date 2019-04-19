package com.forteach.quiz.interaction.team.web.req;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

import static com.forteach.quiz.common.Dic.ASK_GROUP;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-4-19 10:45
 * @version: 1.0
 * @description:
 */
@Data
@ApiModel(value = "需要添加小组数据对象")
public class AddTeamReq implements Serializable {

    @ApiModelProperty(name = "circleId", value = "课堂圈子id/课程id", required = true, dataType = "string")
    private String circleId;

    @ApiModelProperty(name = "classId", value = "班级id", required = true, dataType = "string")
    private String classId;

    @ApiModelProperty(name = "teamName", value = "小组名字", required = true, dataType = "string")
    private String teamName;

    @ApiModelProperty(name = "expType", value = "分组的有效期 forever : 永久, temporarily : 临时", required = true, dataType = "string")
    private String expType;

    @ApiModelProperty(name = "students", value = "学生id(用逗号分割)", example = "1234,1235", dataType = "string", required = true)
    private String students;

    public String getGroupKey() {
        return circleId.concat(ASK_GROUP).concat(classId);
    }
}

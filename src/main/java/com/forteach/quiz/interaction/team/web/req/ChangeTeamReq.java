package com.forteach.quiz.interaction.team.web.req;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.io.Serializable;

import static com.forteach.quiz.common.Dic.ASK_GROUP;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-4-19 09:59
 * @version: 1.0
 * @description:
 */
@Data
@ApiOperation(value = "修改小组成员接口参数")
public class ChangeTeamReq implements Serializable {

    @ApiModelProperty(value = "小组id", notes = "需要移出的小组id", name = "teamId", dataType = "string", required = true)
    private String addTeamId;

    @ApiModelProperty(value = "小组id", notes = "需要添加的小组id", name = "teamId", dataType = "string", required = true)
    private String removeTeamId;

    @ApiModelProperty(value = "被 新增或移除 小组的 学生id, 逗号分割", name = "students", dataType = "string", required = true)
    private String students;

    public String getTeamKey(final String teamId){
        return teamId.concat(ASK_GROUP);
    }

    public static String concatTeamKey(final String teamId){
        return teamId.concat(ASK_GROUP);
    }

    public static String getGroupKey(final String circleId, final String classId) {
        return circleId.concat(ASK_GROUP).concat(classId);
    }
}

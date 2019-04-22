package com.forteach.quiz.interaction.team.web.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

import static com.forteach.quiz.common.Dic.ASK_GROUP;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-4-20 18:00
 * @version: 1.0
 * @description:
 */
@Data
@ApiModel(value = "删除小组", description = "{删除小组信息}")
public class DeleteTeamReq implements Serializable {
    @ApiModelProperty(value = "小组id", notes = "小组id", name = "teamId", dataType = "string", required = true)
    private String teamId;

    public String getTeamKey(){
        return teamId.concat(ASK_GROUP);
    }
}

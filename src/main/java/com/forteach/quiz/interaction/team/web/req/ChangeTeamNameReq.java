package com.forteach.quiz.interaction.team.web.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

import static com.forteach.quiz.common.Dic.ASK_GROUP;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-4-19 09:46
 * @version: 1.0
 * @description:
 */
@Data
@ApiModel(value = "修改小组名称", description = "{修改小组名称}")
public class ChangeTeamNameReq implements Serializable {

    @ApiModelProperty(value = "小组id", name = "teamId", dataType = "string", required = true)
    private String teamId;

    @ApiModelProperty(value = "小组名称", name = "teamName", dataType = "string", required = true)
    private String teamName;

    public String getTeamKey(){
        return teamId.concat(ASK_GROUP);
    }
}

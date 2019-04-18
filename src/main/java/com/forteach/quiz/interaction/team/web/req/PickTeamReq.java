package com.forteach.quiz.interaction.team.web.req;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-4-11 14:56
 * @version: 1.0
 * @description:
 */
@Data
@ApiModel(value = "创建选人小组")
public class PickTeamReq implements Serializable {
    /**
     * 课堂圈子id
     */
    @ApiModelProperty(value = "课堂圈子id", name = "circleId", required = true, dataType = "string")
    private String circleId;

    /**
     * 小组id
     */
    @ApiModelProperty(value = "小组id", name = "teamId", dataType = "string")
    private String teamId;

    @ApiModelProperty(value = "小组名称", name = "teamName", dataType = "string")
    private String teamName;

    /**
     * 学生id, 逗号分割
     */
    @ApiModelProperty(value = "被 新增或移除 小组的 学生id, 逗号分割", name = "students", dataType = "string", required = true)
    private String students;

    /**
     * moreorLess 增加或减少学生
     * 1 : 增加
     * 2 : 减少
     */
    @ApiModelProperty(value = "1 : 增加  2 : 减少 ", name = "moreOrLess", dataType = "string", required = true)
    private String moreOrLess;

    /**
     * 小组的时效性
     */
    @ApiModelProperty(name = "expType", value = "分组的有效期 forever : 永久, temporarily : 临时", dataType = "string", required = true)
    private String expType;

    @JsonIgnore
    private String teacherId;

    public String getTeamRedisKey(final String teamId){
        return teamId.concat(expType).concat(teacherId);
    }
}

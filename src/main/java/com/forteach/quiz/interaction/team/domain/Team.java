package com.forteach.quiz.interaction.team.domain;

import com.forteach.quiz.web.pojo.Students;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.List;

import static com.forteach.quiz.common.Dic.ASK_GROUP;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-4-22 18:24
 * @version: 1.0
 * @description:
 */
@Data
@ApiModel(value = "选人学生信息", description = "小组选人返回的学生信息")
public class Team {

    /**
     * 小组id
     */
    @Indexed
    @ApiModelProperty(name = "teamId", value = "小组id", dataType = "string")
    private String teamId;

    /**
     * 小组名称
     */
    @ApiModelProperty(name = "teamName", value = "小组名称", dataType = "string")
    private String teamName;

    /**
     * 学生数组
     */
    @ApiModelProperty(name = "students", dataType = "list", notes = "学生信息详情")
    private List<Students> students;

    public Team(final String teamId, final String teamName, final List<Students> students) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.students = students;
    }

    public Team(String teamId, String teamName) {
        this.teamId = teamId;
        this.teamName = teamName;
    }

    public Team() {
    }

    public String getTeamsGroupKey(final String teamId){
        return teamId.concat(ASK_GROUP);
    }
}

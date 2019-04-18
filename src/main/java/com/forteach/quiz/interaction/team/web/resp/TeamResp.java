package com.forteach.quiz.interaction.team.web.resp;

import com.forteach.quiz.web.pojo.Students;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-4-15 09:33
 * @version: 1.0
 * @description:
 */
@Data
@Builder
@ApiModel(value = "选人学生信息", description = "小组选人返回的学生信息")
public class TeamResp implements Serializable {

    @ApiModelProperty(name = "teamId", value = "小组id", dataType = "string")
    private String teamId;

    @ApiModelProperty(name = "teamName", value = "小组名称", dataType = "string")
    private String teamName;

    @ApiModelProperty(name = "students", dataType = "list")
    private List<Students> students;

    public TeamResp(String teamId, String teamName, List<Students> students) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.students = students;
    }

    public TeamResp() {
    }

}

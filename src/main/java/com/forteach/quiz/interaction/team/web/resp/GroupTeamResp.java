package com.forteach.quiz.interaction.team.web.resp;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/24  12:37
 */
@Data
public class GroupTeamResp implements Serializable {

    private List<TeamResp> teamList;

    public void addTeamList(TeamResp teamResp) {
        if (this.teamList == null) {
            this.teamList = new ArrayList<>(16);
            this.teamList.add(teamResp);
        } else {
            this.teamList.add(teamResp);
        }
    }

}

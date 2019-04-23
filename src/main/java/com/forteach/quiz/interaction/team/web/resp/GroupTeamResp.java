package com.forteach.quiz.interaction.team.web.resp;

import com.forteach.quiz.interaction.team.domain.Team;
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

    private List<Team> teamList;

    public void addTeamList(Team team) {
        if (this.teamList == null) {
            this.teamList = new ArrayList<>(16);
            this.teamList.add(team);
        } else {
            this.teamList.add(team);
        }
    }

}

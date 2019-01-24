package com.forteach.quiz.interaction.team.web.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/24  12:37
 */
@Data
public class GroupTeamVo {

    private List<Team> teamList;

    public void addTeamList(Team team) {
        if (this.teamList == null) {
            this.teamList = new ArrayList<>(20);
            this.teamList.add(team);
        } else {
            this.teamList.add(team);
        }
    }

}

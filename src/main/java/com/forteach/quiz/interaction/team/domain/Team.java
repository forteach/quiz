package com.forteach.quiz.interaction.team.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-4-22 18:24
 * @version: 1.0
 * @description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    private String teamId;

    private String teamName;

    private List<String> students;
}

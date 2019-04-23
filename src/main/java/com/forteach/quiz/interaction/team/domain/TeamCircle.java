package com.forteach.quiz.interaction.team.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-4-22 18:08
 * @version: 1.0
 * @description: 课堂小组(临时小组，只在当前课堂有效)
 */
@Data
@Document(value = "teamCircle")
@NoArgsConstructor
//@AllArgsConstructor
public class TeamCircle extends BaseTeam{
    /**
     * 课堂id
     */
//    private String circleId;
}
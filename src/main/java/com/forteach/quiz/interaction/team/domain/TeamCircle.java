package com.forteach.quiz.interaction.team.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-4-22 18:08
 * @version: 1.0
 * @description: 课堂小组(临时小组 ， 只在当前课堂有效)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(value = "teamCircle")
@NoArgsConstructor
public class TeamCircle extends BaseTeam {
    /**
     * 课堂id
     */
    @Indexed
    private String circleId;
}

package com.forteach.quiz.interaction.team.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-4-11 14:31
 * @version: 1.0
 * @description:　临时小组
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "pickTeam")
public class PickTeam extends Team {

    public PickTeam() {
    }
}

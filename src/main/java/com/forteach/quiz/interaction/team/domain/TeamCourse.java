package com.forteach.quiz.interaction.team.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-4-22 18:10
 * @version: 1.0
 * @description:　课程小组(对本课程有效)
 */
@Data
@Document(collection = "teamCourse")
@NoArgsConstructor
public class TeamCourse extends BaseTeam{
    /**
     * 课程id
     */
    @Indexed
    private String courseId;

}

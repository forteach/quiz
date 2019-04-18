package com.forteach.quiz.interaction.team.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-4-11 14:26
 * @version: 1.0
 * @description:　课程小组(长久有效)
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "courseTeam")
public class CourseTeam extends Team {

}

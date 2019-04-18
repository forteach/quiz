package com.forteach.quiz.interaction.team.repository;

import com.forteach.quiz.interaction.team.domain.CourseTeam;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-4-11 14:46
 * @version: 1.0
 * @description:
 */
public interface CourseTeamRepository extends ReactiveMongoRepository<CourseTeam, String> {

}

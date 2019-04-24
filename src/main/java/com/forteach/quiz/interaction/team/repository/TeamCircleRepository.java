package com.forteach.quiz.interaction.team.repository;

import com.forteach.quiz.interaction.team.domain.TeamCircle;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-4-24 09:33
 * @version: 1.0
 * @description:
 */
public interface TeamCircleRepository extends ReactiveMongoRepository<TeamCircle, String> {



}

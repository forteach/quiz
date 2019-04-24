package com.forteach.quiz.interaction.team.repository;

import com.forteach.quiz.interaction.team.domain.TeamCourse;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-4-24 09:37
 * @version: 1.0
 * @description:
 */
public interface TeamCourseRepository extends ReactiveMongoRepository<TeamCourse, String> {

    /**
     * .update({"_id":ObjectId("541c336c5bce1709288c96f3"),"mymember":"green","$atomic":"true"},
     * {$set:{"mymember.$":"red"}});
     * @param courseId
     * @param classId
     * @param teamId
     * @param uDate
     * @return
     */
//    @Query()
//    Mono<UpdateResult> updateTeamName(final String courseId, final String classId, final String teamId, final String uDate);
//    @DeleteQuery(value = "")

//    @DeleteQuery(value = "")
//    Mono<Boolean> deleteTeamCourseByTeamListIsTeamId();
}

package com.forteach.quiz.repository;

import com.forteach.quiz.domain.BigQuestion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  11:16
 */
public interface BigQuestionRepository extends ReactiveMongoRepository<BigQuestion,String> {

    /**
     * 根据id 分页查询对象
     *
     * @param teacherId
     * @param page
     * @return
     */
    @Query("{ 'teacherId':?0}")
    Flux<BigQuestion> findAllDetailedPage(final String teacherId, final Pageable page);


}

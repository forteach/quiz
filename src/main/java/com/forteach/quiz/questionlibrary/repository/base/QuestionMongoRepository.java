package com.forteach.quiz.questionlibrary.repository.base;

import com.forteach.quiz.questionlibrary.domain.base.QuestionExamEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import reactor.core.publisher.Flux;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/10  11:10
 */
@NoRepositoryBean
public interface QuestionMongoRepository<T extends QuestionExamEntity> extends ReactiveMongoRepository<T, String> {

    /**
     * 根据id 分页查询对象
     *
     * @param teacherId
     * @param page
     * @return
     */
    @Query("{ 'teacherId':?0}")
    Flux<T> findAllDetailedPage(final String teacherId, final Pageable page);


}

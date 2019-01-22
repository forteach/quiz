package com.forteach.quiz.interaction.execute.repository;

import com.forteach.quiz.interaction.execute.domain.InteractRecord;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/3  15:22
 */
public interface InteractRecordRepository extends ReactiveMongoRepository<InteractRecord, String> {

    /**
     * 根据互动课堂id及教师id查询对象
     *
     * @param teacherId
     * @param teacherId
     * @return
     */
    Flux<InteractRecord> findByCircleIdAndTeacherId(final String circleId, final String teacherId);

    /**
     * 通过课堂id查找记录
     *
     * @param circleId
     * @return
     */
    Mono<InteractRecord> findByCircleIdIs(final String circleId);
}

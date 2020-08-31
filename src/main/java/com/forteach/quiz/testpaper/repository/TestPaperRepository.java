package com.forteach.quiz.testpaper.repository;

import com.forteach.quiz.testpaper.domain.TestPaper;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/8/27 16:56
 * @Version: v1.0
 * @Modified：试卷信息
 * @Description: 试卷信息
 */
public interface TestPaperRepository extends ReactiveMongoRepository<TestPaper, String> {

    /**
     * 查询试卷根据课程
     * @param courseId
     * @param sort
     * @return
     */
    @Transactional(readOnly = true)
    Flux<TestPaper> findAllByCourseId(String courseId, Sort sort);

    /**
     * 查询试卷根据教师
     * @param teacherId
     * @param sort
     * @return
     */
    @Transactional(readOnly = true)
    Flux<TestPaper> findAllByTeacherId(String teacherId, Sort sort);

    /**
     * 查询试卷信息，教师id和课程id
     * @param courseId 课程Id
     * @param teacherId 教师id
     * @param sort
     * @return
     */
    @Transactional(readOnly = true)
    Flux<TestPaper> findAllByCourseIdAndTeacherIdOrderByUDateDesc(String courseId, String teacherId, Sort sort);
}
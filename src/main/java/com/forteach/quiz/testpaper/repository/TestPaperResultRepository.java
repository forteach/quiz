package com.forteach.quiz.testpaper.repository;

import com.forteach.quiz.testpaper.domain.TestPaperResult;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/9/10 11:23
 * @Version: v1.0
 * @Modified：学生回答结果
 * @Description:
 */
public interface TestPaperResultRepository extends ReactiveMongoRepository<TestPaperResult, String> {
    @Transactional(readOnly = true)
    Flux<TestPaperResult> findAllByTestPaperIdAndStudentId(final String testPaperId, final String studentId);
}

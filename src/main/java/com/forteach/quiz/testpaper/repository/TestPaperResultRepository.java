package com.forteach.quiz.testpaper.repository;

import com.forteach.quiz.testpaper.domain.TestPaperResult;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/9/10 11:23
 * @Version: v1.0
 * @Modified：学生回答结果
 * @Description:
 */
public interface TestPaperResultRepository extends ReactiveMongoRepository<TestPaperResult, String> {
}

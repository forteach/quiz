package com.forteach.quiz.testpaper.service;

import cn.hutool.core.util.StrUtil;
import com.forteach.quiz.testpaper.domain.TestPaper;
import com.forteach.quiz.testpaper.repository.TestPaperRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/8/27 17:11
 * @Version: v1.0
 * @Modified：试卷操作信息
 * @Description:
 */
@Slf4j
@Service
public class TestPaperService {
    private final TestPaperRepository testPaperRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public TestPaperService(ReactiveMongoTemplate reactiveMongoTemplate, TestPaperRepository testPaperRepository) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.testPaperRepository = testPaperRepository;
    }


    public Mono<TestPaper> updateSave(final TestPaper testPaper) {
        //保存更新试卷信息
        return testPaperRepository.save(testPaper);
    }

    public Flux<TestPaper> findAll(final String teacherId, final String courseId) {
        final Sort sort = new Sort(Sort.Direction.DESC, "uDate");
        if (StrUtil.isNotBlank(teacherId) && StrUtil.isNotBlank(courseId)) {
            return testPaperRepository.findAllByCourseIdAndTeacherIdOrderByUDateDesc(courseId, teacherId, sort);
        } else if (StrUtil.isNotBlank(teacherId) && StrUtil.isBlank(courseId)) {
            return testPaperRepository.findAllByTeacherId(teacherId, sort);
        } else if (StrUtil.isBlank(teacherId) && StrUtil.isNotBlank(courseId)) {
            return testPaperRepository.findAllByCourseId(courseId, sort);
        } else {
            return testPaperRepository.findAll(sort);
        }
    }

    public Mono<TestPaper> findById(String id){
        return testPaperRepository.findById(id);
    }
}

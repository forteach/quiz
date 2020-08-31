package com.forteach.quiz.testpaper.repository;

import com.forteach.quiz.testpaper.domain.ExamInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/8/28 16:54
 * @Version: v1.0
 * @Modified：考试安排信息
 * @Description:
 */
public interface ExamInfoRepository extends ReactiveMongoRepository<ExamInfo, String> {


}

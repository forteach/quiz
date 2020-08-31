package com.forteach.quiz.testpaper.service;

import cn.hutool.core.util.StrUtil;
import com.forteach.quiz.testpaper.domain.ExamInfo;
import com.forteach.quiz.testpaper.repository.ExamInfoRepository;
import com.forteach.quiz.testpaper.web.req.FindExamInfoReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/8/28 17:02
 * @Version: v1.0
 * @Modified：考试安排信息
 * @Description:
 */
@Slf4j
@Service
public class ExamInfoService {
    private final ExamInfoRepository examInfoRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public ExamInfoService(ExamInfoRepository examInfoRepository, ReactiveMongoTemplate reactiveMongoTemplate) {
        this.examInfoRepository = examInfoRepository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    public Mono<ExamInfo> saveUpdate(final ExamInfo examInfo){
        return examInfoRepository.save(examInfo);
    }

    public Flux<ExamInfo> findAll(final FindExamInfoReq req){
        Criteria criteria = new Criteria();
        if (StrUtil.isNotBlank(req.getClassId())){
            criteria.and("classList").in(req.getClassId());
        }
        if (StrUtil.isNotBlank(req.getStartDateTime())){
            //开始时间大于开始时间
            criteria.and("startDateTime").gte(LocalDateTime.parse(req.getStartDateTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (StrUtil.isNotBlank(req.getEndDateTime())){
            //结束时间大于结束时间
            criteria.and("endDateTime").lte(LocalDateTime.parse(req.getStartDateTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (StrUtil.isNotBlank(req.getTeacherId())){
            criteria.and("teacherId").is(req.getTeacherId());
        }
        if (StrUtil.isNotBlank(req.getSemester())){
            criteria.and("semester").is(Integer.valueOf(req.getSemester()));
        }
        if (StrUtil.isNotBlank(req.getYear())){
            criteria.and("year").is(Integer.valueOf(req.getYear()));
        }
        return reactiveMongoTemplate.find(Query.query(criteria), ExamInfo.class);


//        final Sort sort = new Sort(Sort.Direction.DESC, "uDate");
//        sort.and(Sort.unsorted().and())
//        ExamInfo examInfo = new ExamInfo();
//        BeanUtil.copyProperties(req, examInfo, "startDateTime", "endDateTime", "classId");
//        examInfo.setStartDateTime(req.getStartDateTime());
//        examInfo.setEndDateTime(req.getEndDateTime());
//        examInfo.setClassList(CollUtil.toList(req.getClassId()));
//        ExampleMatcher matching = ExampleMatcher.matchingAny().withIgnoreCase();
//        matching.withStringMatcher(ExampleMatcher.StringMatcher.DEFAULT);
////        examInfoRepository.
//        return examInfoRepository.findAll(Example.of(examInfo, matching), sort);
    }
}

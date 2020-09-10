package com.forteach.quiz.testpaper.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.forteach.quiz.testpaper.domain.ExamInfo;
import com.forteach.quiz.testpaper.repository.ExamInfoRepository;
import com.forteach.quiz.testpaper.web.req.FindExamInfoReq;
import com.forteach.quiz.testpaper.web.req.FindMyExamInfoReq;
import com.forteach.quiz.testpaper.web.resp.PaperExamResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

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

    public Mono<ExamInfo> saveUpdate(final ExamInfo examInfo) {
        return examInfoRepository.save(examInfo);
    }

    public Flux<ExamInfo> findAll(final FindExamInfoReq req) {
        Criteria criteria = new Criteria();
        if (StrUtil.isNotBlank(req.getClassId())) {
            criteria.andOperator(new Criteria().and("classList.classId").in(req.getClassId()));
        }
//        if (StrUtil.isNotBlank(req.getCourseName())){
        // 模糊匹配
//            Pattern pattern = Pattern.compile("^.*+" + req.getCourseName() + "+.*$", Pattern.CASE_INSENSITIVE);
//            Criteria.where("courseName").regex(pattern);
//        }
        if (StrUtil.isNotBlank(req.getCourseId())) {
            criteria.and("courseId").is(req.getCourseId());
        }
        if (StrUtil.isNotBlank(req.getStartDateTime())) {
            //开始时间大于开始时间
            criteria.and("startDateTime").gte(LocalDateTime.parse(req.getStartDateTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (StrUtil.isNotBlank(req.getEndDateTime())) {
            //结束时间大于结束时间
            criteria.and("endDateTime").lte(LocalDateTime.parse(req.getStartDateTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (StrUtil.isNotBlank(req.getTeacherId())) {
            criteria.and("teacherId").is(req.getTeacherId());
        }
        if (StrUtil.isNotBlank(req.getSemester())) {
            criteria.and("semester").is(Integer.valueOf(req.getSemester()));
        }
        if (StrUtil.isNotBlank(req.getYear())) {
            criteria.and("year").is(Integer.valueOf(req.getYear()));
        }
        if (StrUtil.isNotBlank(req.getTestPaperId())) {
            criteria.and("testPaperId").is(req.getTestPaperId());
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

    public Mono<List<PaperExamResp>> findMyExamInfo(final FindMyExamInfoReq req) {
        Criteria criteria = new Criteria();
        if (StrUtil.isNotBlank(req.getClassId())) {
            criteria.andOperator(new Criteria().and("classList.classId").in(req.getClassId()));
        }
        if (StrUtil.isNotBlank(req.getStartDateTime())) {
            //开始时间小于开始时间
            criteria.and("startDateTime").gte(LocalDateTime.parse(req.getStartDateTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (StrUtil.isNotBlank(req.getEndDateTime())) {
            //结束时间大于结束时间
            criteria.and("endDateTime").lte(LocalDateTime.parse(req.getEndDateTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (StrUtil.isNotBlank(req.getCourseId())) {
            criteria.and("courseId").is(req.getCourseId());
        }
        if (StrUtil.isNotBlank(req.getTeacherId())) {
            criteria.and("teacherId").is(req.getTeacherId());
        }
        Query query = Query.query(criteria).with(new Sort(Sort.Direction.ASC, "startDateTime"));
        query.fields().exclude("classList");
        return reactiveMongoTemplate.find(query, ExamInfo.class)
                .map(e -> {
                    PaperExamResp resp = new PaperExamResp();
                    BeanUtil.copyProperties(e, resp);
                    return resp;
                })
                .collectList();
    }

    /**
     * 判断班级学生现在是否在考试时间
     * @param classId
     * @param testPaperId
     * @return
     */
    public Mono<Boolean> decide(final String classId, final String testPaperId) {
        Criteria criteria = new Criteria();
        if (StrUtil.isNotBlank(classId)) {
            criteria.andOperator(new Criteria().and("classList.classId").in(classId));
        }
        LocalDateTime now = LocalDateTime.now();
        //开始时间大于开始时间
        criteria.and("startDateTime").lte(now);
        //结束时间小于结束时间
        criteria.and("endDateTime").gte(now);
        if (StrUtil.isNotBlank(testPaperId)) {
            criteria.and("testPaperId").is(testPaperId);
        }
        Query query = Query.query(criteria).with(new Sort(Sort.Direction.ASC, "startDateTime"));
        return reactiveMongoTemplate.exists(query, ExamInfo.class);
    }
}

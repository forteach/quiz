package com.forteach.quiz.testpaper.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.exceptions.CustomException;
import com.forteach.quiz.questionlibrary.service.BigQuestionService;
import com.forteach.quiz.service.CorrectService;
import com.forteach.quiz.testpaper.domain.TestPaper;
import com.forteach.quiz.testpaper.domain.TestPaperResult;
import com.forteach.quiz.testpaper.web.req.AddResultReq;
import com.forteach.quiz.testpaper.web.req.TestPaperPageReq;
import com.forteach.quiz.testpaper.web.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.forteach.quiz.common.Dic.*;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/9/11 15:11
 * @Version: v1.0
 * @Modified：回答试卷
 * @Description:
 */
@Slf4j
@Service
public class TestPaperResultService {
    private final TestPaperService testPaperService;
    private final CorrectService correctService;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final BigQuestionService bigQuestionService;
    private final ExamInfoService examInfoService;


    public TestPaperResultService(TestPaperService testPaperService, CorrectService correctService, ReactiveMongoTemplate reactiveMongoTemplate, BigQuestionService bigQuestionService, ExamInfoService examInfoService) {
        this.testPaperService = testPaperService;
        this.correctService = correctService;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.bigQuestionService = bigQuestionService;
        this.examInfoService = examInfoService;
    }


    public Mono<Boolean> addResult(final AddResultReq addResultReq) {
        return examInfoService.decide(addResultReq.getClassId(), addResultReq.getTestPaperId())
                .filterWhen(b -> MyAssert.isFalse(b, DefineCode.ERR0013, "您没有对应的权限"))
                .flatMap(b -> testPaperService.findById(addResultReq.getTestPaperId())
                        .switchIfEmpty(Mono.error(new CustomException("没有找到试卷")))
                        .flatMap(testPaper -> {
                            //查找习题对应的详情
                            return bigQuestionService.findOneDetailed(addResultReq.getQuestionId())
                                    .filter(Objects::nonNull)
                                    //判断回答内容正确与否
                                    .flatMap(bigQuestion -> correctService.result(bigQuestion, addResultReq.getAnswer())
                                            .map(result -> createResultVo(result, addResultReq.getQuestionType(), testPaper, addResultReq))
                                            // 判断是否已经回答过当前题目设置设置查询条件
                                            .flatMap(resultVo -> reactiveMongoTemplate.findOne(Query.query(Criteria.where("studentId")
                                                    .is(addResultReq.getStudentId())
                                                    .and("testPaperId").is(addResultReq.getTestPaperId())), TestPaperResult.class)
                                                    .switchIfEmpty(Mono.just(new TestPaperResult()))
                                                    .flatMap(testPaperResult -> {
                                                        List<ResultVo> resultList = testPaperResult.getResultList() == null ? new ArrayList<>() : testPaperResult.getResultList();
                                                        if (null != resultList && resultList.isEmpty()) {
                                                            resultList = CollUtil.toList(resultVo);
                                                        } else {
                                                            //获取题目id集合
                                                            List<String> questionIds = resultList.stream()
                                                                    .filter(Objects::nonNull)
                                                                    .map(ResultVo::getQuestionId)
                                                                    .collect(Collectors.toList());
                                                            //如果已经回答过了，覆盖回答并重新计算保存数据
                                                            if (!questionIds.isEmpty() && questionIds.contains(addResultReq.getQuestionId())) {
                                                                ResultVo vo = resultList.stream().filter(Objects::nonNull).filter(v -> v.getQuestionId().equals(addResultReq.getQuestionId())).findFirst().get();
                                                                BeanUtil.copyProperties(resultVo, vo);
                                                            } else {
                                                                resultList.add(resultVo);
                                                            }
                                                        }
                                                        //重新计算总成绩
                                                        int testScore = resultList.stream().mapToInt(ResultVo::getScore).sum();
                                                        BeanUtil.copyProperties(addResultReq, testPaperResult);
                                                        testPaperResult.setResultList(resultList);
                                                        testPaperResult.setTestScore(testScore);
                                                        return reactiveMongoTemplate.save(testPaperResult).hasElement();
                                                    })
                                            )
                                    );
                        })
                );
    }

    private ResultVo createResultVo(Boolean result, String questionType, TestPaper testPaper, AddResultReq addResultReq) {
        ResultVo resultVo = new ResultVo();
        if (result) {
            switch (questionType) {
                //单选
                case QUESTION_CHOICE_OPTIONS_SINGLE:
                    resultVo.setScore(testPaper.getSingleScore());
                    break;
                //多选
                case QUESTION_CHOICE_MULTIPLE_SINGLE:
                    resultVo.setScore(testPaper.getMultipleScore());
                    break;
                //判断
                case BIG_QUESTION_EXAM_CHILDREN_TYPE_TRUEORFALSE:
                    resultVo.setScore(testPaper.getTrueOrFalseScore());
                    break;
                default:
                    MyAssert.isFalse(false, DefineCode.ERR0013, "习题类型错误");
            }
        }
        resultVo.setResult(result);
        resultVo.setAnswer(addResultReq.getAnswer());
        resultVo.setQuestionId(addResultReq.getQuestionId());
        resultVo.setQuestionType(questionType);
        return resultVo;
    }


    public Mono<List<TestPaperResult>> findAllPage(final TestPaperPageReq req) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (StrUtil.isNotBlank(req.getTestPaperId())) {
            criteria.and("testPaperId").is(req.getTestPaperId());
        }
        if (StrUtil.isNotBlank(req.getStudentId())) {
            criteria.and("studentId").is(req.getStudentId());
        }
        if (StrUtil.isNotBlank(req.getClassName())) {
            Pattern pattern = Pattern.compile("^.*" + req.getClassName() + ".*$", Pattern.CASE_INSENSITIVE);
            criteria.and("className").regex(pattern);
        }
        if (StrUtil.isNotBlank(req.getStudentName())) {
            Pattern pattern = Pattern.compile("^.*" + req.getStudentName() + ".*$", Pattern.CASE_INSENSITIVE);
            criteria.and("studentName").regex(pattern);
        }
        if (StrUtil.isNotBlank(req.getCourseName())) {
            Pattern pattern = Pattern.compile("^.*" + req.getCourseName() + ".*$", Pattern.CASE_INSENSITIVE);
            criteria.and("courseName").regex(pattern);
        }
        if (StrUtil.isNotBlank(req.getTestPaperName())) {
            Pattern pattern = Pattern.compile("^.*" + req.getTestPaperName() + ".*$", Pattern.CASE_INSENSITIVE);
            criteria.and("testPaperName").regex(pattern);
        }
        if (StrUtil.isNotBlank(String.valueOf(req.getYear()))) {
            criteria.and("year").is(req.getYear());
        }
        if (StrUtil.isNotBlank(String.valueOf(req.getSemester()))) {
            criteria.and("semester").is(req.getSemester());
        }
        //设置分页和排序方式
        Sort sort;
        if (0 == req.getSort()) {
            sort = new Sort(Sort.Direction.ASC, "uDate");
        } else {
            sort = new Sort(Sort.Direction.DESC, "uDate");
        }
        query.addCriteria(criteria).with(PageRequest.of(req.getPage(), req.getSize(), sort));
        return reactiveMongoTemplate.find(query, TestPaperResult.class).collectList();
    }
}
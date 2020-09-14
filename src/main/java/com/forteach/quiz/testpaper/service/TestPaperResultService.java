package com.forteach.quiz.testpaper.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.exceptions.CustomException;
import com.forteach.quiz.questionlibrary.service.BigQuestionService;
import com.forteach.quiz.service.CorrectService;
import com.forteach.quiz.testpaper.domain.TestPaper;
import com.forteach.quiz.testpaper.domain.TestPaperResult;
import com.forteach.quiz.testpaper.repository.TestPaperResultRepository;
import com.forteach.quiz.testpaper.web.req.AddResultReq;
import com.forteach.quiz.testpaper.web.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private final TestPaperResultRepository testPaperResultRepository;


    public TestPaperResultService(TestPaperService testPaperService, CorrectService correctService, ReactiveMongoTemplate reactiveMongoTemplate, BigQuestionService bigQuestionService, TestPaperResultRepository testPaperResultRepository) {
        this.testPaperService = testPaperService;
        this.correctService = correctService;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.bigQuestionService = bigQuestionService;
        this.testPaperResultRepository = testPaperResultRepository;
    }


    public Mono<Boolean> addResult(final AddResultReq addResultReq) {
        return testPaperService.findById(addResultReq.getTestPaperId())
                .switchIfEmpty(Mono.error(new CustomException("没有找到试卷")))
                .flatMap(testPaper -> {
                    // 判断是否已经回答过当前题目设置设置查询条件
                    //查找习题对应的详情
                    return bigQuestionService.findOneDetailed(addResultReq.getQuestionId())
                            .filter(Objects::nonNull)
                            //判断回答内容正确与否
                            .flatMap(bigQuestion ->
                                    correctService.result(bigQuestion, addResultReq.getAnswer())
                                            .map(result -> createResultVo(result, addResultReq.getQuestionType(), testPaper, addResultReq))
                                            .flatMap(resultVo ->
                                                    reactiveMongoTemplate.findOne(Query.query(Criteria.where("studentId")
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
                                                                    if (!questionIds.isEmpty() && questionIds.contains(addResultReq.getQuestionId())){
                                                                        ResultVo vo = resultList.stream().filter(Objects::nonNull).filter(v -> v.getQuestionId().equals(addResultReq.getQuestionId())).findFirst().get();
                                                                        BeanUtil.copyProperties(resultVo, vo);
                                                                    }else {
                                                                        resultList.add(resultVo);
                                                                    }
                                                                }
                                                                //计算总成绩
                                                                int testScore = resultList.stream().mapToInt(ResultVo::getScore).sum();
                                                                BeanUtil.copyProperties(addResultReq, testPaperResult);
                                                                testPaperResult.setResultList(resultList);
                                                                testPaperResult.setTestScore(testScore);
                                                                return reactiveMongoTemplate.save(testPaperResult).hasElement();
                                                            })
                                            )
                            );
                });

    }

    private ResultVo createResultVo(Boolean result, String questionType, TestPaper testPaper, AddResultReq addResultReq) {
        ResultVo resultVo = new ResultVo();
        if (result) {
            if ("single".equals(questionType)) {
                resultVo.setScore(testPaper.getSingleScore());
            } else if ("multiple".equals(questionType)) {
                resultVo.setScore(testPaper.getSingleScore());
            } else if ("trueOrFalse".equals(questionType)) {
                resultVo.setScore(testPaper.getTrueOrFalseScore());
            } else {
                MyAssert.isFalse(false, DefineCode.ERR0013, "习题类型错误");
            }
        }
        resultVo.setResult(result);
        resultVo.setAnswer(addResultReq.getAnswer());
        resultVo.setQuestionId(addResultReq.getQuestionId());
        resultVo.setQuestionType(questionType);
        return resultVo;
    }
}
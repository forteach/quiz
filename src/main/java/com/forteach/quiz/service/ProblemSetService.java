package com.forteach.quiz.service;

import com.forteach.quiz.domain.BigQuestion;
import com.forteach.quiz.domain.ProblemSet;
import com.forteach.quiz.domain.QuestionIds;
import com.forteach.quiz.exceptions.ExamQuestionsException;
import com.forteach.quiz.repository.BigQuestionRepository;
import com.forteach.quiz.repository.ExerciseBookSheetRepository;
import com.forteach.quiz.repository.ProblemSetRepository;
import com.forteach.quiz.web.pojo.ProblemSetDet;
import com.forteach.quiz.web.req.ProblemSetReq;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.forteach.quiz.common.Dic.PARAMETER_ALL;
import static com.forteach.quiz.common.Dic.PARAMETER_PART;
import static com.forteach.quiz.util.StringUtil.isNotEmpty;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/11  16:18
 */
@Service
public class ProblemSetService {


    private final ExerciseBookSheetRepository exerciseBookSheetRepository;

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    private final CorrectService correctService;

    private final ProblemSetRepository problemSetRepository;

    private final BigQuestionRepository bigQuestionRepository;


    public ProblemSetService(ExerciseBookSheetRepository exerciseBookSheetRepository,
                             ReactiveMongoTemplate reactiveMongoTemplate, CorrectService correctService,
                             ProblemSetRepository problemSetRepository, BigQuestionRepository bigQuestionRepository) {
        this.exerciseBookSheetRepository = exerciseBookSheetRepository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.correctService = correctService;
        this.problemSetRepository = problemSetRepository;
        this.bigQuestionRepository = bigQuestionRepository;
    }

    /**
     * 保存题册
     *
     * @param problemSet
     * @return
     */
    public Mono<ProblemSet> buildExerciseBook(final ProblemSet problemSet) {
        return problemSetRepository.save(problemSet);
    }

    /**
     * 删除练习册
     *
     * @param id
     * @return
     */
    public Mono<Void> delExerciseBook(final String id) {
        return problemSetRepository.deleteById(id);
    }

    /**
     * 根据id 获取练习册 基本信息
     *
     * @param exerciseBookId
     * @return
     */
    public Mono<ProblemSet> findOne(final String exerciseBookId) {
        return problemSetRepository.findById(exerciseBookId);
    }

    public Mono<ProblemSetDet> findAllDetailed(final String exerciseBookId) {
        return findOne(exerciseBookId)
                .flatMap(set -> {
                    /*
                     * 提取所有的问题id集
                     * 提取对照的index坐标 后续排序
                     * */
                    final List<String> list = set.getQuestionIds().stream()
                            .map(QuestionIds::getBigQuestionId).collect(Collectors.toList());
                    final Map<String, Integer> indexMap = set.getQuestionIds().stream()
                            .collect(Collectors.toMap(QuestionIds::getBigQuestionId, QuestionIds::getIndex));

                    return findById(list)
                            .sort(Comparator.comparing(question -> indexMap.get(question.getId())))
                            .collectList()
                            .map(monoList -> new ProblemSetDet(set, monoList));
                });
    }

    private Flux<BigQuestion> findById(final List<String> id) {
        return bigQuestionRepository.findAllById(id);
    }

    private Flux<ProblemSet> findPratProblemSet(final ProblemSetReq sortVo) {

        Criteria criteria = Criteria.where("teacherId").is(sortVo.getOperatorId());

        Query query = new Query(criteria);

        if (isNotEmpty(sortVo.getExeBookType())) {
            criteria.and("exeBookType").in(sortVo.getExeBookType());
        }
        if (isNotEmpty(sortVo.getSectionId())) {
            criteria.and("sectionId").in(sortVo.getSectionId());
        }
        if (isNotEmpty(sortVo.getKnowledgeId())) {
            criteria.and("knowledgeId").in(sortVo.getKnowledgeId());
        }

        sortVo.queryPaging(query);

        return reactiveMongoTemplate.find(query, ProblemSet.class);
    }

    private Flux<ProblemSet> findAllProblemSet(final ProblemSetReq sortVo) {
        return findPratProblemSet(sortVo).flatMap(obj -> findAllDetailed(obj.getId()));
    }


    /**
     * 根据分页信息查询
     *
     * @param sortVo
     * @return
     */
    public Flux<ProblemSet> findProblemSet(final ProblemSetReq sortVo) {

        if (PARAMETER_PART.equals(sortVo.getAllOrPart())) {
            return findPratProblemSet(sortVo);
        } else if (PARAMETER_ALL.equals(sortVo.getAllOrPart())) {
            return findAllProblemSet(sortVo);
        }

        return Flux.error(new ExamQuestionsException("错误的查询条件"));

    }


}

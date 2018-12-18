package com.forteach.quiz.service;

import com.forteach.quiz.domain.BigQuestion;
import com.forteach.quiz.domain.ExerciseBook;
import com.forteach.quiz.domain.QuestionIds;
import com.forteach.quiz.repository.ExerciseBookRepository;
import com.forteach.quiz.web.req.ExerciseBookReq;
import com.forteach.quiz.web.vo.ExerciseBookVo;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import static com.forteach.quiz.util.StringUtil.isNotEmpty;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/13  16:45
 */
@Service
public class ExerciseBookService {

    private final ExamQuestionsService examQuestionsService;

    private final ExerciseBookRepository exerciseBookRepository;

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public ExerciseBookService(ExamQuestionsService examQuestionsService, ExerciseBookRepository exerciseBookRepository,
                               ReactiveMongoTemplate reactiveMongoTemplate) {
        this.examQuestionsService = examQuestionsService;
        this.exerciseBookRepository = exerciseBookRepository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    /**
     * 按照顺序 保存练习册
     *
     * @param exerciseBookVo
     * @return
     */
    public Mono<ExerciseBook> buildBook(final ExerciseBookVo exerciseBookVo) {

        final Map<String, Integer> idexMap = exerciseBookVo.getQuestionIds().stream().collect(Collectors.toMap(QuestionIds::getBigQuestionId, QuestionIds::getIndex));

        return examQuestionsService
                .findBigQuestionInId(
                        exerciseBookVo
                                .getQuestionIds()
                                .stream()
                                .map(QuestionIds::getBigQuestionId)
                                .collect(Collectors.toList()))
                .map(bigQuestion -> {
                    bigQuestion.setIndex(idexMap.get(bigQuestion.getId()));
                    return bigQuestion;
                })
                .sort(Comparator.comparing(BigQuestion::getIndex))
                .collectList()
                .flatMap(vos -> exerciseBookRepository.save(
                        new ExerciseBook<>(
                                exerciseBookVo, vos
                        )
                ));
    }

    public Flux<ExerciseBook> findExerciseBook(final ExerciseBookReq sortVo) {

        Criteria criteria = Criteria.where("teacherId").is(sortVo.getOperatorId());

        Query query = new Query(criteria);

        if (isNotEmpty(sortVo.getExeBookType())) {
            criteria.and("exeBookType").in(Integer.parseInt(sortVo.getExeBookType()));
        }
        if (isNotEmpty(sortVo.getSectionId())) {
            criteria.and("sectionId").in(sortVo.getSectionId());
        }
        if (isNotEmpty(sortVo.getCourseId())) {
            criteria.and("courseId").in(sortVo.getCourseId());
        }
        if (isNotEmpty(sortVo.getLevelId())) {
            criteria.and("levelId").in(sortVo.getLevelId());
        }

        sortVo.queryPaging(query);

        return reactiveMongoTemplate.find(query, ExerciseBook.class);
    }
}

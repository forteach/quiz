package com.forteach.quiz.service;

import com.forteach.quiz.domain.BigQuestion;
import com.forteach.quiz.domain.ExerciseBook;
import com.forteach.quiz.domain.QuestionIds;
import com.forteach.quiz.repository.ExerciseBookRepository;
import com.forteach.quiz.web.vo.ExerciseBookVo;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

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

    public ExerciseBookService(ExamQuestionsService examQuestionsService, ExerciseBookRepository exerciseBookRepository) {
        this.examQuestionsService = examQuestionsService;
        this.exerciseBookRepository = exerciseBookRepository;
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

}

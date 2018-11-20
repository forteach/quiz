package com.forteach.quiz.service;

import com.forteach.quiz.domain.ExerciseBook;
import com.forteach.quiz.domain.ExerciseBookSheet;
import com.forteach.quiz.domain.QuestionIds;
import com.forteach.quiz.repository.ExerciseBookRepository;
import com.forteach.quiz.repository.ExerciseBookSheetRepository;
import com.forteach.quiz.web.vo.BigQuestionVo;
import com.forteach.quiz.web.vo.ExerciseBookAttributeVo;
import com.forteach.quiz.web.vo.ExerciseBookQuestionVo;
import com.forteach.quiz.web.vo.ExerciseBookVo;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 练习册相关
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  11:01
 */
@Component
public class ProblemSetService {

    private final ExamQuestionsService examQuestionsService;

    private final ExerciseBookRepository exerciseBookRepository;

    private final ExerciseBookSheetRepository exerciseBookSheetRepository;

    public ProblemSetService(ExamQuestionsService examQuestionsService, ExerciseBookRepository exerciseBookRepository,
                             ExerciseBookSheetRepository exerciseBookSheetRepository) {
        this.examQuestionsService = examQuestionsService;
        this.exerciseBookRepository = exerciseBookRepository;
        this.exerciseBookSheetRepository = exerciseBookSheetRepository;
    }

    /**
     * 按照顺序 保存练习册
     *
     * @param exerciseBookVo
     * @return
     */
    public Mono<ExerciseBook> buildExerciseBook(final ExerciseBookVo exerciseBookVo) {

        final Map<String, Integer> idexMap = exerciseBookVo.getQuestionIds().stream().collect(Collectors.toMap(QuestionIds::getBigQuestionId, QuestionIds::getIndex));

        return examQuestionsService
                .findBigQuestionInId(
                        exerciseBookVo
                                .getQuestionIds()
                                .stream()
                                .map(QuestionIds::getBigQuestionId)
                                .collect(Collectors.toList()))
                .map(bigQuestion -> new BigQuestionVo(idexMap.get(bigQuestion.getId()), bigQuestion))
                .sort(Comparator.comparing(BigQuestionVo::getIndex))
                .collectList()
                .flatMap(vos -> exerciseBookRepository.save(
                        new ExerciseBook(
                                exerciseBookVo.getExeBookType(), exerciseBookVo.getTeacherId(), exerciseBookVo.getExeBookName(), vos
                        )
                ));
    }

    /**
     * 编辑保存练习册属性
     *
     * @param exerciseBookAttributeVo
     * @return
     */
    public Mono<ExerciseBook> editexerciseBookAttribute(final ExerciseBookAttributeVo exerciseBookAttributeVo) {
        return exerciseBookRepository
                .findById(exerciseBookAttributeVo.getId())
                .map(obj -> {
                    obj.setCDate(obj.getCDate());
                    obj.setExeBookName(exerciseBookAttributeVo.getExeBookName());
                    obj.setExeBookType(exerciseBookAttributeVo.getExeBookType());
                    return obj; })
                .flatMap(exerciseBookRepository::save);
    }

    public Mono<Void> delExerciseBook(final String id){
        return exerciseBookRepository.deleteById(id);
    }

    /**
     * 根据id 获取练习册
     * @param exerciseBookId
     * @return
     */
    public Mono<ExerciseBook> getExerciseBook(final String exerciseBookId) {
        return exerciseBookRepository.findById(exerciseBookId);
    }

    /**
     * 练习册答案
     *
     * @param exerciseBookSheet
     * @return
     */
    public Mono<ExerciseBookSheet> editExerciseBookSheet(final  ExerciseBookSheet exerciseBookSheet){
        return exerciseBookSheetRepository.save(exerciseBookSheet);
    }

    /**
     *
     * @param exerciseBookQuestionVo
     * @return
     */
    public Mono<ExerciseBook> changeExerciseBookQuestions(final ExerciseBookQuestionVo exerciseBookQuestionVo){
        return examQuestionsService.editBigQuestion(exerciseBookQuestionVo.getBigQuestions())
                .collectList()
                .flatMap(questionList -> {
                    if (questionList.size()>0){
                        return exerciseBookRepository
                                .findById(exerciseBookQuestionVo.getExerciseBookId())
                                .map(obj -> {
                                    obj.setCDate(obj.getCDate());
                                    obj.setQuestionChildren(questionList);
                                    return obj;
                                })
                                .flatMap(exerciseBookRepository::save);
                    }else {
                        return Mono.empty();
                    }
                });
    }







}

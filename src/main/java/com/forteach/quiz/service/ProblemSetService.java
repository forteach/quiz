package com.forteach.quiz.service;

import com.alibaba.fastjson.JSON;
import com.forteach.quiz.domain.*;
import com.forteach.quiz.exceptions.ProblemSetException;
import com.forteach.quiz.repository.ExerciseBookRepository;
import com.forteach.quiz.repository.ExerciseBookSheetRepository;
import com.forteach.quiz.repository.ProblemSetBackupRepository;
import com.forteach.quiz.web.vo.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import static com.forteach.quiz.common.Dic.COMMIT_EXERCISE_BOOK_SHEET_COMMIT;
import static com.forteach.quiz.common.Dic.COMMIT_EXERCISE_BOOK_SHEET_MODIFY;

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

    private final ProblemSetBackupRepository problemSetBackupRepository;

    private final ExerciseBookSheetRepository exerciseBookSheetRepository;

    private final CorrectService correctService;


    public ProblemSetService(ExamQuestionsService examQuestionsService, ExerciseBookRepository exerciseBookRepository,
                             ExerciseBookSheetRepository exerciseBookSheetRepository, ProblemSetBackupRepository problemSetBackupRepository,
                             CorrectService correctService) {
        this.examQuestionsService = examQuestionsService;
        this.exerciseBookRepository = exerciseBookRepository;
        this.problemSetBackupRepository = problemSetBackupRepository;
        this.exerciseBookSheetRepository = exerciseBookSheetRepository;
        this.correctService = correctService;
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
                                .parallelStream()
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
                    return obj;
                })
                .flatMap(exerciseBookRepository::save);
    }

    public Mono<Void> delExerciseBook(final String id) {
        return exerciseBookRepository.deleteById(id);
    }

    /**
     * 根据id 获取练习册
     *
     * @param exerciseBookId
     * @return
     */
    public Mono<ExerciseBook> getExerciseBook(final String exerciseBookId) {
        return exerciseBookRepository.findById(exerciseBookId);
    }

    /**
     * 练习册答案
     *
     * @param exerciseBookSheetVo
     * @return
     */
    public Mono<ExerciseBookSheet> editExerciseBookSheet(final ExerciseBookSheetVo exerciseBookSheetVo) {
        return exerciseBookSheetRepository
                .findById(exerciseBookSheetVo.getId())
                .defaultIfEmpty(new ExerciseBookSheet(exerciseBookSheetVo))
                .filterWhen(this::verifyExerciseBookSheetCommit)
                .map(obj -> {
                    obj.setAnsw(exerciseBookSheetVo.getAnsw());
                    return obj;
                })
                .flatMap(exerciseBookSheetRepository::save);
    }

    /**
     * 提交练习册答案 并批改答案
     *
     * @param exerciseBookSheetVo
     * @return
     */
    public Mono<ExerciseBookSheet> commitExerciseBookSheet(final ExerciseBookSheetVo exerciseBookSheetVo) {
        return exerciseBookSheetRepository
                .findById(exerciseBookSheetVo.getId())
                .switchIfEmpty(Mono.error(new ProblemSetException("未找到练习册")))
                .filterWhen(this::verifyExerciseBookSheetCommit)
                .map(obj -> {
                    obj.setAnsw(exerciseBookSheetVo.getAnsw());
                    obj.setBackupId(exerciseBookSheetVo.getBackupId());
                    obj.setCommit(COMMIT_EXERCISE_BOOK_SHEET_COMMIT);
                    return obj;
                })
                .transform(correctService::exerciseBookCorrect)
                .flatMap(exerciseBookSheetRepository::save);
    }

    /**
     * @param exerciseBookQuestionVo
     * @return
     */
    public Mono<ExerciseBook> changeExerciseBookQuestions(final ExerciseBookQuestionVo exerciseBookQuestionVo) {
        return examQuestionsService.editBigQuestion(exerciseBookQuestionVo.getBigQuestions())
                .collectList()
                .flatMap(questionList -> {
                    return exerciseBookRepository
                            .findById(exerciseBookQuestionVo.getExerciseBookId())
                            .map(obj -> {
                                obj.setCDate(obj.getCDate());
                                obj.setQuestionChildren(questionList);
                                return obj;
                            })
                            .flatMap(exerciseBookRepository::save);
                });
    }

    /**
     * 生成 题集备份册子   供:作业 ~ 试卷
     *
     * @return
     */
    public Mono<ProblemSetBackup> editProblemSetBackup(final ProblemSetBackupVo problemSetBackupVo) {
        return getExerciseBook(problemSetBackupVo.getExerciseBookId())
                .flatMap(exerciseBook -> problemSetBackupRepository.save(new ProblemSetBackup(problemSetBackupVo.getType(), JSON.toJSONString(exerciseBook))));
    }

    private Mono<Boolean> verifyExerciseBookSheetCommit(final ExerciseBookSheet sheetMono) {
        if (COMMIT_EXERCISE_BOOK_SHEET_MODIFY.equals(sheetMono.getCommit())) {
            return Mono.just(true);
        } else {
            return Mono.error(new ProblemSetException("练习册已提交或已批改"));
        }
    }

}

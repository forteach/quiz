package com.forteach.quiz.practiser.repository;

import com.forteach.quiz.practiser.domain.ExerciseAnswerQuestionBook;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-12 14:20
 * @version: 1.0
 * @description: 学生回答作业练习的记录及评分、快照、评价
 */
public interface AskAnswerQuestionBookRepository extends ReactiveMongoRepository<ExerciseAnswerQuestionBook, String> {

    /**
     * @param exeBookType
     * @param courseId
     * @param chapterId
     * @param preview
     * @param classId
     * @param studentId
     * @param questionId
     * @return
     */
//    @Query(value = "{'exeBookType': ?0, 'courseId': ?1, 'chapterId': ?2, 'preview': ?3 'classId': ?4, 'studentId': ?5, 'bigQuestionExerciseBook.questionChildren._id': ?6}"
//            ,
//            fields = "{'_id': 0,'bigQuestionExerciseBook.questionChildren.$': 1}"
//    )
//    @Transactional(readOnly = true, rollbackFor = Exception.class)
//    Mono<ExerciseAnswerQuestionBook> findBigQuestion(final String exeBookType, final String courseId, final String chapterId,
//                                                          final String preview, final String classId, final String studentId, final String questionId);

//    Mono<ExerciseAnswerQuestionBook>
//    findByExeBookTypeAndCourseIdAndChapterIdAndPreviewAndClassIdAndStudentId(final String exeBookType,
//                                                                             final String courseId, final String chapterId,
//                                                                             final String preview, final String classId, final String studentId);
}

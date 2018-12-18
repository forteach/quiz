package com.forteach.quiz.service;

import com.forteach.quiz.domain.ExerciseBook;
import com.forteach.quiz.domain.QuestionIds;
import com.forteach.quiz.repository.ExerciseBookRepository;
import com.forteach.quiz.web.req.ExerciseBookReq;
import com.forteach.quiz.web.vo.BigQuestionVo;
import com.forteach.quiz.web.vo.DelExerciseBookPartVo;
import com.forteach.quiz.web.vo.ExerciseBookVo;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.forteach.quiz.common.Dic.MONGDB_ID;
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

        final Map<String, String> previewMap = exerciseBookVo.getQuestionIds().stream().filter(obj -> isNotEmpty(obj.getPreview())).collect(Collectors.toMap(QuestionIds::getBigQuestionId, QuestionIds::getPreview));

        return examQuestionsService
                .findBigQuestionInId(
                        exerciseBookVo
                                .getQuestionIds()
                                .stream()
                                .map(QuestionIds::getBigQuestionId)
                                .collect(Collectors.toList()))
                .map(bigQuestion -> new BigQuestionVo(previewMap.get(bigQuestion.getId()), idexMap.get(bigQuestion.getId()), bigQuestion))
                .sort(Comparator.comparing(BigQuestionVo::getIndex))
                .collectList()
                .flatMap(vos -> exerciseBookRepository.save(
                        new ExerciseBook<>(
                                exerciseBookVo, vos
                        )
                ));
    }

    /**
     * 查找挂接的课堂练习题
     *
     * @param sortVo
     * @return
     */
    public Mono<List> findExerciseBook(final ExerciseBookReq sortVo) {

        final Criteria criteria = buildExerciseBook(sortVo.getExeBookType(), sortVo.getChapterId(), sortVo.getCourseId());

        Query query = new Query(criteria);

        return reactiveMongoTemplate.findOne(query, ExerciseBook.class).map(ExerciseBook::getQuestionChildren).defaultIfEmpty(new ArrayList());
    }

    /**
     * 删除课堂练习题部分子文档
     *
     * @param delVo
     * @return
     */
    public Mono<UpdateResult> delExerciseBookPart(final DelExerciseBookPartVo delVo) {

        final Criteria criteria = buildExerciseBook(delVo.getExeBookType(), delVo.getChapterId(), delVo.getCourseId());

        Update update = new Update();

        update.pull("questionChildren", Query.query(Criteria.where(MONGDB_ID).is(delVo.getTargetId())));

        return reactiveMongoTemplate
                .updateMulti(Query.query(criteria), update, ExerciseBook.class);
    }

    /**
     * 创建练习册的查询条件
     *
     * @param exeBookType
     * @param chapterId
     * @param courseId
     * @return
     */
    private Criteria buildExerciseBook(final String exeBookType, final String chapterId, final String courseId) {

        Criteria criteria = new Criteria();

        if (isNotEmpty(exeBookType)) {
            criteria.and("exeBookType").in(Integer.parseInt(exeBookType));
        }
        if (isNotEmpty(chapterId)) {
            criteria.and("chapterId").in(chapterId);
        }
        if (isNotEmpty(courseId)) {
            criteria.and("courseId").in(courseId);
        }

        return criteria;
    }
}

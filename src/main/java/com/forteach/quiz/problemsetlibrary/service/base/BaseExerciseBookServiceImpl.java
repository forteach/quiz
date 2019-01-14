package com.forteach.quiz.problemsetlibrary.service.base;

import com.forteach.quiz.domain.QuestionIds;
import com.forteach.quiz.exceptions.CustomException;
import com.forteach.quiz.problemsetlibrary.domain.DelExerciseBookPartVo;
import com.forteach.quiz.problemsetlibrary.domain.base.ExerciseBook;
import com.forteach.quiz.problemsetlibrary.repository.base.ExerciseBookMongoRepository;
import com.forteach.quiz.problemsetlibrary.web.req.ExerciseBookReq;
import com.forteach.quiz.problemsetlibrary.web.vo.ProblemSetVo;
import com.forteach.quiz.questionlibrary.domain.base.QuestionExamEntity;
import com.forteach.quiz.questionlibrary.service.base.BaseQuestionServiceImpl;
import com.forteach.quiz.web.vo.BigQuestionVo;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Mono;

import java.lang.reflect.ParameterizedType;
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
 * @date: 2019/1/13  21:50
 */
public abstract class BaseExerciseBookServiceImpl<T extends ExerciseBook, R extends QuestionExamEntity> implements BaseExerciseBookService<T, R> {

    protected final BaseQuestionServiceImpl<R> questionRepository;
    protected ExerciseBookMongoRepository<T> repository;
    protected ReactiveMongoTemplate template;

    public BaseExerciseBookServiceImpl(ExerciseBookMongoRepository<T> repository,
                                       ReactiveMongoTemplate template,
                                       BaseQuestionServiceImpl<R> questionRepository) {
        this.repository = repository;
        this.template = template;
        this.questionRepository = questionRepository;
    }

    /**
     * 按照顺序 保存练习册
     *
     * @param problemSetVo
     * @return
     */
    @Override
    public Mono<T> buildBook(final ProblemSetVo problemSetVo) {

        final Map<String, Integer> idexMap = problemSetVo.getQuestionIds().stream().collect(Collectors.toMap(QuestionIds::getBigQuestionId, QuestionIds::getIndex));

        final Map<String, String> previewMap = problemSetVo.getQuestionIds().stream().filter(obj -> isNotEmpty(obj.getPreview())).collect(Collectors.toMap(QuestionIds::getBigQuestionId, QuestionIds::getPreview));

        return questionRepository
                .findBigQuestionInId(
                        problemSetVo
                                .getQuestionIds()
                                .stream()
                                .map(QuestionIds::getBigQuestionId)
                                .collect(Collectors.toList()))
                .map(bigQuestion -> new BigQuestionVo<R>(previewMap.get(bigQuestion.getId()), idexMap.get(bigQuestion.getId()), bigQuestion))
                .sort(Comparator.comparing(BigQuestionVo::getIndex))
                .collectList()
                .zipWhen(list ->
                        findExerciseBook(problemSetVo.getChapterId(), problemSetVo.getCourseId()))
                .flatMap(tuple2 -> {
                    if (isNotEmpty(tuple2.getT2().getId())) {
                        tuple2.getT2().setQuestionChildren(tuple2.getT1());
                        return repository.save(tuple2.getT2());
                    } else {
                        return repository.save((T) instantiate(entityClass()).build(new ExerciseBook<>(problemSetVo, tuple2.getT1())));
                    }
                });
    }

    /**
     * 查找挂接的课堂练习题
     *
     * @param sortVo
     * @return
     */
    @Override
    public Mono<List> findExerciseBook(final ExerciseBookReq sortVo) {

        return findExerciseBook(sortVo.getChapterId(), sortVo.getCourseId())
                .map(ExerciseBook::getQuestionChildren).defaultIfEmpty(new ArrayList()).onErrorReturn(new ArrayList());
    }

    /**
     * 查找需要挂接的课堂链接册
     */
    private Mono<T> findExerciseBook(final String chapterId, final String courseId) {

        final Criteria criteria = buildExerciseBook(chapterId, courseId);

        Query query = new Query(criteria);

        return template.findOne(query, entityClass()).defaultIfEmpty(instantiate(entityClass()));
    }

    /**
     * 删除课堂练习题部分子文档
     *
     * @param delVo
     * @return
     */
    @Override
    public Mono<UpdateResult> delExerciseBookPart(final DelExerciseBookPartVo delVo) {

        final Criteria criteria = buildExerciseBook(delVo.getChapterId(), delVo.getCourseId());

        Update update = new Update();

        update.pull("questionChildren", Query.query(Criteria.where(MONGDB_ID).is(delVo.getTargetId())));

        return template
                .updateMulti(Query.query(criteria), update, entityClass());
    }

    /**
     * 创建练习册的查询条件
     *
     * @param chapterId
     * @param courseId
     * @return
     */
    private Criteria buildExerciseBook(final String chapterId, final String courseId) {

        Criteria criteria = new Criteria();

        if (isNotEmpty(chapterId)) {
            criteria.and("chapterId").in(chapterId);
        }
        if (isNotEmpty(courseId)) {
            criteria.and("courseId").in(courseId);
        }

        return criteria;
    }

    private <C> C instantiate(Class<C> c) {
        try {
            return c.newInstance();
        } catch (Exception e) {
            throw new CustomException("反射实例化泛型出错" + e);
        }
    }

    /**
     * 获取泛型的class
     *
     * @return
     */
    private Class<T> entityClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * 获取Question泛型的class
     *
     * @return
     */
    private Class<R> questionClass() {
        return (Class<R>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

}

package com.forteach.quiz.problemsetlibrary.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.domain.QuestionIds;
import com.forteach.quiz.problemsetlibrary.domain.BigQuestionExerciseBook;
import com.forteach.quiz.problemsetlibrary.domain.base.ExerciseBook;
import com.forteach.quiz.problemsetlibrary.repository.base.ExerciseBookMongoRepository;
import com.forteach.quiz.problemsetlibrary.service.base.BaseExerciseBookServiceImpl;
import com.forteach.quiz.problemsetlibrary.web.req.ExerciseBookReq;
import com.forteach.quiz.problemsetlibrary.web.vo.DelExerciseBookPartVo;
import com.forteach.quiz.problemsetlibrary.web.vo.ProblemSetVo;
import com.forteach.quiz.problemsetlibrary.web.vo.UnwindedBigQuestionexerciseBook;
import com.forteach.quiz.questionlibrary.domain.BigQuestion;
import com.forteach.quiz.questionlibrary.service.base.BaseQuestionServiceImpl;
import com.forteach.quiz.web.vo.BigQuestionVo;
import com.forteach.quiz.web.vo.PreviewChangeVo;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

import static com.forteach.quiz.common.Dic.EXE_BOOKTYPE_PREVIEW;
import static com.forteach.quiz.common.Dic.MONGDB_ID;
import static com.forteach.quiz.util.StringUtil.isNotEmpty;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/13  22:43
 */
@Service
public class BigQuestionExerciseBookService extends BaseExerciseBookServiceImpl<BigQuestionExerciseBook, BigQuestion> {

    public BigQuestionExerciseBookService(ExerciseBookMongoRepository<BigQuestionExerciseBook> repository,
                                          ReactiveMongoTemplate template,
                                          BaseQuestionServiceImpl<BigQuestion> questionRepository) {
        super(repository, template, questionRepository);
    }

    /**
     * 按照顺序 保存练习册
     *
     * @param problemSetVo
     * @return
     */
    @Override
    public Mono<BigQuestionExerciseBook> buildBook(final ProblemSetVo problemSetVo) {

        final Map<String, Integer> idexMap = problemSetVo.getQuestionIds().stream().collect(Collectors.toMap(QuestionIds::getBigQuestionId, QuestionIds::getIndex));

        final Map<String, String> previewMap = problemSetVo.getQuestionIds().stream().filter(obj -> isNotEmpty(obj.getPreview())).collect(Collectors.toMap(QuestionIds::getBigQuestionId, QuestionIds::getPreview));

        return questionRepository
                .findBigQuestionInId(
                        problemSetVo
                                .getQuestionIds()
                                .stream()
                                .map(QuestionIds::getBigQuestionId)
                                .collect(Collectors.toList()))
                .map(bigQuestion -> new BigQuestionVo<BigQuestion>(previewMap.get(bigQuestion.getId()), String.valueOf(idexMap.get(bigQuestion.getId())), bigQuestion))
                .sort(Comparator.comparing(BigQuestionVo::getIndex))
                .collectList()
                .zipWhen(list -> findExerciseBook(String.valueOf(problemSetVo.getExeBookType()), problemSetVo.getChapterId(), problemSetVo.getCourseId()))
                .flatMap(tuple2 -> {
                    if (isNotEmpty(tuple2.getT2().getId())) {
                        tuple2.getT2().setQuestionChildren(tuple2.getT1());
                        return repository.save(tuple2.getT2());
                    } else {
                        return repository.save(new BigQuestionExerciseBook(problemSetVo, tuple2.getT1()));
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
    public Mono<List<BigQuestion>> findExerciseBook(final ExerciseBookReq sortVo) {

        return findExerciseBook(sortVo.getExeBookType(), sortVo.getChapterId(), sortVo.getCourseId(), sortVo.getPreview())
                .filter(Objects::nonNull)
                .map(ExerciseBook::getQuestionChildren);
    }


    /**
     * 查找需要挂接的课堂链接册
     */
    public Mono<BigQuestionExerciseBook> findExerciseBook(final String exeBookType, final String chapterId, final String courseId, final String preview) {

        Criteria criteria = buildExerciseBook(exeBookType, chapterId, courseId);
        if (StrUtil.isNotBlank(preview)){
            criteria.and("questionChildren.preview").is(preview);
        }

        Aggregation agg = newAggregation(
                unwind("questionChildren"),
                match(criteria)
        );
        return template.aggregate(agg, "bigQuestionexerciseBook", UnwindedBigQuestionexerciseBook.class)
                .next()
                .flatMap(unwindedBigQuestionexerciseBook -> {
                    BigQuestionExerciseBook bigQuestionExerciseBook = new BigQuestionExerciseBook();
                    BeanUtils.copyProperties(unwindedBigQuestionexerciseBook, bigQuestionExerciseBook);
                    List list = new ArrayList();
                    list.add(unwindedBigQuestionexerciseBook.getQuestionChildren());
                    bigQuestionExerciseBook.setQuestionChildren(list);
                    return Mono.just(bigQuestionExerciseBook);
                })
                .defaultIfEmpty(new BigQuestionExerciseBook());
    }

    /**
     * 查找需要挂接的课堂链接册
     */
    public Mono<BigQuestionExerciseBook> findExerciseBook(final String exeBookType, final String chapterId, final String courseId) {

        final Criteria criteria = buildExerciseBook(exeBookType, chapterId, courseId);

        Query query = new Query(criteria);

        return template.findOne(query, BigQuestionExerciseBook.class).defaultIfEmpty(new BigQuestionExerciseBook());
    }

    /**
     * 删除课堂练习题部分子文档
     *
     * @param delVo
     * @return
     */
    @Override
    public Mono<UpdateResult> delExerciseBookPart(final DelExerciseBookPartVo delVo) {

        final Criteria criteria = buildExerciseBook(delVo.getExeBookType(), delVo.getChapterId(), delVo.getCourseId());

        Update update = new Update();

        update.pull("questionChildren", Query.query(Criteria.where(MONGDB_ID).is(delVo.getTargetId())));

        return template.updateMulti(Query.query(criteria), update, BigQuestionExerciseBook.class);
    }

    /**
     * 编辑练习册 预习类型
     *
     * @param changeVo
     * @return
     */
    public Mono<Boolean> editPreview(final PreviewChangeVo changeVo) {

        final Criteria criteria = buildExerciseBook(EXE_BOOKTYPE_PREVIEW, changeVo.getChapterId(), changeVo.getCourseId())
                .and("questionChildren." + MONGDB_ID).is(new ObjectId(changeVo.getTargetId()));
        Update update = Update.update("questionChildren.$.preview", changeVo.getPreview());
        update.set("uDate", DateUtil.now());

        return template.updateMulti(Query.query(criteria), update, BigQuestionExerciseBook.class)
                .flatMap(updateResult -> MyAssert.isFalse(updateResult.wasAcknowledged(), DefineCode.ERR0013, "修改失败"));
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
            criteria.and("exeBookType").is(Integer.parseInt(exeBookType));
        }
        if (isNotEmpty(chapterId)) {
            criteria.and("chapterId").is(chapterId);
        }
        if (isNotEmpty(courseId)) {
            criteria.and("courseId").is(courseId);
        }

        return criteria;
    }
}

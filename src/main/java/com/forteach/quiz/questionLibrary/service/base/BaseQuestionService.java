package com.forteach.quiz.questionLibrary.service.base;

import com.forteach.quiz.questionLibrary.domain.QuestionExamEntity;
import com.forteach.quiz.questionLibrary.web.req.QuestionBankReq;
import com.mongodb.client.result.UpdateResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @Description: 问题结构的基础服务
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/10  11:52
 */
public interface BaseQuestionService<T extends QuestionExamEntity> {

    /**
     * 修改新增判断题
     *
     * @param bigQuestion
     * @param obj
     * @return
     */
    Mono<T> editQuestion(final T bigQuestion, final Class obj);

    /**
     * 题目分享
     *
     * @param questionBankId
     * @param teacherId
     * @return
     */
    Mono<Boolean> questionBankAssociationAdd(final String questionBankId, final String teacherId);

    /**
     * 删除单道题
     *
     * @param id
     * @return
     */
    Mono<Void> delQuestions(final String id);

    /**
     * 查询详细或全部字段的问题
     *
     * @param sortVo
     * @return
     */
    Flux<T> findAllDetailed(final QuestionBankReq sortVo);

    /**
     * 根据id查询详细
     *
     * @param id
     * @return
     */
    Mono<T> findOneDetailed(final String id);

    /**
     * 修改是是否更新到课后练习册
     *
     * @param bigQuestion
     * @return
     */
    Mono<T> editQuestions(final T bigQuestion);

    /**
     * 更新题目与教师关系信息
     *
     * @param questionBankId
     * @param teacherId
     * @return
     */
    Mono<UpdateResult> questionBankAssociation(final String questionBankId, final String teacherId);
}

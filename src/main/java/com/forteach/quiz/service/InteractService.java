package com.forteach.quiz.service;

import com.forteach.quiz.web.pojo.CircleAnswer;
import com.forteach.quiz.web.pojo.Students;
import com.forteach.quiz.web.vo.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/19  10:25
 */
public interface InteractService {

    /**
     * 发送问题
     *
     * @param giveVo
     * @return
     */
    Mono<Long> sendQuestion(final GiveVo giveVo);

    /**
     * 老师获取实时学生的答题情况
     *
     * @param achieves
     * @return
     */
    Mono<List<CircleAnswer>> achieveAnswer(final AchieveAnswerVo achieves);

    /**
     * 实时获取问题
     *
     * @param achieveVo
     * @return
     */
    Mono<AskQuestionVo> achieveQuestion(final AchieveVo achieveVo);

    /**
     * 提交答案
     *
     * @param answerVo
     * @return
     */
    Mono<String> sendAnswer(final InteractAnswerVo answerVo);

    /**
     * 课堂提问
     * 重新发起举手
     *
     * @param askLaunchVo
     * @return
     */
    Mono<Long> launchRaise(final AskLaunchVo askLaunchVo);

    /**
     * 课堂提问
     * 学生举手
     *
     * @param raisehandVo
     * @return
     */
    Mono<Long> raiseHand(final RaisehandVo raisehandVo);

    /**
     * 老师获取实时举手的学生
     *
     * @param achieveRaiseVo
     * @return
     */
    Mono<List<Students>> achieveRaise(final AchieveRaiseVo achieveRaiseVo);


}

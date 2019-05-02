package com.forteach.quiz.interaction.execute.service.Key;


/**
 * @Description:
 * @author: zjw
 * @version: V1.0
 * @date: 2018/11/9 11:03
 */
public class SingleQueKey {

    /**
     * 当前课堂当前互动名称
     */
    public static final String CLASSROOM_NOW_INTERACT= "nowInteract";

    /**
     * 互动提问hash前缀(习题库\头脑风暴等。。。)
     */
    public static final String CLASSROOM_ASK_NOW = "now";

    /**
     * 互动提问hash前缀(习题库\头脑风暴等。。。)
     */
    public static final String CLASSROOM_ASK_PRVE = "prve";

    /**
     * 互动方式为提问
     */
    public static final String CLASSROOM_ASK_QUESTIONS_ID = "ask";


    //缓存当前已发布的题目题干内容，不分课堂。
    public static String questionsNow(String questionId) {
        return CLASSROOM_ASK_NOW.concat(questionId);
    }
    /**
     * 课堂题目当前前缀
     *
     * @return now+课堂Id=map
     */
    public static String questionsIdNow(String circleId) {
        return CLASSROOM_ASK_NOW.concat(circleId);
    }

    /**
     * 课堂题目上一题前缀
     *
     * @return 上一次的问题前缀+课堂+问题类型+回答方式=ForValu
     */
    public static String askTypeQuestionsIdPrve(final String questType,final String circleId,final String interactive) {
        return CLASSROOM_ASK_PRVE.concat(askTypeQuestionsId(questType,circleId,interactive));
    }

    /**
     * 课堂题目当前前缀
     *
     * @return 当前问题前缀+课堂+问题类型+回答方式=ForValue
     */
    public static String askTypeQuestionsIdNow(final String questionType, String circleId, String interactive) {
        return CLASSROOM_ASK_NOW + askTypeQuestionsId(questionType, circleId, interactive);
    }

    /**
     * 课堂互动前缀
     *
     * @return 问题前缀+课堂+问题类型  题目列表set
     */
    public static String askTypeQuestionsId(final String questType,final String circleId) {
        return circleId.concat(CLASSROOM_ASK_QUESTIONS_ID).concat(questType);
    }

    /**
     * 课堂互动前缀
     *
     * @return 问题前缀+课堂+问题类型+回答方式  题目列表List
     */
    public static String askTypeQuestionsId(final String questionType, String circleId, String interactive) {
        return circleId.concat(SingleQueKey.CLASSROOM_ASK_QUESTIONS_ID).concat(questionType.concat(interactive));
    }

}

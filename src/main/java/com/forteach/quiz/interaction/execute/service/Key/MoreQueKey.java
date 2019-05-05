package com.forteach.quiz.interaction.execute.service.Key;

/**
 * @Description:
 * @author: zjw
 * @version: V1.0
 * @date: 2018/11/9 11:03
 */
public class MoreQueKey {

    /**
     * 互动练习册发布
     */
    public static final String CLASSROOM_BOOK_QUESTIONS_LIST = "bookList";

    /**
     * 课堂练习册
     */
    public static final String CLASSROOM_BOOK_NOW = "nowBook";

    /**
     * 互动练习册的题目
     */
    public static final String CLASSROOM_BOOK_QUESTIONS_MAP = "bookMap";

    /**
     * 互动方式为练习
     */
    public static final String CLASSROOM_BOOK_QUESTIONS_ID = "book";

    /**
     * 课堂题目当前前缀
     *
     * @return now+课堂Id=map
     */
    public static String questionsBookNowMap(final String typeName, final String circleId) {
        return CLASSROOM_BOOK_NOW.concat(circleId).concat(typeName);
    }

    /**
     * 课堂练习测题目
     * @param circleId
     * @return
     */
    public static String bookQuestionMap(final String typeName,final String circleId){
        return circleId.concat(CLASSROOM_BOOK_QUESTIONS_MAP).concat(typeName);
    }

    /**
     * 课堂多题目活动互动前缀
     *
     * @return 题目列表List
     */
    public static String bookTypeQuestionsList(final String typeName, final String circleId) {
        return circleId.concat(CLASSROOM_BOOK_QUESTIONS_LIST).concat(typeName);
    }

}

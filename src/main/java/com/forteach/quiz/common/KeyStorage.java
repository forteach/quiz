package com.forteach.quiz.common;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/9 11:03
 */
public class KeyStorage {

    /**
     * 互动提问hash 前缀
     */
    public static final String CLASSROOM_ASK_QUESTIONS_ID = "classAsk";

    /**
     *
     */
    public static final String CLASSROOM_ASK_QUESTIONS_DISTINCT = "distinctAsk";

    public static final String CLASSROOM_ASK_QUESTIONS_RACE = "askRace";

    /**
     * 学生信息从redis 取出通过从[oracle 数据库取出保存进入(redis) hash]
     */
    public static final String STUDENT_ADO = "studentsData$";

    public static final String RAISE_HAND_STUDENT_DISTINCT = "distinctRaiseHand";

    /**
     *
     */
    public static final String ANSW_HAND_STUDENT_DISTINCT = "distinctAnswHand";

    /**
     * 课堂提问答案等前缀
     */
    public static final String EXAMINEE_IS_REPLY_KEY = "askExamineeIsReply";



}

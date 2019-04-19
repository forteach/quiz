package com.forteach.quiz.interaction.execute.service.Key;

/**
 * @Description: 题目回答键值
 * @author: zjw
 * @version: V1.0
 * @date: 2018/11/9 11:03
 */
public class AchieveAnswerKey {

    /**
     * 已回答
     */
    public static final String ASK_CIRCLE_ANSWER_DID = "2";

    /**
     * 互动提问hash前缀(习题库\头脑风暴等。。。)
     */
    public static final String CLASSROOM_ASK_NOW= "now";

    //刷新清除场景命名问题回答场景
    public static final String CLASSROOM_CLEAR_TAG_ANSWER="answer";


    /**
     * 课堂当前道题目回答前缀
     * sutId  学生ID
     * questionId 问题ID
     * typeName  题目互动方式  提问、联练习。。。。
     * @return 单个题目ID+前缀+学生编号=题目答案  Hashmap
     */
    public static String answerTypeQuestionsId(final String circleId,String questionId,String typeName) {
        return questionId.concat("answer").concat(typeName).concat(circleId);
    }

    /**
     * 课堂当前道题目回答学生前缀
     * sutId  学生ID
     * questionId 问题ID
     * typeName  题目互动方式  提问、联练习。。。。
     * @return 单个题目ID+前缀+学生编号=题目答案=Hashmap
     */
    public static String tiJiaoanswerTypeQuestStuSet(final String circleId,final String questionId,final String typeName) {
        return questionId.concat("answerStuSet").concat(typeName).concat(circleId);
    }

    /**
     * 课堂当前道题目回答批改前缀
     * sutId  学生ID
     * questionId 问题ID
     * typeName  题目互动方式  提问、联练习。。。。
     * @return 单个题目ID+前缀+学生编号=题目答案=Hashmap
     */
    public static String piGaiTypeQuestionsId(final String circleId,String questionId,String typeName) {
        return questionId.concat("pigai").concat(typeName).concat(circleId);
    }

    /**
     * 课堂当前道题目回答学生列表前缀
     * sutId  学生ID
     * questionId 问题ID
     * typeName  题目互动方式  提问、联练习。。。。
     * @return 单个题目ID+前缀+学生编号=题目答案=Hashmap
     */
    public static String answerTypeQuestStuList(final String circleId,String questionId,String typeName) {
        //TODO 需要处理为空??
        return questionId.concat("answerlist").concat(typeName).concat(circleId);
    }

}

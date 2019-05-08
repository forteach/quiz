package com.forteach.quiz.interaction.execute.service.Key;

/**
 * @Description: 题目回答键值
 * @author: zjw
 * @version: V1.0
 * @date: 2018/11/9 11:03
 */
public class AchieveAnswerKey {

    /**
     * 加入课堂，已推送过得学生回答
     */
    public static final String ROOM_JOIN_ANSW_TS = "RoomJoinAnsw";

    /**
     * 拉取操作
     */
    public static final String ASK_PUSH = "Push";

    /**
     * 推送操作
     */
    public static final String ASK_PULL = "Pull";

    //抢答
    public static final String ASK_INTERACTIVE_RACE = "race";
    //举手
    public static final String ASK_INTERACTIVE_RAISE = "raise";
    //选人
    public static final String ASK_INTERACTIVE_SELECT = "select";

    public static final String ASK_INTERACTIVE_VOTE = "vote";


    /**
     * 课堂当前道题目回答前缀
     * sutId  学生ID
     * questionId 问题ID
     * questionType  题目互动方式  提问、联练习。。。。
     * @return 单个题目ID+前缀+学生编号=题目答案  Hashmap
     */
    public static String answerTypeQuestionsId(final String circleId,String questionId,String questionType) {
        return circleId.concat(questionId).concat("Answer").concat(questionType);
    }

    /**
     * 课堂当前道题目回答学生前缀
     * sutId  学生ID
     * questionId 问题ID
     * questionType  题目互动方式  提问、联练习。。。。
     * @return 单个题目ID+前缀+学生编号=题目答案=Hashmap
     */
    public static String tiJiaoanswerTypeQuestStuSet(final String circleId,final String questionId,final String questionType) {
        return circleId.concat(questionId).concat("AnswerStuSet").concat(questionType);
    }

    /**
     * 课堂当前道题目回答批改前缀
     * sutId  学生ID
     * questionId 问题ID
     * questionType  题目互动方式  提问、联练习。。。。
     * @return 单个题目ID+前缀+学生编号=题目答案=Hashmap
     */
    public static String piGaiTypeQuestionsId(final String circleId,String questionId,String questionType) {
        return circleId.concat(questionId).concat("PiGai").concat(questionType);
    }

    /**
     * 课堂当前道题目回答学生列表前缀
     * sutId  学生ID
     * questionId 问题ID
     * questionType  题目互动方式  提问、联练习。。。。
     * @return 单个题目ID+前缀+学生编号=题目答案=Hashmap
     */
    public static String answerTypeQuestStuList(final String circleId,String questionId,String questionType) {
        //TODO 需要处理为空??
        return circleId.concat(questionId).concat("AnswerList").concat(questionType);
    }

    /**
     * 清楚题目回答的学生键值
     * @param circleId
     * @param questionId
     * @return
     */
    public static String cleanAnswerHasJoin(String circleId,String questionId,String questionType){
        return circleId.concat(questionId).concat(ROOM_JOIN_ANSW_TS).concat(questionType).concat(ASK_PUSH);
    }

}

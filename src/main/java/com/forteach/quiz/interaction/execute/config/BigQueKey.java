package com.forteach.quiz.interaction.execute.config;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/9 11:03
 */
public class BigQueKey {
//
//    /**
//     * 互动提问hash前缀(习题库\头脑风暴等。。。)
//     */
//    public static final String CLASSROOM_ASK_NOW= "now";
//
//    /**
//     * 互动提问hash前缀(习题库\头脑风暴等。。。)
//     */
//    public static final String CLASSROOM_ASK_PRVE= "prve";
//
//    /**
//     * 课堂提问答案等前缀
//     */
//    public static final String EXAMINEE_IS_REPLY_KEY = "askReply";
//
//    /**
//     * 课堂问题前缀
//     */
//    public static final String CLASSROOM_ASK_QUESTIONS_RACE = "askRace";
//
//    /**
//     * 课堂练习册
//     */
//    public static final String CLASSROOM_BOOK_NOW= "nowBook";
//
//    public static final String CLASSROOM_BOOK_INFO= "nowBookInfo";
//
//    /**
//     * 互动提问hash前缀(习题库\头脑风暴等。。。)
//     */
//    public static final String CLASSROOM_ASK_QUESTIONS_ID = "ask";
//
//    /**
//     * 互动练习册发布
//     */
//    public static final String CLASSROOM_BOOK_QUESTIONS_LIST = "bookList";
//
//    /**
//     * 互动练习册的题目
//     */
//    public static final String CLASSROOM_BOOK_QUESTIONS_MAP = "bookMap";
//
//    /**
//     * 互动练习册回答题目
//     */
//    public static final String CLASSROOM_BOOK_QUESTIONS_ANSWER_MAP = "bookAnswerMap";
//
//    /**
//     * 缓存当前已发布的题目题干内容，不分课堂。
//     * @param questionId
//     * @return
//     */
//    public static String questionsNow(final String questionId) {
//        return CLASSROOM_ASK_NOW.concat(questionId);
//    }
//
////    /**
////     * 缓存当前已发布的练习册题目题干内容，不分课堂。
////     * @param circleId
////     * @return
////     */
////    public static String bookQuestionsInfoNow(final String circleId) {
////        return CLASSROOM_BOOK_INFO.concat(circleId);
////    }
//
//    /**
//     * 课堂提问当前前缀
//     *
//     * @return now+课堂Id=map
//     */
//    public static String questionsIdNow(final String circleId) {
//        return CLASSROOM_ASK_NOW.concat(circleId);
//    }
//
//        /**
//         * 课堂题目当前前缀
//         *
//         * @return now+课堂Id=map
//         */
//        public static String questionsBookNow(final String circleId) {
//            return CLASSROOM_BOOK_NOW.concat(circleId);
//        }
//
//
//    /**
//     * 课堂题目当前前缀
//     *
//     * @return 当前问题前缀+课堂+问题类型+回答方式=ForValue
//     */
//    public static String askTypeQuestionsIdNow(final String questType, final String circleId, final String interactive) {
//        return CLASSROOM_ASK_NOW.concat(askTypeQuestionsId(questType,circleId,interactive));
//    }
//
//    /**
//     * 课堂互动前缀
//     *
//     * @return 问题前缀+课堂+问题类型+回答方式  题目列表List
//     */
//    public static String askTypeQuestionsId(final String questType,final String circleId,final String interactive) {
//        return circleId.concat(BigQueKey.CLASSROOM_ASK_QUESTIONS_ID).concat(questType.concat(interactive));
//    }
//
//    /**
//     * 课堂练习互动前缀
//     *
//     * @return  题目列表List
//     */
//    public static String bookTypeQuestionsList(String typeName,final String circleId) {
//        return circleId.concat(CLASSROOM_BOOK_QUESTIONS_LIST);
//    }
//
//    /**
//     * 课堂练习测题目
//     * @param circleId
//     * @return
//     */
//    public static String bookQuestionMap(final String circleId){
//        return circleId.concat(CLASSROOM_BOOK_QUESTIONS_MAP);
//    }
//    /**
//     * 课堂练习册回答题目=回答者和答案
//     *
//     * @return   题目列表map
//     */
//    public static String bookQuestionsAnswerMap(final String circleId,final String questionId) {
//        return circleId.concat(BigQueKey.CLASSROOM_BOOK_QUESTIONS_ANSWER_MAP).concat(questionId);
//    }
//
//    /**
//     * 课堂互动前缀
//     *
//     * @return 问题前缀+课堂+问题类型  题目列表set
//     */
//    public static String askTypeQuestionsId(final String questType,final String circleId) {
//        return circleId.concat(BigQueKey.CLASSROOM_ASK_QUESTIONS_ID).concat(questType);
//    }
//
//    /**
//     * 课堂题目上一题前缀
//     *
//     * @return 上一次的问题前缀+课堂+问题类型+回答方式=ForValu
//     */
//    public static String askTypeQuestionsIdPrve(final String questType,final String circleId,final String interactive) {
//        return CLASSROOM_ASK_PRVE.concat(askTypeQuestionsId(questType,circleId,interactive));
//    }
//
//    /**
//     * 课堂所选单道题目前缀
//     *
//     * @return 问题前缀+课堂+问题类型+回答方式+单个题目ID=ForSet
//     */
//    public static String askTypeQuestionsId(final String questType,final String circleId,final String interactive,final String questionId) {
//        return askTypeQuestionsId(questType,circleId,interactive).concat(questionId);
//    }
//
//    /**
//     * 课堂当前道题目回答前缀
//     * sutId  学生ID
//     * questionId 问题ID
//     * typeName  题目互动方式  提问、练习册。。。。
//     * @return 单个题目ID+前缀+学生编号=题目答案  Hashmap
//     */
//    public static String answerTypeQuestionsId(final String circleId,final String questionId,final String typeName) {
//        return questionId.concat("answer").concat(typeName).concat(circleId);
//    }
//
//    /**
//     * 课堂当前道题目回答批改前缀
//     * sutId  学生ID
//     * questionId 问题ID
//     * typeName  题目互动方式  提问、联练习。。。。
//     * @return 单个题目ID+前缀+学生编号=题目答案=Hashmap
//     */
//    public static String piGaiTypeQuestionsId(final String circleId,final String questionId,final String typeName) {
//        return questionId.concat("pigai").concat(typeName).concat(circleId);
//    }
//
//    /**
//     * 课堂当前道题目回答学生前缀
//     * sutId  学生ID
//     * questionId 问题ID
//     * typeName  题目互动方式  提问、联练习。。。。
//     * @return 单个题目ID+前缀+学生编号=题目答案=Hashmap
//     */
//    public static String tiJiaoanswerTypeQuestStuSet(final String circleId,final String questionId,final String typeName) {
//        return questionId.concat("answerStuSet").concat(typeName).concat(circleId);
//    }
//
//    /**
//     * 课堂当前道题目回答学生列表前缀
//     * sutId  学生ID
//     * questionId 问题ID
//     * typeName  题目互动方式  提问、联练习。。。。
//     * @return 单个题目ID+前缀+学生编号=题目答案=Hashmap
//     */
//    public static String answerTypeQuestStuList(final String circleId,final String questionId,final String typeName) {
//        return questionId.concat("answerlist").concat(typeName).concat(circleId);
//    }
//
//    /**
//     * 课堂题目互动类型前缀
//     *
//     * @return 问题前缀+课堂+问题类型+回答方式+单个题目ID，用于改题目过期判断数据依据
//     */
//    public static String askTypeQuestionsIdType(final String circleId,final String questionId) {
//        return  circleId.concat("asknow").concat(questionId);
//    }
//

}

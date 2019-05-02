package com.forteach.quiz.interaction.execute.service.Key;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/9 11:03
 */
public class ClassRoomKey {

    /**
     * 当前课堂当前互动名称
     */
    public static final String CLASSROOM_NOW_INTERACT= "nowInteract";

    /**
     * 课堂相关信息ID-Redis的编码前缀
     */
    public static final String CLASS_ROOM_QR_CODE_PREFIX = "RoomMember";

    /**
     * 老师创建临时课堂前缀
     */
    public static final String INTERACTIVE_CLASSROOM = "RoomTeacher";

    /**
     * 老师创建临时课堂前缀
     */
    public static final String OPEN_CLASSROOM = "OpenRoom";

    /**
     *临时课堂的随机数前缀
     */
    public static final String OPEN_CLASSROOM_RANDOM = "OpenRoomRandom";

    /**
     * 加入课堂，已推送过得学生
     */
    public static final String ROOM_JOIN_STU_TS = "RoomJoinStu";

    /**
     * 加入课堂，已推送过得学生回答
     */
    public static final String ROOM_JOIN_ANSW_TS = "RoomJoinAnsw";

    /**
     * 加入课堂，已推送过得学生回答
     */
    public static final String ROOM_JOIN_RAISE_TS = "RoomJoinRaise";

    /**
     * 互动方式为加入学生
     */
    public static final String CLASSROOM_JOIN_QUESTIONS_ID = "joinStu";

    /**
     * 设置当前活动KEY
     *
     * @return
     */
    public static String setInteractionType(final String circleId) {
        return CLASSROOM_NOW_INTERACT.concat(circleId);
    }

    /**
     * 课堂所有学生
     * @param circleId
     * @return
     */
    public static String getInteractiveIdQra(String circleId){
        return circleId.concat(ClassRoomKey.CLASS_ROOM_QR_CODE_PREFIX);
    }


    /**
     * 课堂的上课教师
     * @param circleId
     * @return
     */
    public static String getRoomTeacherKey(String circleId){
        return circleId.concat(ClassRoomKey.INTERACTIVE_CLASSROOM);
    }

    /**
     * 获得用户scoket连接的随机数
     * @param circleId
     * @param uid
     * @return
     */
    public static String getOpenClassRandom(String circleId,String uid){
        return circleId.concat(OPEN_CLASSROOM_RANDOM).concat(uid);
    }

    /**
     * 获得用户scoket连接的随机数改变状态
     * @param circleId
     * @param uid
     * @param tagType 清空标记的场景类型 加入学生  举手  回答
     * @return
     */
    public static String getOpenClassRandomTag(String circleId,String uid,String tagType){
        return circleId.concat(OPEN_CLASSROOM_RANDOM.concat(tagType).concat("tag")).concat(uid);
    }


    /**
     * 课堂创建信息
     * @param teacherId
     * @return
     */
    public static String getRoomKey(String teacherId){

        return ClassRoomKey.INTERACTIVE_CLASSROOM.concat(teacherId);
    }


    /**
     * 加入课堂，已推送过得学生
     * @param circleId
     * @return
     */
    public static String getJoinTuisongStuKey(String circleId){
        return circleId.concat(ClassRoomKey.ROOM_JOIN_STU_TS);
    }

    /**
     * 加入课堂，已推送过的学生题目回答
     * @param circleId
     * @param questionId
     * @return
     */
    public static String getJoinTuisongAnswerKey(String circleId,String questionId){
        return circleId.concat(questionId).concat(ClassRoomKey.ROOM_JOIN_ANSW_TS);
    }

    /**
     * 加入课堂，已推送过的学生题目回答
     * @param circleId
     * @param questionId
     * @return
     */
    public static String getJoinTuisongRaiseKey(String circleId,String questionId){
        return circleId.concat(questionId).concat(ClassRoomKey.ROOM_JOIN_RAISE_TS);
    }

}

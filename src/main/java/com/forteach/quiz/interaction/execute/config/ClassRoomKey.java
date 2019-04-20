package com.forteach.quiz.interaction.execute.config;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/9 11:03
 */
public class ClassRoomKey {

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

    //课堂所有学生
    public static String getInteractiveIdQra(final String circleId){
        return circleId.concat(ClassRoomKey.CLASS_ROOM_QR_CODE_PREFIX);
    }

    //课堂创建信息
//    public static String getRoomKey(final String teacherId){
//        return ClassRoomKey.INTERACTIVE_CLASSROOM.concat(teacherId);
//    }

    //课堂的上课教师
    public static String getRoomTeacherKey(final String circleId){
        return circleId.concat(ClassRoomKey.INTERACTIVE_CLASSROOM);
    }

}
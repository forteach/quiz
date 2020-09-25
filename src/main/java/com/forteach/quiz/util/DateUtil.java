package com.forteach.quiz.util;

import java.util.Calendar;
import java.util.Date;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/4  17:47
 */
public class DateUtil {
    public static Date getStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    public static Date getEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }

    public static int getSemesterByNow() {
        /* 如果在9、10、11、12、1月，为此学年第 2 学期，
         *     其中在9、10、11、12月为 year 学年，1月为 year-1 学年。
         * 如果在2、3、4、5、6、7、8月，为此学年第 2 学期，
         *     year-1学年。
         * 计算当前的学年学期
         */
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int term = 1;
        if (month > 2 && month < 9) {
            term = 2;
        }
        return term;
    }
}

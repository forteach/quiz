package com.forteach.quiz.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.UUID;


/**
 * @Description:
 * @version: V1.0
 * @author: liu zhenming
 * @Email: 1119264845@qq.com
 * @Date: 2018-07-11 9:31
 */
public class StringUtil {

    private static ObjectMapper objectMapper;

    /**
     * @return
     * @Description: 获取32位的随机UUID
     * @author: liu zhenming
     * @Date: 2018/7/10 9:34
     */
    public static String getRandomUUID() {
        return String.join("", UUID.randomUUID().toString().split("-"));
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static <T> T readValue(String jsonStr, Class<T> valueType) {

        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }

        try {
            return objectMapper.readValue(jsonStr, valueType);
        } catch (IOException e) {
            throw new RuntimeException("json 转换失败 " + e.getMessage());
        }
    }


}
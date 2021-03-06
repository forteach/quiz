package com.forteach.quiz.util;

import com.forteach.quiz.common.WebResult;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/6/15  0:15
 */
@Slf4j
public class PropertiesUtil {

    public static Properties getProperties() {
        Properties prop = new Properties();
        InputStream in = WebResult.class.getClassLoader().getResourceAsStream("webResult.properties");
        try {
            prop.load(in);
        } catch (IOException e) {
            log.error("返回信息时，获取配置文件出错", e.getMessage(), e);
        }
        return prop;
    }

    public static Map<String, String> getMapForProperties() {
        Properties prop = new Properties();
        InputStreamReader inputStream = new InputStreamReader(Objects.requireNonNull(WebResult.class.getClassLoader().getResourceAsStream("webResult.properties")), StandardCharsets.UTF_8);
        try {
            prop.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            log.error("返回信息时，获取配置文件出错", e.getMessage(), e);
        }
        return propertiesToMap(prop);
    }

    private static Map<String, String> propertiesToMap(Properties properties) {
        return new HashMap<String, String>((Map) properties);
    }

}


package com.forteach.quiz.common;

import com.forteach.quiz.util.PropertiesUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.Map;

/**
 * @Description:
 * @version: V1.0
 * @author: liu zhenming
 * @Email: 1119264845@qq.com
 * @Date: 2018-07-11 13:37
 */
@Data
@Slf4j
public class WebResult implements Serializable {

    private int ret;
    private Object data;
    private String msg;

    /**
     * 成功操作 操作码默认为0   无参
     **/
    public static WebResult okResult() {
        return okResult(getOkCode(), "");
    }

    /**
     * 成功操作 操作码默认为0   只有提示码
     **/
    public static WebResult okResult(int code) {
        return okResult(getOkCode(), String.valueOf(code), "");
    }

    /**
     * 成功操作 操作码默认为0   只有返回数据
     **/
    public static WebResult okResult(Object data) {
        return okResult(getOkCode(), data);
    }

    /**
     * 成功操作 操作码默认为0   只有提示码 和 数据
     **/
    public static WebResult okResult(int code, Object data) {
        return okResult(getOkCode(), String.valueOf(code), data);
    }

    public static WebResult okCustomResult(int code, String msg) {
        return customResult(code, msg, "");
    }

    /**
     * 内部使用
     **/
    private static WebResult okResult(int code, String windowCode, Object data) {
        return getWebResult(code, windowCode, data);
    }

    private static Integer getOkCode() {
        Map<String, String> propertiesmap = PropertiesUtil.getMapForProperties();
        return Integer.valueOf(propertiesmap.get("0[ret]"));
    }

    private static Integer getFailCode() {
        Map<String, String> propertiesmap = PropertiesUtil.getMapForProperties();
        return Integer.valueOf(propertiesmap.get("9999[ret]"));
    }

    /**
     * ##### 失败 返回 ######
     * 只传入一个失败码
     **/
    public static WebResult failResult(int code) {
        return failResult(code, "");
    }

    /**
     * 失败操作 操作码默认为9999   只有返回数据
     **/
    public static WebResult failResult(Object data) {
        return failResult(getFailCode(), data);
    }

    /**
     * 失败操作 操作码默认为9999  只有提示码 和 数据
     **/
    public static WebResult failResult(int code, Object data) {
        return failResult(getFailCode(), String.valueOf(code), data);
    }

    public static WebResult failResult(int code, String windowCode, Object object) {
        return getWebResult(code, windowCode, object);
    }

    private static WebResult getWebResult(int code, String windowCode, Object object) {
        Map<String, String> propertiesmap = PropertiesUtil.getMapForProperties();
        WebResult wr = new WebResult();
        wr.setData(object);
        wr.setRet(code);
        wr.setMsg(propertiesmap.get(Integer.valueOf(windowCode) + "[msg]"));
        return wr;
    }


    public static WebResult failException() {
        return failResult(9999);
    }

    public static WebResult failException(String eMsg) {
        WebResult wr = new WebResult();
        wr.setRet(9999);
        wr.setMsg(eMsg);
        wr.setData("");
        return wr;
    }


    private static WebResult backResult(int code, Object data) {
        Map<String, String> map = PropertiesUtil.getMapForProperties();
        WebResult wr = new WebResult();
        wr.setRet(code);
        wr.setData(data);
        wr.setMsg(map.get("0[msg]"));
        return wr;
    }

    /**
     * 成功操作 操作码默认为0   无参
     **/
    public static Mono<WebResult> okResultMono() {
        return Mono.just(okResult(getOkCode(), ""));
    }

    /**
     * 成功操作 操作码默认为0   只有提示码
     **/
    public static Mono<WebResult> okResultMono(int code) {
        return Mono.just(okResult(getOkCode(), String.valueOf(code), ""));
    }

    /**
     * 成功操作 操作码默认为0   只有返回数据
     **/
    public static Mono<WebResult> okResultMono(Mono<Object> objectMono) {
        return objectMono.flatMap(o -> Mono.just(okResult(o)));
    }

    /**
     * 成功操作 有提示码 和 数据
     **/
    public static Mono<WebResult> okResultMono(int code, Mono<Object> objectMono) {
        return objectMono.flatMap(o -> Mono.just(okResult(code, String.valueOf(code), o)));
    }

    /**
     * 操作失败 操作码默认为9999   无参
     **/
    public static Mono<WebResult> failResultMono() {
        return Mono.just(failResult(getFailCode(), ""));
    }

    /**
     * 操作失败 操作码默认为9999   只有提示码
     **/
    public static Mono<WebResult> failResultMono(int code) {
        return Mono.just(failResult(getFailCode(), String.valueOf(code), ""));
    }

    /**
     * 成功操作 操作码默认为9999   只有返回数据
     **/
    public static Mono<WebResult> failResultMono(Mono<Object> objectMono) {
        return objectMono.flatMap(o -> Mono.just(failResult(o)));
    }

    /**
     * 成功操作 有提示码 和 数据
     **/
    public static Mono<WebResult> failResultMono(int code, Mono<Object> objectMono) {
        return objectMono.flatMap(o -> Mono.just(failResult(code, String.valueOf(code), o)));
    }

    public static Mono<WebResult> okCustomResultMono(int code, String msg) {
        return Mono.just(customResult(code, msg, ""));
    }

    public static WebResult customResult(int code, String msg, Object obj) {
        WebResult wr = new WebResult();
        wr.setRet(code);
        wr.setMsg(msg);
        wr.setData(obj);
        return wr;
    }

}

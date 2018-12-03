package com.forteach.quiz.web.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/27  9:36
 */
@Data
public class RewriteVo {
    /**
     * 需要撤回的id
     */
    private List<String> sheetId;
}

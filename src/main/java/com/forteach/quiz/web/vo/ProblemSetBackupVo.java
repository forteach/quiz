package com.forteach.quiz.web.vo;

import lombok.Data;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/21  15:47
 */
@Data
public class ProblemSetBackupVo {

    private String id;

    /**
     *
     */
    private String name;

    /**
     * exerciseBook 练习册   /    paper   试卷
     */
    private String type;

    /**
     * 习题册id
     */
    private String exerciseBookId;

}

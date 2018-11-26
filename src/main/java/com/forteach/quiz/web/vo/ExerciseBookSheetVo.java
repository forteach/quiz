package com.forteach.quiz.web.vo;

import com.forteach.quiz.domain.Answ;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/21  10:06
 */
@Data
public class ExerciseBookSheetVo {

    /**
     * 答题卡id
     */
    private String id;

    /**
     * 学生id
     */
    private String examineeId;

    /**
     * 练习册id
     */
    private String backupId;

    /**
     * 参与方式
     */
    private String participate;

    /**
     * 答案
     */
    private List<Answ> answ;

    /**
     * 提交状态  modify   commit   correct
     */
    private String commit;
}

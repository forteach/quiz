package com.forteach.quiz.interaction.execute.dto;

import com.forteach.quiz.interaction.execute.domain.SurveyInteractRecord;

import java.io.Serializable;
import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-2-26 18:18
 * @version: 1.0
 * @description:
 */
public class SurveysDto implements Serializable {
    /**
     * 问卷记录(发布了哪些题目 发布顺序 发布时间 发布次数 选择了哪些人 哪些人进行了回答 回答情况 回答答案)
     */
    private List<SurveyInteractRecord> surveys;

    public List<SurveyInteractRecord> getSurveys() {
        return surveys;
    }
}

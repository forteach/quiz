package com.forteach.quiz.interaction.execute.dto;

import com.forteach.quiz.interaction.execute.domain.record.InteractQuestionsRecord;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-2-26 15:39
 * @version: 1.0
 * @description:
 */
@NoArgsConstructor
@AllArgsConstructor
public class QuestionsDto implements Serializable {
    /**
     * 进行提问过的问题(发布了哪些题目 发布顺序 发布时间 发布次数 选择了哪些人 哪些人进行了回答 回答情况 回答答案)
     */
    private List<InteractQuestionsRecord> questions;

    public List<InteractQuestionsRecord> getQuestions() {
        return questions;
    }
}

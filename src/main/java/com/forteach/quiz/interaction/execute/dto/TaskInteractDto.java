package com.forteach.quiz.interaction.execute.dto;

import com.forteach.quiz.interaction.execute.domain.TaskInteractRecord;

import java.io.Serializable;
import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-2-28 09:18
 * @version: 1.0
 * @description: 任务记录
 */
public class TaskInteractDto implements Serializable {
    private List<TaskInteractRecord> interacts;

    public List<TaskInteractRecord> getInteracts() {
        return interacts;
    }
}

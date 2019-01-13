package com.forteach.quiz.problemsetlibrary.domain;

import com.forteach.quiz.problemsetlibrary.domain.base.ProblemSet;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/13  18:50
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "taskQuestionProblemSet")
@ApiModel(value = "任务题库 编辑题册对象", description = "如果新增 不添加id 如果修改 添加id")
public class TaskQuestionProblemSet extends ProblemSet {
}

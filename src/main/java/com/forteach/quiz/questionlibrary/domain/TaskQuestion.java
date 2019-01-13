package com.forteach.quiz.questionlibrary.domain;

import com.forteach.quiz.questionlibrary.domain.base.QuestionExamEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/10  11:16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "taskQuestion")
@ApiModel(value = "任务题对象", description = "只有主观题 所有的题目类型 全部由大题外部封装   由examChildren展示具体的题目信息")
public class TaskQuestion<Design> extends QuestionExamEntity<Design> {
}

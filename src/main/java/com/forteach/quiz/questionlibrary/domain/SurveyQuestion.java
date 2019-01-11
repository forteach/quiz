package com.forteach.quiz.questionlibrary.domain;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/10  11:27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "surveyQuestion")
@ApiModel(value = "问卷题对象", description = "除了主观题 只有客观题 所有的题目类型 全部由大题外部封装   由examChildren展示具体的题目信息")
public class SurveyQuestion<T> extends QuestionExamEntity<T> {
}

package com.forteach.quiz.interaction.domain;

import com.forteach.quiz.domain.BaseEntity;
import com.forteach.quiz.interaction.web.vo.InteractiveSheetAnsw;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/20  13:29
 */
@Data
@Document(collection = "activityAskAnswer")
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@ApiModel(value = "活动 (任务,风暴,卷子) 回答信息", description = "活动 (任务,风暴,卷子) 学生回答信息")
public class ActivityAskAnswer extends BaseEntity {

    /**
     * 学生id
     */
    @ApiModelProperty(value = "学生id", name = "examineeId")
    private String examineeId;

    /**
     * 问题库类别
     */
    @ApiModelProperty(value = "不需要传值 后台赋值 问题库类别  bigQuestion(考题 练习)/ brainstormQuestion (头脑风暴题库) /" +
            " surveyQuestion(问卷题库) / taskQuestion (任务题库)", name = "questionId")
    private String libraryType;

    /**
     * 答案评价
     */
    @ApiModelProperty(value = "主观题 教师给出的答案评价", name = "evaluate")
    private String evaluate;

    /**
     * 课堂id
     */
    @ApiModelProperty(value = "课堂id", name = "circleId")
    private String circleId;

    /**
     * 答案列表
     */
    @ApiModelProperty(value = "答案列表", name = "answList")
    private List<InteractiveSheetAnsw> answList;


}

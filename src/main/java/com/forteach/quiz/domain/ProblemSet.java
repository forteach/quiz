package com.forteach.quiz.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Description: 题集
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  16:35
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "problemSet")
@ApiModel(value = "编辑题册对象", description = "如果新增 不添加id 如果修改 添加id")
public class ProblemSet extends BaseEntity {

    /**
     * 题集类型：1、预习练习册 2、课堂练习册3、课后作业册
     */
    @ApiModelProperty(value = "题集类型", name = "exeBookType", example = "1、预习练习册 2、课堂练习册3、课后作业册")
    private int exeBookType;

    /**
     * 教师操作人id
     */
    @ApiModelProperty(value = "教师操作人id", name = "teacherId", example = "教师操作人id ")
    private String teacherId;

    /**
     * 题册名
     */
    @ApiModelProperty(value = "题册名", name = "exeBookName", example = "题册名")
    private String exeBookName;

    /**
     * 保存的题目集
     */
    @ApiModelProperty(value = "保存的题目集", name = "questionIds", example = "题目集list")
    private List<QuestionIds> questionIds;

    /**
     * 章节id
     */
    @ApiModelProperty(value = "章节id", name = "sectionId", example = "章节id")
    private String sectionId;

    /**
     * 知识点id
     */
    @ApiModelProperty(value = "知识点id", name = "knowledgeId", example = "0")
    private String knowledgeId;

}
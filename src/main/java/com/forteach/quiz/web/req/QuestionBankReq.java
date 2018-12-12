package com.forteach.quiz.web.req;

import com.forteach.quiz.web.vo.SortVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/12  9:41
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "题库条件查询", description = "题库条件查询")
public class QuestionBankReq extends SortVo {

    /**
     * 章节id
     */
    @ApiModelProperty(value = "章节id", name = "chapter", example = "463bcd8e5fed4a33883850c14f877271")
    protected String chapter;

    /**
     * 难易度id
     */
    @ApiModelProperty(value = "难易度id", name = "levelId", example = "0")
    private String levelId;

    /**
     * 知识点id
     */
    @ApiModelProperty(value = "知识点id", name = "knowledgeId", example = "0")
    private String knowledgeId;


    /**
     * 题目类型
     */
    @ApiModelProperty(value = "题目类型 single  multiple trueOrFalse design bigQuestion", name = "questionType", example = "single")
    private String questionType;

    /**
     * 显示全部详情或者是只返回id
     */
    @ApiModelProperty(value = "显示全部详情或者是只返回id all 全部/part 只显示id", name = "questionType", example = "part")
    private String allOrPart;

}

package com.forteach.quiz.interaction.execute.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/17  14:51
 */
@Data
@NoArgsConstructor
@ApiModel(value = "互动活动答题卡", description = "互动活动答题卡")
public class InteractiveSheetAnsw {

    /**
     * 问题id
     */
    @ApiModelProperty(value = "问题id", name = "questionId")
    private String questionId;

    /**
     * 答案
     */
    @ApiModelProperty(value = "答案", name = "answer")
    private String answer;

    /**
     * 答案附件
     */
    @ApiModelProperty(value = "附件列表", name = "fileList")
    private List<DataDatumVo> fileList;


    /**
     * 题目回答是否正确
     */
    private String answerRight;

    private String date;

    public InteractiveSheetAnsw(String questionId, String answer,List<DataDatumVo> fileList, String answerRight) {
        this.questionId = questionId;
        this.answer = answer;
        this.answerRight = answerRight;
    }

    public InteractiveSheetAnsw(String questionId) {
        this.questionId = questionId;
    }
}

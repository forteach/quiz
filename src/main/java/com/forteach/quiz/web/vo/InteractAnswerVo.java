package com.forteach.quiz.web.vo;

import com.forteach.quiz.interaction.execute.web.vo.DataDatumVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/29  14:48
 */
@Data
@ApiModel(value = "学生提交答案", description = "学生提交答案 需要传入接收的题目的cut值")
public class InteractAnswerVo {

    /**
     * 学生id
     */
//    @ApiModelProperty(value = "学生id", name = "examineeId")
    private String examineeId;
    /**
     * 课堂圈子id
     */
    @ApiModelProperty(value = "课堂圈子id", name = "circleId")
    private String circleId;
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
     * 题目交互活动类型  TIWEN LIANXI RENWU
     */
    @ApiModelProperty(value = "切换提问类型过期标识  接收的该题cut", name = "cut")
    private String questionType;
    /**
     * 回答参与方式
     */
    @ApiModelProperty(value = "参与方式 race   : 抢答/raise  : 举手/select : 选择/vote  没有参与方式：no : 投票", name = "questionType")
    private String interactive = "no";

    public InteractAnswerVo() {
    }

    public InteractAnswerVo(String examineeId, String circleId, String questionId, String answer, String questionType, List<DataDatumVo> fileList) {
        this.examineeId = examineeId;
        this.circleId = circleId;
        this.questionId = questionId;
        this.answer = answer;
        this.questionType = questionType;
        this.fileList = fileList;
    }

    public InteractAnswerVo(String examineeId, String circleId, String questionId, String answer, String questionType) {
        this.examineeId = examineeId;
        this.circleId = circleId;
        this.questionId = questionId;
        this.answer = answer;
        this.questionType = questionType;
    }

}

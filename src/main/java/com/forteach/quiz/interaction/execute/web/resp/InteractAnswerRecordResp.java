package com.forteach.quiz.interaction.execute.web.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-3-20 15:41
 * @version: 1.0
 * @description:
 */
@Data
@Builder
@ApiModel(value = "学生回答结果对象")
public class InteractAnswerRecordResp implements Serializable {

    @ApiModelProperty(name = "studentId", value = "学生id", dataType = "string")
    private String studentId;

    @ApiModelProperty(name = "name", value = "学生姓名", dataType = "string")
    private String name;

    @ApiModelProperty(name = "portrait", value = "学生头像url", dataType = "string")
    private String portrait;

    /**
     * 回答的答案
     */
    @ApiModelProperty(name = "answer", value = "回答内容", dataType = "string")
    private String answer;

    @ApiModelProperty(name = "piGaiResult", value = "批改的答题结果", dataType = "boolean", notes = "true / false")
    private boolean piGaiResult;

    public InteractAnswerRecordResp() {
    }

    public InteractAnswerRecordResp(String studentId, String name, String portrait, String answer, boolean piGaiResult) {
        this.studentId = studentId;
        this.name = name;
        this.portrait = portrait;
        this.answer = answer;
        this.piGaiResult = piGaiResult;
    }

}

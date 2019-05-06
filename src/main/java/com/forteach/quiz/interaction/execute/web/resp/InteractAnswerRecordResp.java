package com.forteach.quiz.interaction.execute.web.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-3-20 15:41
 * @version: 1.0
 * @description:
 */
@Data
@ApiModel(value = "学生回答结果详细对象记录")
public class InteractAnswerRecordResp {

//    @ApiModelProperty(name = "category", value = "发布类别")
//    private String category;

    @ApiModelProperty(name = "interactive", value = "提问方式")
    private String interactive;

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
    private Boolean piGaiResult;

    @ApiModelProperty(name = "updateTime", value = "回答时间", dataType = "string")
    private String updateTime;

    public InteractAnswerRecordResp() {
    }

    public InteractAnswerRecordResp(
//            String category,
            String interactive, String studentId, String name, String portrait,
                                    String answer, String piGaiResult, String updateTime) {
//        this.category = category;
        this.interactive = interactive;
        this.studentId = studentId;
        this.name = name;
        this.portrait = portrait;
        this.answer = answer;
        if ("true".equals(piGaiResult)){
            this.piGaiResult = true;
        }else {
            this.piGaiResult = false;
        }
        this.updateTime = updateTime;
    }

    public InteractAnswerRecordResp(String studentId, String name, String portrait) {
        this.studentId = studentId;
        this.name = name;
        this.portrait = portrait;
    }
}

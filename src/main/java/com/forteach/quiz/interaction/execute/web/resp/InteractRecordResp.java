package com.forteach.quiz.interaction.execute.web.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-3-27 16:06
 * @version: 1.0
 * @description:
 */
@Data
@Builder
@ApiModel(value = "查询问题记录数据对象")
public class InteractRecordResp implements Serializable {

//    @ApiModelProperty(name = "category", value = "发布类别")
//    private String category;

//    @ApiModelProperty(name = "interactive", value = "提问方式")
//    private String interactive;

    private String circleId;

    private String questionId;

    private List<InteractAnswerRecordResp> answerRecordList;

//    public InteractRecordResp(String category, String interactive, String circleId,
//                              String questionId, List<InteractAnswerRecordResp> answerRecordList) {
//        this.category = category;
//        this.interactive = interactive;
//        this.circleId = circleId;
//        this.questionId = questionId;
//        this.answerRecordList = answerRecordList;
//    }

    public InteractRecordResp() {
    }

    public InteractRecordResp(String circleId, String questionId, List<InteractAnswerRecordResp> answerRecordList) {
        this.answerRecordList = answerRecordList;
        this.circleId = circleId;
        this.questionId = questionId;
    }
}

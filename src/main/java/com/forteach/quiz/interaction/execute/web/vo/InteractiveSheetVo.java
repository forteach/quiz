package com.forteach.quiz.interaction.execute.web.vo;

import com.forteach.quiz.questionlibrary.domain.QuestionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import static com.forteach.quiz.common.KeyStorage.CLASSROOM_ASK_QUESTIONS_ID;
import static com.forteach.quiz.common.KeyStorage.EXAMINEE_IS_REPLY_KEY;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/17  14:37
 */
@Data
@ApiModel(value = "互动活动答题卡", description = "互动活动答题卡")
public class InteractiveSheetVo {

    /**
     * 学生id
     */
    @ApiModelProperty(value = "学生id", name = "examineeId")
    private String examineeId;

    /**
     * 课堂圈子id
     */
    @ApiModelProperty(value = "课堂圈子id", name = "circleId")
    private String circleId;

    /**
     * 切换提问类型过期标识
     */
    @ApiModelProperty(value = "切换提问类型过期标识  接收的该题cut", name = "cut")
    private String cut;

    /**
     * 答案列表
     */
    @ApiModelProperty(value = "答案列表", name = "answ")
    private InteractiveSheetAnsw answ;

    /**
     * 获取redis 提问 key
     *
     * @return
     */
    public String getAskKey(QuestionType type) {
        return CLASSROOM_ASK_QUESTIONS_ID.concat(type.name()).concat(circleId);
    }

    public String getExamineeIsReplyKey(QuestionType type) {
        return EXAMINEE_IS_REPLY_KEY.concat(type.name()).concat(circleId);
    }

}

package com.forteach.quiz.interaction.execute.web.vo;

import com.forteach.quiz.interaction.execute.config.BigQueKey;
import com.forteach.quiz.questionlibrary.domain.QuestionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;
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
    @ApiModelProperty(hidden = true)
    private String examineeId;

    /**
     * 课堂圈子id
     */
    @NotBlank(message = "课堂id不为空")
    @ApiModelProperty(value = "课堂圈子id", name = "circleId")
    private String circleId;

    /**
     * 切换提问类型过期标识
     */
    @NotBlank(message = "接收的该题cut不为空")
    @ApiModelProperty(value = "切换提问类型过期标识  接收的该题cut", name = "cut")
    private String cut;

    /**
     * 答案列表
     */
    @NotBlank(message = "答案列表不为空")
    @ApiModelProperty(value = "答案列表", name = "answ")
    private InteractiveSheetAnsw answ;

    /**
     * 获取redis 提问 key
     *
     * @return
     */
    public String getAskKey(QuestionType type) {
        return BigQueKey.CLASSROOM_ASK_QUESTIONS_ID.concat(circleId).concat(type.name());
    }

    /**
     * 问卷提问 问卷题库
     * @param type
     * @return
     */
    public String getExamineeIsReplyKey(QuestionType type) {
        return BigQueKey.EXAMINEE_IS_REPLY_KEY.concat(circleId).concat(type.name());
    }

}

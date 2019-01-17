package com.forteach.quiz.interaction.domain.base;

import com.forteach.quiz.questionlibrary.domain.QuestionType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import static com.forteach.quiz.common.KeyStorage.EXAMINEE_IS_REPLY_KEY;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/15  9:49
 */
@Data
public class GiveVo {

    /**
     * 课堂圈子id
     */
    @ApiModelProperty(value = "课堂圈子id", name = "circleId")
    protected String circleId;

    /**
     * 选取类别
     * <p>
     * people  :  个人
     * team    :  小组
     */
    @ApiModelProperty(value = "选取类别 people  :  个人 / team    :  小组", name = "category", notes = "people  :  个人 / team    :  小组")
    protected String category;

    /**
     * 选中人员  [逗号 分割]
     */
    @ApiModelProperty(value = "选中人员 [逗号 分割]", name = "selected")
    protected String selected;

    /**
     * 切换
     * 当老师重复提问问题时  但是两次的提问类型不一样  如第一次抢答 第二次提问
     * 需要传送切换标识
     * 0 : 原题目   非0 : 切题
     */
    @ApiModelProperty(value = "切换提问方式", name = "cut", notes = "切换\n" +
            "     * 当老师重复提问问题时  但是两次的提问类型不一样  如第一次抢答 第二次提问\n" +
            "     * 需要传送切换标识\n" +
            "     * 0 : 原题目   非0 : 切题")
    private String cut;

    public String getExamineeIsReplyKey(QuestionType type) {
        return EXAMINEE_IS_REPLY_KEY.concat(type.name()).concat(circleId);
    }
}

package com.forteach.quiz.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import static com.forteach.quiz.common.KeyStorage.CLASSROOM_ASK_QUESTIONS_RACE;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/27  15:21
 */
@Data
@ApiModel(value = "发布课堂提问", description = "除了提问题,所有问题只有选中的学生才会收到")
public class GiveVo {

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
     * 互动方式
     * <p>
     * race   : 抢答
     * raise  : 举手
     * select : 选则
     * vote   : 投票
     */
    @ApiModelProperty(value = "互动方式 race   : 抢答/raise  : 举手/select : 选择/vote   : 投票", name = "interactive", notes = "race   : 抢答/raise  : 举手/select : 选择/vote   : 投票")
    private String interactive;

    /**
     * 选取类别
     * <p>
     * people  :  个人
     * team    :  小组
     */
    @ApiModelProperty(value = "选取类别", name = "category", notes = "people  :  个人 / team    :  小组")
    private String category;

    /**
     * 选中人员  [逗号 分割]
     */
    @ApiModelProperty(value = "选中人员 [逗号 分割]", name = "selected")
    private String selected;

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
    private int cut;

    public String getRaceAnswerFlag() {
        return CLASSROOM_ASK_QUESTIONS_RACE.concat(circleId).concat(questionId);
    }
}

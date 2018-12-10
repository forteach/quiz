package com.forteach.quiz.web.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.forteach.quiz.common.KeyStorage.CLASSROOM_ASK_QUESTIONS_ID;
import static com.forteach.quiz.common.KeyStorage.EXAMINEE_IS_REPLY_KEY;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/7  15:13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchieveAnswerVo {

    /**
     * 课堂圈子id
     */
    private String circleId;

    /**
     * 教师id
     */
    private String teacher;

    /**
     * 随机数
     */
    private String random;

    public String getExamineeIsReplyKey(final String examineeId) {
        return EXAMINEE_IS_REPLY_KEY.concat(examineeId);
    }

    /**
     * 获取redis 提问 key
     *
     * @return
     */
    public String getAskKey() {
        return CLASSROOM_ASK_QUESTIONS_ID.concat(circleId);
    }


}

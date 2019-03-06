package com.forteach.quiz.web.vo;

import com.forteach.quiz.interaction.execute.config.BigQueKey;
import com.forteach.quiz.questionlibrary.domain.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import static com.forteach.quiz.common.KeyStorage.*;

/**
 * @Description: 实时获取学生回答情况
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
        return BigQueKey.EXAMINEE_IS_REPLY_KEY.concat(examineeId);
    }

    /**
     * 获取redis 提问 key
     *
     * @return
     */
    public String getAskKey(QuestionType type) {
        return BigQueKey.CLASSROOM_ASK_QUESTIONS_ID.concat(circleId).concat(type.name());
    }

    /**
     * 考生是否答复
     * @param type 考试习题头脑风暴的类型
     * @return
     */
    public String getExamineeIsReplyKey(QuestionType type) {
        return BigQueKey.EXAMINEE_IS_REPLY_KEY.concat(circleId).concat(type.name());
    }

    /**
     * 获取课堂随机字符串拼接结果推送给教师
     * @return
     */
    public String getAnswDistinctKey() {
        return ANSW_HAND_STUDENT_DISTINCT.concat(circleId).concat(random);
    }


}

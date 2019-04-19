package com.forteach.quiz.web.vo;

import com.forteach.quiz.interaction.execute.config.BigQueKey;
import com.forteach.quiz.questionlibrary.domain.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.forteach.quiz.common.Dic.ASK_RAISE_HAND;
import static com.forteach.quiz.common.KeyStorage.RAISE_HAND_STUDENT_DISTINCT;

/**
 * @Description: 加入课堂的学生信息vo
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/4  15:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchieveRaiseVo {

    /**
     * 学生id
     */
    private String studentId;

    /**
     * 课堂圈子id
     */
    private String circleId;

    private String teacher;

    /**
     * 随机数
     */
    private String random;

}

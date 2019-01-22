package com.forteach.quiz.domain;

import com.forteach.quiz.interaction.execute.domain.Answ;
import com.forteach.quiz.web.vo.ExerciseBookSheetVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Description: 练习册 答题卡
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/19  9:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "exerciseBookSheet")
public class ExerciseBookSheet extends BaseEntity {

    /**
     * 学生id
     */
    private String examineeId;

    /**
     * 练习册id
     */
    private String backupId;

    /**
     * 参与方式
     */
    private String participate;

    /**
     * 答案
     */
    private List<Answ> answ;

    /**
     * 提交状态  modify   commit   correct
     */
    private String commit;

    /**
     * 评价
     */
    private String evaluation;

    /**
     * 批改教师
     */
    private String correctTeacher;

    /**
     * 练习册分数
     */

    public ExerciseBookSheet() {
    }

    public ExerciseBookSheet(ExerciseBookSheetVo exerciseBookSheetVo) {
        this.examineeId = exerciseBookSheetVo.getExamineeId();
        this.backupId = exerciseBookSheetVo.getBackupId();
        this.participate = exerciseBookSheetVo.getParticipate();
        this.answ = exerciseBookSheetVo.getAnsw();
        this.commit = exerciseBookSheetVo.getCommit();
    }

    public ExerciseBookSheet(String examineeId, String backupId, String participate, List<Answ> answ, String commit, String evaluation, String correctTeacher) {
        this.examineeId = examineeId;
        this.backupId = backupId;
        this.participate = participate;
        this.answ = answ;
        this.commit = commit;
        this.evaluation = evaluation;
        this.correctTeacher = correctTeacher;
    }


}

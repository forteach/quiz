package com.forteach.quiz.practiser.web.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-12 11:23
 * @version: 1.0
 * @description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerVo {

    private String exeBookType, chapterId, chapterName, courseId, preview, classId, studentId;

    public AnswerVo(String exeBookType, String chapterId, String courseId, String preview, String studentId) {
        this.exeBookType = exeBookType;
        this.chapterId = chapterId;
        this.courseId = courseId;
        this.preview = preview;
        this.studentId = studentId;
    }
}

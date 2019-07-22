package com.forteach.quiz.practiser.web.resp;

import com.forteach.quiz.problemsetlibrary.domain.BigQuestionExerciseBook;
import com.forteach.quiz.problemsetlibrary.web.vo.ProblemSetVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-7-22 19:50
 * @version: 1.0
 * @description:
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BigQuestionExerciseBookResp extends BigQuestionExerciseBook {

    private String preview;

    private String chapterName;

    public BigQuestionExerciseBookResp() {
    }

    public BigQuestionExerciseBookResp(String preview, String chapterName) {
        this.preview = preview;
        this.chapterName = chapterName;
    }

    public BigQuestionExerciseBookResp(ProblemSetVo problemSetVo, List<?> list, String preview, String chapterName) {
        super(problemSetVo, list);
        this.preview = preview;
        this.chapterName = chapterName;
    }
}

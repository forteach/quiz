package com.forteach.quiz.practiser.web.resp;

import com.forteach.quiz.interaction.execute.web.vo.DataDatumVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.Serializable;
import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-10 13:45
 * @version: 1.0
 * @description:
 */
@Data
public class AskAnswerExerciseResp implements Serializable {
    /**
     * 习题id
     */
    @ApiModelProperty(value = "习题id", name = "questionId")
    @Indexed
    private String questionId;



    /**
     * 学生答案
     */
    @ApiModelProperty(value = "回答内容", name = "answer")
    private String answer;

    /**
     * 答案附件
     */
    @ApiModelProperty(value = "附件列表", name = "fileList")
    private List<DataDatumVo> fileList;

    /**
     * 答案图片列表
     */
    private List<String> answerImageList;

    public AskAnswerExerciseResp() {
    }

    public AskAnswerExerciseResp(String questionId, String answer, List<DataDatumVo> fileList, List<String> answerImageList) {
        this.questionId = questionId;
        this.answer = answer;
        this.fileList = fileList;
        this.answerImageList = answerImageList;
    }
}

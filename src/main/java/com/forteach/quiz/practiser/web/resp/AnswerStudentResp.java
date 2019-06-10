package com.forteach.quiz.practiser.web.resp;

import com.forteach.quiz.practiser.domain.AskAnswerExercise;
import com.forteach.quiz.web.pojo.Students;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-4 14:05
 * @version: 1.0
 * @description:
 */
@Data
@ApiModel(value = "学生回答的情况")
@EqualsAndHashCode(callSuper = true)
public class AnswerStudentResp extends Students implements Serializable {

    @ApiModelProperty(name = "questionIds", value = "回答/没有回答 的问题id")
    private List<String> questionIds;

    @ApiModelProperty(name = "askAnswerExercises", value = "回答结果", dataType = "list")
    private List<AskAnswerExercise> askAnswerExercises;

    @ApiModelProperty(name = "isAnswerCompleted", value = "是否答完题 Y/N")
    private String isAnswerCompleted;

    @ApiModelProperty(name = "isCorrectCompleted", value = "是否批改完作业　Y/N", dataType = "string", required = true)
    private String isCorrectCompleted;

    @ApiModelProperty(name = "isReward", value = "是否奖励", dataType = "string")
    private String isReward;

    public AnswerStudentResp(List<AskAnswerExercise> askAnswerExercises, String isAnswerCompleted, String isCorrectCompleted, String isReward, String id, String name, String portrait) {
        super(id, name, portrait);
        this.askAnswerExercises = askAnswerExercises;
        this.isAnswerCompleted = isAnswerCompleted;
        this.isCorrectCompleted = isCorrectCompleted;
        this.isReward = isReward;
    }

    public AnswerStudentResp(List<AskAnswerExercise> askAnswerExercises, String isAnswerCompleted, String isCorrectCompleted, String isReward) {
        this.askAnswerExercises = askAnswerExercises;
        this.isAnswerCompleted = isAnswerCompleted;
        this.isCorrectCompleted = isCorrectCompleted;
        this.isReward = isReward;
    }

    public AnswerStudentResp(String id, String name, String portrait, List<String> questionIds, String isAnswerCompleted, String isCorrectCompleted, String isReward) {
        super(id, name, portrait);
        this.questionIds = questionIds;
        this.isAnswerCompleted = isAnswerCompleted;
        this.isCorrectCompleted = isCorrectCompleted;
        this.isReward = isReward;
    }
}

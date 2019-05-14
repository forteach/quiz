package com.forteach.quiz.interaction.execute.web.resp;

import com.forteach.quiz.interaction.execute.domain.ActivityAskAnswer;
import com.forteach.quiz.interaction.execute.web.vo.InteractiveSheetAnsw;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-5-6 10:06
 * @version: 1.0
 * @description:
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ActivityAskAnswerResp extends ActivityAskAnswer {

    @ApiModelProperty(name = "name", value = "学生姓名", dataType = "string")
    private String name;

    @ApiModelProperty(name = "portrait", value = "学生头像", dataType = "string")
    private String portrait;


    public ActivityAskAnswerResp() {
    }

    public ActivityAskAnswerResp(String name,
                                 String portrait,
                                 String examineeId,
                                 String libraryType,
                                 String evaluate,
                                 String circleId,
                                 List<InteractiveSheetAnsw> answList) {
        super(examineeId, libraryType, evaluate, circleId, answList);
        this.name = name;
        this.portrait = portrait;
    }

    public ActivityAskAnswerResp(String name, String portrait) {
        this.name = name;
        this.portrait = portrait;
    }
}

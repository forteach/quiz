package com.forteach.quiz.interaction.execute.web.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Auther: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2019/1/27 17:22
 * @Version: 1.0
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordReq implements Serializable {

    @ApiModelProperty(value = "课堂id", name = "circleId", dataType = "string", required = true)
    private String circleId;

    @ApiModelProperty(value = "问题id", name = "questionId", dataType = "string")
    private String questionId;

    @ApiModelProperty(value = "学生id", name = "examineeId", dataType = "string")
    private String examineeId;

    @ApiModelProperty(name = "libraryType", value = "问题库类别  bigQuestion(考题 练习)/ brainstormQuestion (头脑风暴题库) /" +
            " surveyQuestion(问卷题库) / taskQuestion (任务题库)", dataType = "string")
    private String libraryType;

}

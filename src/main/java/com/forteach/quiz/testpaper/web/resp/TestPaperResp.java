package com.forteach.quiz.testpaper.web.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/9/28 11:18
 * @Version: v1.0
 * @Modified：学生成绩
 * @Description:
 */
@Data
@ApiModel(value = "查询学生成绩信息")
public class TestPaperResp implements Serializable {
    /**
     * 试卷id
     *
     * @return
     */
    @ApiModelProperty(name = "testPaperId", value = "试卷id", dataType = "string")
    private String testPaperId;

    /**
     * 试卷名称
     */
    @ApiModelProperty(name = "testPaperName", value = "试卷名称", dataType = "string")
    private String testPaperName;
    /**
     * 考试分数
     */
    @ApiModelProperty(name = "testScore", value = "考试试卷分数", dataType = "int")
    private int testScore;
    /**
     * 课程id
     */
    @ApiModelProperty(name = "courseId", value = "课程id", dataType = "string")
    private String courseId;
    /**
     * 课程名称
     */
    @ApiModelProperty(name = "courseName", value = "课程名称", dataType = "string")
    private String courseName;
}

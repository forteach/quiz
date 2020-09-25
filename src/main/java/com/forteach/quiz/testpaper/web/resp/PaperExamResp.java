package com.forteach.quiz.testpaper.web.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/9/9 14:03
 * @Version: v1.0
 * @Modified：返回查询到的考试信息
 * @Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "返回考试安排信息")
public class PaperExamResp implements Serializable {
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
     * 需要考试的学年 默认是当前年份
     */
    @ApiModelProperty(name = "year", value = "考试年份默认是当前年份", dataType = "int")
    private Integer year;
    /**
     * 需要考试的学期
     */
    @ApiModelProperty(name = "semester", value = "需要考试的学期默认是 当前学期", dataType = "int")
    private Integer semester;
    /**
     * 监考教师id
     */
    @ApiModelProperty(name = "teacherId", value = "教师id", dataType = "string")
    private String teacherId;
    /**
     * 监考的教师名称
     */
    @ApiModelProperty(name = "teacherName", value = "监考教师名称", dataType = "string")
    private String teacherName;
    /**
     * 开始日期
     */
    @ApiModelProperty(name = "startTimeTime", value = "开始时间(yyyy-MM-dd HH:mm:ss)", dataType = "string", example = "2020-09-11 09:00:00")
    private String startDateTime;
    /**
     * 结束日期
     */
    @ApiModelProperty(name = "endDateTime", value = "结束时间(yyyy-MM-dd HH:mm:ss)", dataType = "string", example = "2020-09-11 11:00:00")
    private String endDateTime;
}

package com.forteach.quiz.testpaper.web.req;

import cn.hutool.core.date.DateUtil;
import com.forteach.quiz.web.vo.SortVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/9/15 10:10
 * @Version: v1.0
 * @Modified：分页查询考试信息
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "查询我的课程信息")
public class TestPaperPageReq extends SortVo implements Serializable {

    @ApiModelProperty(name = "testPaperName", value = "试卷名称", dataType = "string")
    private String testPaperName;

    @ApiModelProperty(name = "courseName", value = "课程名称", dataType = "string")
    private String courseName;

    @ApiModelProperty(name = "studentName", value = "学生名称", dataType = "string")
    private String studentName;

    @ApiModelProperty(name = "className", value = "班级名称", dataType = "string")
    private String className;

    @ApiModelProperty(name = "year", value = "考试年份(默认当前年份)", dataType = "int")
    private Integer year = this.getYear() == null ? DateUtil.thisYear() : this.getYear();

    @ApiModelProperty(name = "semester", value = "学期(默认当前学期)", dataType = "int")
    private Integer semester = this.getSemester() == null ? com.forteach.quiz.util.DateUtil.getSemesterByNow() : this.getSemester();
}

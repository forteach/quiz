package com.forteach.quiz.testpaper.web.req;

import cn.hutool.core.util.StrUtil;
import com.forteach.quiz.util.DateUtil;
import com.forteach.quiz.web.vo.SortVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/8/28 18:10
 * @Version: v1.0
 * @Modified：查询考试记录信息
 * @Description:
 */
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "查询考试信息")
public class FindExamInfoReq extends SortVo implements Serializable {

    @ApiModelProperty(name = "classId", value = "班级id", dataType = "string")
    private String classId;

    @ApiModelProperty(name = "teacherId", value = "教师id", dataType = "string")
    private String teacherId;

    @ApiModelProperty(name = "year", value = "年", dataType = "string")
    private String year;

    @ApiModelProperty(name = "semester", value = "学期", dataType = "string")
    private String semester;
    /**
     * 开始日期时间
     */
    @ApiModelProperty(name = "startDateTime", value = "开始时间日期(yyyy-MM-dd HH:mm:ss)", dataType = "string", example = "2020-08-31 09:00:00")
    private String startDateTime;
    /**
     * 结束日期时间
     */
    @ApiModelProperty(name = "endDateTime", value = "结束日期(yyyy-MM-dd HH:mm:ss)", dataType = "string", example = "2020-08-31 11:00:00")
    private String endDateTime;

    @ApiModelProperty(name = "testPaperId", value = "试卷id", dataType = "string")
    private String testPaperId;

    @ApiModelProperty(name = "courseId", value = "课程id", dataType = "string")
    private String courseId;

    @ApiModelProperty(name = "courseName", value = "课程名称", dataType = "string")
    private String courseName;

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        if (StrUtil.isBlank(year)){
            year = String.valueOf(cn.hutool.core.date.DateUtil.thisYear());
        }
        this.year = year;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        if (StrUtil.isBlank(semester)){
            semester = String.valueOf(DateUtil.getSemesterByNow());
        }
        this.semester = semester;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getTestPaperId() {
        return testPaperId;
    }

    public void setTestPaperId(String testPaperId) {
        this.testPaperId = testPaperId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
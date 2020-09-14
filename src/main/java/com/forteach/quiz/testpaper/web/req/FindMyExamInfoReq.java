package com.forteach.quiz.testpaper.web.req;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/9/9 14:26
 * @Version: v1.0
 * @Modified：查询我的考试信息
 * @Description:
 */
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "查询我的课程信息")
public class FindMyExamInfoReq implements Serializable {

    @ApiModelProperty(name = "classId", value = "班级id", dataType = "string", hidden = true)
    private String classId;

    @ApiModelProperty(name = "semester", value = "学期", dataType = "string")
    private String semester;

    @ApiModelProperty(name = "year", value = "年", dataType = "string")
    private String year;

    @ApiModelProperty(name = "startDateTime", value = "开始时间日期(yyyy-MM-dd HH:mm:ss)", dataType = "string", example = "2020-08-31 09:00:00")
    private String startDateTime;

    @ApiModelProperty(name = "endDateTime", value = "结束日期(yyyy-MM-dd HH:mm:ss)", dataType = "string", example = "2020-08-31 11:00:00")
    private String endDateTime;

    @ApiModelProperty(name = "teacherId", value = "教师id", dataType = "string")
    private String teacherId;

    @ApiModelProperty(name = "courseId", value = "课程id", dataType = "string")
    private String courseId;

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        if (StrUtil.isBlank(semester)){
            semester = String.valueOf(com.forteach.quiz.util.DateUtil.getSemesterByNow());
        }
        this.semester = semester;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        if (StrUtil.isBlank(year)){
            year = String.valueOf(DateUtil.thisYear());
        }
        this.year = year;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        if (StrUtil.isBlank(startDateTime)){
            startDateTime = DateUtil.offsetDay(new Date(), -90).toString();
        }
        this.startDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        if (StrUtil.isBlank(endDateTime)){
            endDateTime = DateUtil.offsetDay(new Date(), 30).toString();
        }
        this.endDateTime = endDateTime;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}

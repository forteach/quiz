package com.forteach.quiz.testpaper.domain;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.forteach.quiz.domain.BaseEntity;
import com.forteach.quiz.testpaper.web.vo.ClassVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/8/28 11:09
 * @Version: v1.0
 * @Modified：考试信息
 * @Description:
 */
@EqualsAndHashCode(callSuper = true)
@Document(collection = "examInfo")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "考试安排信息", description = "考试安排信息")
public class ExamInfo extends BaseEntity {
    /**
     * 需要考试的学年 默认是当前年份
     */
    @ApiModelProperty(name = "year", value = "考试年份", dataType = "int", required = true)
    private Integer year = this.getYear() == null ? DateUtil.thisYear() : this.getYear();
    /**
     * 需要考试的学期
     */
    @ApiModelProperty(name = "semester", value = "学期", dataType = "int", required = true)
    private Integer semester = this.getSemester() == null ? com.forteach.quiz.util.DateUtil.getSemesterByNow() : this.getSemester();
    /**
     * 监考教师id
     */
    @ApiModelProperty(name = "teacherId", value = "批改教师id", dataType = "string")
    private String teacherId;
    /**
     * 监考的教师名称
     */
    @ApiModelProperty(name = "teacherName", value = "批改教师名称", dataType = "string")
    private String teacherName;
    /**
     * 开始日期
     */
    @ApiModelProperty(name = "startDateTime", value = "开始时间(yyyy-MM-dd HH:mm:ss)", dataType = "string")
    private LocalDateTime startDateTime;
    /**
     * 结束日期
     */
    @ApiModelProperty(name = "endDateTime", value = "结束时间(yyyy-MM-dd HH:mm:ss)", dataType = "string")
    private LocalDateTime endDateTime;
    /**
     * 需要考试的班级集合
     */
    @ApiModelProperty(name = "classList", value = "班级集合信息", dataType = "list", required = true)
    private List<ClassVo> classList;
    /**
     * 课程id
     */
    @ApiModelProperty(name = "courseId", value = "课程名称", dataType = "string", required = true)
    private String courseId;
    /**
     * 课程名称
     */
    @ApiModelProperty(name = "courseName", value = "课程名称", dataType = "string", required = true)
    private String courseName;
    /**
     * 试卷id
     *
     * @return
     */
    @ApiModelProperty(name = "testPaperId", value = "试卷id", dataType = "string", required = true)
    private String testPaperId;

    /**
     * 试卷名称
     */
    @ApiModelProperty(name = "teacherName", value = "试卷名称", dataType = "string", required = true)
    private String testPaperName;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getStartDateTime() {
        if (null != startDateTime) {
            return startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
        return "";
    }

    public void setStartDateTime(String startDateTime) {
        if (StrUtil.isNotBlank(startDateTime)) {
            this.startDateTime = LocalDateTime.parse(startDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
    }

    public String getEndDateTime() {
        if (null != endDateTime) {
            return endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
        return "";
    }

    public void setEndDateTime(String endDateTime) {
        if (StrUtil.isNotBlank(endDateTime)) {
            this.endDateTime = LocalDateTime.parse(endDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
    }

    public List<ClassVo> getClassList() {
        return classList;
    }

    public void setClassList(List<ClassVo> classList) {
        this.classList = classList;
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

    public String getTestPaperName() {
        return testPaperName;
    }

    public void setTestPaperName(String testPaperName) {
        this.testPaperName = testPaperName;
    }
}
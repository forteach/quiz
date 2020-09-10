package com.forteach.quiz.testpaper.web.req;

import cn.hutool.core.date.DateUtil;
import com.forteach.quiz.testpaper.web.vo.ClassVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/8/28 18:09
 * @Version: v1.0
 * @Modified：保存考试记录信息
 * @Description:
 */
@ApiModel(value = "保存试卷信息")
public class UpdateExamInfoReq implements Serializable {

    @ApiModelProperty(value = "id", name = "id", example = "5c06d23sz8737b1dc8068da8", notes = "传入id为修改  不传id为新增")
    protected String id;

    @ApiModelProperty(name = "year", value = "所属年份(没有值得话是当前年份)", dataType = "int")
    private Integer year;
    /**
     * 需要考试的学期
     */
    @ApiModelProperty(name = "semester", value = "所属学期", required = true, dataType = "int")
    private Integer semester;
    /**
     * 监考教师id
     */
    @ApiModelProperty(name = "teacherId", value = "监考教师id", dataType = "string", required = true)
    private String teacherId;
    /**
     * 监考的教师名称
     */
    @ApiModelProperty(name = "teacherName", value = "监考的教师名称", dataType = "string", required = true)
    private String teacherName;
    /**
     * 开始日期时间
     */
    @ApiModelProperty(name = "startDateTime", value = "开始时间日期(yyyy-MM-dd HH:mm:ss)", required = true, dataType = "string", example = "2020-08-31 09:00:00")
    private String startDateTime;
    /**
     * 结束日期时间
     */
    @ApiModelProperty(name = "endDateTime", value = "结束日期(yyyy-MM-dd HH:mm:ss)", dataType = "string", required = true, example = "2020-08-31 11:00:00")
    private String endDateTime;
    /**
     * 需要考试的班级集合
     */
    @ApiModelProperty(name = "classList", value = "需要考试的班级集合包含班级Id和名称", dataType = "list", required = true)
    private List<ClassVo> classList;

    @ApiModelProperty(name = "testPaperId", value = "试卷id", dataType = "string", required = true)
    private String testPaperId;

    @ApiModelProperty(name = "testPaperName", value = "试卷名称", dataType = "string", required = true)
    private String testPaperName;

    /**
     * 课程id
     */
    @NotBlank(message = "课程id 不为空")
    @ApiModelProperty(name = "courseId", value = "课程id", dataType = "string", required = true)
    private String courseId;
    /**
     * 课程名称
     */
    @NotBlank(message = "课程名不为空")
    @ApiModelProperty(name = "courseName", value = "课程名称", dataType = "string", required = true)
    private String courseName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        if (null == year){
            year = DateUtil.thisYear();
        }
        this.year = year;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        if (null == semester){
            semester = com.forteach.quiz.util.DateUtil.getSemesterByNow();
        }
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

    public String getTestPaperName() {
        return testPaperName;
    }

    public void setTestPaperName(String testPaperName) {
        this.testPaperName = testPaperName;
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

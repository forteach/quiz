package com.forteach.quiz.testpaper.domain;

import cn.hutool.core.date.DateUtil;
import com.forteach.quiz.domain.BaseEntity;
import com.forteach.quiz.testpaper.web.vo.ResultVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/9/10 10:45
 * @Version: v1.0
 * @Modified：学生考试答题结果
 * @Description:
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "testPaperResult")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "学生答题信息", description = "学生回答答题信息表")
public class TestPaperResult extends BaseEntity {
    /**
     * 试卷id
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

    /**
     * 需要考试的学年 默认是当前年份
     */
    @ApiModelProperty(name = "year", value = "考试年份", dataType = "int")
    private Integer year = this.getYear() == null ? DateUtil.thisYear() : this.getYear();
    /**
     * 需要考试的学期
     */
    @ApiModelProperty(name = "semester", value = "学期", dataType = "int")
    private Integer semester = this.getSemester() == null ? com.forteach.quiz.util.DateUtil.getSemesterByNow() : this.getSemester();

    /**
     * 批改教师id
     */
    @ApiModelProperty(name = "teacherId", value = "批改教师id", dataType = "string")
    private String teacherId;
    /**
     * 批改的教师名称
     */
    @ApiModelProperty(name = "teacherName", value = "批改教师名称", dataType = "string")
    private String teacherName;
    /**
     * 完成
     */
    @ApiModelProperty(name = "complete", value = "是否考试完成", dataType = "string")
    private Boolean complete;

    @ApiModelProperty(name = "studentId", value = "学生id", dataType = "string")
    private String studentId;

    @ApiModelProperty(name = "studentName", value = "学生名称", dataType = "string")
    private String studentName;
    /**
     * 班级id
     */
    @ApiModelProperty(name = "classId", value = "班级id", dataType = "string")
    private String classId;
    /**
     * 班级名称
     */
    @ApiModelProperty(name = "className", value = "班级名称", dataType = "string")
    private String className;

    /**
     * 答题结果
     */
    @ApiModelProperty(name = "resultList", value = "回答结果", dataType = "list")
    private List<ResultVo> resultList;
}
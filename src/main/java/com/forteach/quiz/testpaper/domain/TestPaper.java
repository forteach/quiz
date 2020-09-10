package com.forteach.quiz.testpaper.domain;

import cn.hutool.core.date.DateUtil;
import com.forteach.quiz.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/8/27 16:42
 * @Version: v1.0
 * @Modified：试卷信息
 * @Description:
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "testPaper")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "试卷信息", description = "试卷信息表")
public class TestPaper extends BaseEntity {
    @ApiModelProperty(name = "testPaperName", value = "试卷名称", dataType = "string")
    private String testPaperName;
    @ApiModelProperty(name = "courseId", value = "课程Id", dataType = "string")
    private String courseId;
    @ApiModelProperty(name = "courseName", value = "课程名称", dataType = "string")
    private String courseName;
    @ApiModelProperty(name = "teacherId", value = "教师", dataType = "string")
    private String teacherId;
    @ApiModelProperty(name = "teacherName", value = "教师名称", dataType = "string")
    private String teacherName;

    @ApiModelProperty(name = "singleList", value = "单选习题编号集合", dataType = "list")
    private List<String> singleList;

    @ApiModelProperty(name = "multipleList", value = "多选集合", dataType = "string")
    private List<String> multipleList;

    @ApiModelProperty(name = "trueOrFalseList", value = "判断习题集合", dataType = "string")
    private List<String> trueOrFalseList;

    @ApiModelProperty(name = "totalScore", value = "总成绩分数", dataType = "int")
    private Integer totalScore;
    @ApiModelProperty(name = "passingScore", value = "及格分数", dataType = "int")
    private Integer passingScore;
    /**
     * 单选题每题分数
     */
    @ApiModelProperty(name = "singleScore", value = "单选题每题分数", dataType = "int")
    private Integer singleScore;
    /**
     * 多选题每题分数
     */
    @ApiModelProperty(name = "multipleScore", value = "多选每题分数", dataType = "int")
    private Integer multipleScore;
    /**
     * 判断每题分值
     */
    @ApiModelProperty(name = "trueOrFalseScore", value = "判断每题分数", dataType = "int")
    private Integer trueOrFalseScore;
    /**
     * 创建日期
     */
    @ApiModelProperty(name = "createDate", value = "创建日期", dataType = "string", required = true)
    private String createDate = DateUtil.today();
}
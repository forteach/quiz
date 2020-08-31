package com.forteach.quiz.testpaper.web.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/8/27 17:02
 * @Version: v1.0
 * @Modified：创建修改试卷
 * @Description:
 */
@Data
@ApiModel(value = "保存更新试卷信息")
public class UpdateTestPaperReq implements Serializable {
    @ApiModelProperty(value = "id", name = "id", example = "5c06d23sz8737b1dc8068da8", notes = "传入id为修改  不传id为新增")
    protected String id;
    @ApiModelProperty(name = "testPaperName", value = "试卷名称", dataType = "string")
    private String testPaperName;
    @NotBlank(message = "课程id不为空")
    @ApiModelProperty(name = "courseId", value = "课程Id", dataType = "string")
    private String courseId;
    @NotBlank(message = "课程名称不为空")
    @ApiModelProperty(name = "courseName", value = "课程名称", dataType = "string")
    private String courseName;
    @NotBlank(message = "教师id不为空")
    @ApiModelProperty(name = "teacherId", value = "教师", dataType = "string")
    private String teacherId;
    @NotBlank(message = "教师名称不为空")
    @ApiModelProperty(name = "teacherName", value = "教师名称", dataType = "string")
    private String teacherName;
    @Size(message = "习题集合不为空")
    @ApiModelProperty(name = "questionList", value = "习题编号集合", dataType = "list")
    private List<String> questionList;
    @ApiModelProperty(name = "totalScore", value = "总成绩分数", dataType = "int")
    private Integer totalScore;
    @ApiModelProperty(name = "passingScore", value = "及格分数", dataType = "int")
    private Integer passingScore;
}

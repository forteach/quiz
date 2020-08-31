package com.forteach.quiz.testpaper.web.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
@Data
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
    @ApiModelProperty(name = "classList", value = "需要考试的班级Id集合", dataType = "list", required = true)
    private List<String> classList;
}

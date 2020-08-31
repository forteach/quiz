package com.forteach.quiz.testpaper.web.req;

import com.forteach.quiz.web.vo.SortVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/8/28 18:10
 * @Version: v1.0
 * @Modified：查询考试记录信息
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "查询考试信息")
public class FindExamInfoReq extends SortVo {

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
}
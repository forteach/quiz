package com.forteach.quiz.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/19  16:58
 */
@Data
@ApiModel(value = "编辑练习册 预习类型")
public class PreviewChangeVo {

    /**
     * 课堂练习：before/预习 now/课堂 before,now/全部
     */
    @ApiModelProperty(value = "课堂练习  before/预习 now/课堂 before,now/全部", name = "preview", dataType = "string", example = "1")
    private String preview;

    /**
     * 章节id
     */
    @ApiModelProperty(value = "章节id", name = "chapterId", example = "章节id", dataType = "string")
    private String chapterId;

    /**
     * 课程id
     */
    @ApiModelProperty(value = "课程id", name = "courseId", example = "章节id", dataType = "string")
    private String courseId;

    /**
     * target id
     */
    @ApiModelProperty(value = "需要修改的题目id", name = "targetId", example = "需要修改的题目id ... ", dataType = "string")
    private String targetId;


}

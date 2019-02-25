package com.forteach.quiz.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import static com.forteach.quiz.common.KeyStorage.INTERACTIVE_CLASSROOM;

/**
 * @Description: 创建临时课堂
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/21  17:58
 */
@Data
@ApiModel(value = "创建临时课堂", description = "创建临时课堂")
public class InteractiveRoomVo {


    /**
     * 教师id
     */
//    @ApiModelProperty(value = "教师id", name = "teacherId")
    private String teacherId;

    /**
     * 章节id
     */
    @ApiModelProperty(value = "章节id", name = "chapterId")
    private String chapterId;


    /**
     * 老师创建临时课堂前缀
     * @return string 临时课堂
     */
    public String getRoomKey() {
        return INTERACTIVE_CLASSROOM.concat(teacherId);
    }

}

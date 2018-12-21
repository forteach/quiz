package com.forteach.quiz.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import static com.forteach.quiz.common.KeyStorage.INTERACTIVE_CLASSROOM;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/21  17:58
 */
@Data
@ApiModel(value = "发布课堂提问", description = "除了提问题,所有问题只有选中的学生才会收到")
public class InteractiveRoomVo {


    @ApiModelProperty(value = "教师id", name = "teacherId")
    private String teacherId;

    @ApiModelProperty(value = "章节id", name = "chapterId")
    private String chapterId;


    public String getRoomKey() {
        return INTERACTIVE_CLASSROOM.concat(teacherId);
    }

}

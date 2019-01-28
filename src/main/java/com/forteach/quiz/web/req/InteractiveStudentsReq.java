package com.forteach.quiz.web.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import static com.forteach.quiz.common.KeyStorage.INTERACTIVE_CLASSROOM_STUDENTS;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/21  18:42
 */
@Data
@ApiModel(value = "查找加入课堂的学生", description = "查找加入课堂的学生")
public class InteractiveStudentsReq {

    /**
     * 课堂圈子id
     */
    @ApiModelProperty(value = "课堂圈子id", name = "circleId")
    private String circleId;

    /**
     * 获取房间键的前缀
     * @return String　房间信息的redis key
     */
    public String getRoomKey() {
        return INTERACTIVE_CLASSROOM_STUDENTS.concat(circleId);
    }

}

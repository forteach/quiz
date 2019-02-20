package com.forteach.quiz.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import static com.forteach.quiz.common.KeyStorage.INTERACTIVE_CLASSROOM_STUDENTS;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/21  18:33
 */
@Data
@ApiModel(value = "学生加入互动课堂", description = "学生加入临时互动课堂")
public class JoinInteractiveRoomVo {

    /**
     * 学生id
     */
//    @ApiModelProperty(value = "学生id", name = "examineeId")
    private String examineeId;

    /**
     * 课堂圈子id
     */
    @ApiModelProperty(value = "课堂圈子id", name = "circleId")
    private String circleId;

    /**
     * 学生加入课堂前缀
     * @return　学生加入信息 redis key
     */
    public String getJoinKey() {
        return INTERACTIVE_CLASSROOM_STUDENTS.concat(circleId);
    }

}

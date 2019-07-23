package com.forteach.quiz.practiser.web.req;

import com.forteach.quiz.practiser.web.req.base.AbstractReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-5 19:14
 * @version: 1.0
 * @description:
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AddRewardReq extends AbstractReq implements Serializable {

    @ApiModelProperty(name = "num", value = "奖励数量", dataType = "string", required = true)
    private String num;

    @ApiModelProperty(name = "studentId", value = "学生id", dataType = "string")
    private String studentId;

    @ApiModelProperty(hidden = true)
    private String teacherId;
}

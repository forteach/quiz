package com.forteach.quiz.interaction.execute.web.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Auther: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2019/1/27 17:22
 * @Version: 1.0
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordVo {
    @NotBlank(message = "课堂id不为空")
    @ApiModelProperty(value = "课堂id", name = "circleId", dataType = "string", required = true)
    private String circleId;
}

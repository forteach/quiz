package com.forteach.quiz.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  16:57
 */
@Data
public abstract class BaseEntity {

    @Id
    @ApiModelProperty(value = "id", name = "id", example = "5c06d23sz8737b1dc8068da8", notes = "传入id为修改  不穿id为新增")
    protected String id;

    @ApiModelProperty(value = "更新时间", name = "uDate", example = "1543950907881", notes = "时间戳")
    protected String uDate;

}

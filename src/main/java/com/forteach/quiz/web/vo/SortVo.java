package com.forteach.quiz.web.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/5  4:36
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortVo {

    @NotNull(message = "页码不能位空")
    @ApiModelProperty(value = "分页", notes = "分页 从0开始", dataType = "int", name = "page", example = "0", required = true)
    private int page;

    @NotNull(message = "每页数量不为空")
    @DecimalMin(value = "0", message = "每页数量不能小于０")
    @DecimalMax(value = "100", message = "每页数量不能大于１００")
    @ApiModelProperty(value = "每页数量", notes = "每页数量", dataType = "int", name = "size", example = "20", required = true)
    private int size;

    @NotNull(message = "排序规则不能为空")
    @ApiModelProperty(value = "排序规则", notes = "依照数据库哪条字段排序 驼峰", dataType = "string", name = "sorting", example = "uTime", required = true)
    private String sorting;

    @Range(min = 0, max = 1, message = "排序方式不正确")
    @ApiModelProperty(value = "sort", name = "排序方式", notes = "排序方式 0 正序　１ 倒叙　默认倒叙(1)", dataType = "int", example = "1")
    private int sort = 1;

    @ApiModelProperty(value = "操作人id", notes = "登陆未完成 手动传入操作人id", dataType = "string", name = "operatorId", example = "001", required = true)
    private String operatorId;

    public void queryPaging(Query query) {
        Sort sort = new Sort(Sort.Direction.DESC, this.getSorting());
        Pageable pageable = PageRequest.of(this.getPage(), this.getSize(), sort);
        query.with(sort);
        query.with(pageable);
    }

}

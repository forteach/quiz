package com.forteach.quiz.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.forteach.quiz.web.vo.BigQuestionView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  16:57
 */
@Data
public abstract class BaseEntity {

    @Id
    @JsonView(BigQuestionView.Summary.class)
    protected String id;

    protected Date cDate;

    protected Date uDate;

}

@EqualsAndHashCode(callSuper = true)
@Data
class AbstractExamEntity extends BaseEntity{

    protected Double score;

    /**
     * 创作老师
     */
    protected String teacherId;
}

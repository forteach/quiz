package com.forteach.quiz.evaluate.domain;

import com.forteach.quiz.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/17  10:16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "evaluate")
public class Evaluate extends BaseEntity {

    private String value;


}

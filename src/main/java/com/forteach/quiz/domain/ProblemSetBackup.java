package com.forteach.quiz.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/21  13:47
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "problemSetBackup")
public class ProblemSetBackup extends BaseEntity {

    /**
     * exerciseBook 练习册   /    paper   试卷
     */
    private String type;

    private String backup;

    public ProblemSetBackup() {
    }

    public ProblemSetBackup(String type, String backup) {
        this.type = type;
        this.backup = backup;
    }
}

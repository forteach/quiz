package com.forteach.quiz.interaction.execute.dto;

import com.forteach.quiz.interaction.execute.domain.record.BrainstormInteractRecord;
import java.io.Serializable;
import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-2-28 09:19
 * @version: 1.0
 * @description: 头脑风暴记录
 */
public class BrainstormDto implements Serializable {
    private List<BrainstormInteractRecord> brainstorms;

    public List<BrainstormInteractRecord> getBrainstorms() {
        return brainstorms;
    }
}

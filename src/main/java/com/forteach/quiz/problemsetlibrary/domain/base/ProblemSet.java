package com.forteach.quiz.problemsetlibrary.domain.base;

import com.forteach.quiz.domain.BaseEntity;
import com.forteach.quiz.domain.QuestionIds;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @Description: 题集
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  16:35
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "编辑题册对象", description = "如果新增 不添加id 如果修改 添加id")
public class ProblemSet extends BaseEntity {

    /**
     * 教师操作人id
     */

    @ApiModelProperty(value = "教师操作人id", name = "teacherId", example = "教师操作人id ")
    private String teacherId;

    /**
     * 题册名
     */

    @ApiModelProperty(value = "题册名", name = "exeBookName", example = "题册名")
    private String exeBookName;

    /**
     * 保存的题目集
     */
    @ApiModelProperty(value = "保存的题目集", name = "questionIds", example = "题目集list")
    private List<QuestionIds> questionIds;

    /**
     * 章节id
     */
    @ApiModelProperty(value = "章节id", name = "chapterId", example = "章节id")
    private String chapterId;

    /**
     * 课程id
     */
    @ApiModelProperty(value = "课程id", name = "courseId", example = "章节id")
    private String courseId;

    /**
     * 难易度id
     */
    @ApiModelProperty(value = "难易度id", name = "levelId", example = "0")
    private String levelId;

    public ProblemSet build(final ProblemSet problemSet) {
        BeanUtils.copyProperties(problemSet, this);
        return this;
    }
}
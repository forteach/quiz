package com.forteach.quiz.domain;

import com.forteach.quiz.web.vo.ExerciseBookVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Description: 练习册类型：1、预习练习册 2、课堂练习册3、课后作业册
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  16:35
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "exerciseBook")
public class ExerciseBook<T> extends BaseEntity {

    @ApiModelProperty(value = "练习册类型：1、预习练习册 2、课堂练习册3、课后作业册", name = "exeBookType", example = "3")
    protected int exeBookType;

    @ApiModelProperty(value = "教师id (创建人id)", name = "teacherId", example = "001")
    protected String teacherId;

    @ApiModelProperty(value = "练习册名称", name = "exeBookName", example = "章节id")
    protected String exeBookName;

    @ApiModelProperty(value = "练习册的子题目集", name = "questionChildren")
    protected List<T> questionChildren;

    /**
     * 章节id
     */

    @ApiModelProperty(value = "章节id", name = "sectionId", example = "章节id")
    private String sectionId;

    /**
     * 知识点id
     */

    @ApiModelProperty(value = "知识点id", name = "knowledgeId", example = "知识点id")
    private String knowledgeId;




    public ExerciseBook() {
    }

    public ExerciseBook(String id, int exeBookType, String teacherId, String exeBookName, List<T> questionChildren, String sectionId, String knowledgeId) {
        this.id = id;
        this.exeBookType = exeBookType;
        this.teacherId = teacherId;
        this.exeBookName = exeBookName;
        this.questionChildren = questionChildren;
        this.sectionId = sectionId;
        this.knowledgeId = knowledgeId;
    }

    public ExerciseBook(final ExerciseBookVo exerciseBookVo, final List<T> list) {
        BeanUtils.copyProperties(exerciseBookVo, this);
        this.questionChildren = list;
    }

}

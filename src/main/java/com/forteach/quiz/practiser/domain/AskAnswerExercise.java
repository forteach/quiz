package com.forteach.quiz.practiser.domain;

import com.forteach.quiz.domain.BaseEntity;
import com.forteach.quiz.interaction.execute.web.vo.DataDatumVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-5 10:32
 * @version: 1.0
 * @description: 练习记录
 */
@Data
@Document(collection = "askAnswerExercise")
@Builder
@AllArgsConstructor
@ApiModel(value = "学生作业回答详情信息", description = "学生作业回答详情信息")
public class AskAnswerExercise extends BaseEntity implements Serializable {


    @ApiModelProperty(value = "练习册类型: 1、提问册 2、练习册3、作业册", name = "exeBookType", example = "3")
    @Indexed
    private String exeBookType;

    @ApiModelProperty(name = "courseId", value = "课程id", dataType = "string")
    @Indexed
    private String courseId;

    @ApiModelProperty(value = "章节id", name = "chapterId", example = "463bcd8e5fed4a33883850c14f877271")
    @Indexed
    protected String chapterId;
    
    @ApiModelProperty(name = "classId", value = "班级id", dataType = "string")
    private String classId;
    /**
     * 课堂练习：before/预习 now/课堂 before,now/全部
     */
    @ApiModelProperty(value = "习题类型  before/预习 now/课堂 after/课后练习", name = "preview", dataType = "string", example = "before")
    @Indexed
    private String preview;
    /**
     * 习题id
     */
    @ApiModelProperty(value = "习题id", name = "questionId")
    @Indexed
    private String questionId;

    /**
     * 学生id
     */
    @Indexed
    @ApiModelProperty(value = "学生id", name = "studentId", dataType = "string")
    private String studentId;

    /**
     * 学生答案
     */
    @ApiModelProperty(value = "回答内容", name = "answer")
    private String answer;

    /**
     * 答案附件
     */
    @ApiModelProperty(value = "附件列表", name = "fileList")
    private List<DataDatumVo> fileList;

    /**
     * 答案图片列表
     */
    private List<String> answerImageList;

    /**
     * 答题得分
     */
    @ApiModelProperty(name = "score", value = "得分", dataType = "string")
    private String score;

    /**
     * 答案评价  主观题: {人工输入:优.良.中.差}    客观题: true  false  halfOf
     */
    @ApiModelProperty(value = "主观题 教师给出的答案评价", name = "evaluation", example = "客观题: true  false  halfOf, 答案评价  主观题: {人工输入:优.良.中.差}    客观题: true  false  halfOf")
    private String evaluation;

    @ApiModelProperty(name = "teacherId", value = "批改教师id", dataType = "string")
    private String teacherId;

}

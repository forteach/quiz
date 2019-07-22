package com.forteach.quiz.questionlibrary.domain.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.forteach.quiz.domain.BaseEntity;
import com.forteach.quiz.web.vo.BigQuestionView;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/10  11:02
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionExamEntity<T> extends BaseEntity {

    /**
     * 题目分数
     */
    @ApiModelProperty(value = "题目分数", name = "score", example = "5")
    protected Double score;

    /**
     * 创作老师
     */
    @ApiModelProperty(value = "创建人id", name = "teacherId", example = "5c06d23sz8737b1dc8068da8")
    protected String teacherId;

    /**
     * 大题用题干
     */
    @ApiModelProperty(value = "大题用题干", name = "paperInfo", example = "阅读理解... ...")
    @JsonView(BigQuestionView.Summary.class)
    protected String paperInfo;

    /**
     * 题目的具体内容信息,是一个list
     */
    @ApiModelProperty(value = "题目的具体内容信息,是一个list", name = "examChildren", required = true)
    @JsonView(BigQuestionView.Summary.class)
    protected List<T> examChildren;

    /**
     * 大题用题目类型 分为主管或客观 director  objective
     */
    @ApiModelProperty(value = "大题用题目类型 分为主管或客观 director  objective", name = "type", example = "director")
    @JsonView(BigQuestionView.Summary.class)
    protected String type;

    /**
     * 章节id
     */
    @JsonView(BigQuestionView.Summary.class)
    @ApiModelProperty(value = "章节id", name = "chapterId", example = "463bcd8e5fed4a33883850c14f877271")
    protected String chapterId;

    /**
     * 是否修改应用到所有的练习册
     * 1 : 应用到所有练习册    0  :  只修改本题
     */
    @Transient
    @ApiModelProperty(value = "是否修改应用到所有的练习册", name = "relate", example = "0")
    private int relate;

    /**
     * 难易度id
     */
    @ApiModelProperty(value = "难易度id", name = "levelId", example = "0")
    private String levelId;

    /**
     * 知识点id
     */
    @ApiModelProperty(value = "知识点id", name = "knowledgeId", example = "0")
    private String knowledgeId;

    /**
     * 关键词
     */
    @ApiModelProperty(value = "关键词", name = "keyword", example = "")
    @Indexed
    private List<String> keyword;

    /**
     * 下标
     * 不保存数据库
     */
    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value = "下标", name = "index", example = "")
    protected String index;

    /**
     * 课堂练习  before/预习 now/课堂 before,now/全部
     */
    @ApiModelProperty(value = "课堂练习  before/预习 now/课堂 before,now/全部", name = "preview", example = "1")
    private String preview;
}

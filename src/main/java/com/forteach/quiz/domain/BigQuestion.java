package com.forteach.quiz.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.forteach.quiz.web.vo.BigQuestionView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  11:04
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "bigQuestion")
@ApiModel(value = "题对象", description = "所有的题目类型 全部由大题外部封装   由examChildren展示具体的题目信息")
public class BigQuestion<T> extends AbstractExamEntity {

    @ApiModelProperty(value = "大题用题干", name = "paperInfo", example = "阅读理解... ...")
    @JsonView(BigQuestionView.Summary.class)
    protected String paperInfo;

    @ApiModelProperty(value = "题目的具体内容信息,是一个list", name = "examChildren", required = true)
    @JsonView(BigQuestionView.Summary.class)
    protected List<T> examChildren;

    @ApiModelProperty(value = "大题用题目类型 分为主管或客观 director  objective", name = "type", example = "director")
    @JsonView(BigQuestionView.Summary.class)
    protected String type;

    @ApiModelProperty(value = "题册排序用坐标", name = "index", example = "1")
    @JsonView(BigQuestionView.SummaryWithDetail.class)
    protected int index;

    /**
     * 是否修改应用到所有的练习册
     * 1 : 应用到所有练习册    0  :  只修改本题
     */
    @ApiModelProperty(value = "大题用题干", name = "paperInfo", example = "0")
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

    public BigQuestion() {
    }

    public BigQuestion(final String id, final List<T> examChildren) {
        this.id = id;
        this.setUDate(new Date());
        this.examChildren = examChildren;
    }

    public BigQuestion(String paperInfo, List<T> examChildren, String type, int index) {
        this.paperInfo = paperInfo;
        this.examChildren = examChildren;
        this.type = type;
        this.index = index;
    }

    public BigQuestion(String teacherId, List<T> examChildren, Double score) {
        this.score = score;
        this.teacherId = teacherId;
        this.examChildren = examChildren;
    }

    public BigQuestion(String teacherId, String paperInfo, List<T> examChildren, Double score) {
        this.score = score;
        this.teacherId = teacherId;
        this.paperInfo = paperInfo;
        this.examChildren = examChildren;
    }

    public BigQuestion(String id, Double score, String teacherId, String paperInfo, List<T> examChildren) {
        this.id = id;
        this.score = score;
        this.teacherId = teacherId;
        this.paperInfo = paperInfo;
        this.examChildren = examChildren;
    }

    public BigQuestion(int index, BigQuestion bigQuestion) {
        this.id = bigQuestion.getId();
        this.score = bigQuestion.getScore();
        this.teacherId = bigQuestion.getTeacherId();
        this.paperInfo = bigQuestion.getPaperInfo();
        this.examChildren = bigQuestion.getExamChildren();
        this.type = bigQuestion.getType();
        this.index = index;
    }
}

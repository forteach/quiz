package com.forteach.quiz.interaction.team.domain;

import com.forteach.quiz.domain.BaseEntity;
import lombok.Data;
import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-4-11 14:23
 * @version: 1.0
 * @description:　小组详情抽象类
 */
@Data
public abstract class BaseTeam extends BaseEntity {

    /**
     * 上课教师id
     */
    private String teacherId;

    /**
     * 小组人数
     */
    private Integer teamNumber;

    /**
     * 小组创建时间
     */
    private String createTime;

    /**
     * 失效时间
     */
    private String loseTime;

    /**
     * 小组类型
     */
    private String expType;

    /**
     * 小组成员信息
     */
    private List<Team> teamList;
}

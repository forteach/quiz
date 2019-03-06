package com.forteach.quiz.web.vo;

import cn.hutool.core.util.StrUtil;
import com.forteach.quiz.interaction.execute.config.ClassRoomKey;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @Description: 创建临时课堂
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/21  17:58
 */
@Data
@ApiModel(value = "创建临时课堂", description = "创建临时课堂")
public class InteractiveRoomVo {

    /**
     * 返回的课堂id
     */
    private String circleId;

    /**
     * 教师id
     */
    @ApiModelProperty(value = "教师id", name = "teacherId")
    private String teacherId;

    /**
     * 章节id
     */
    @ApiModelProperty(value = "章节id", name = "chapterId")
    private String chapterId;


    /**
     * 老师创建临时课堂前缀
     * @return string 临时课堂
     */
    public String getRoomKey() {
        return ClassRoomKey.getRoomKey(teacherId);
    }

    public String getQrCode() {
        return   StrUtil.isBlank(circleId)?"":ClassRoomKey.getInteractiveIdQra(circleId);
    }

    public InteractiveRoomVo(String circleId, String teacherId, String chapterId) {
        this.circleId = circleId;
        this.teacherId = teacherId;
        this.chapterId = chapterId;
    }

    public InteractiveRoomVo(String teacherId, String chapterId) {
        this.teacherId = teacherId;
        this.chapterId = chapterId;
    }

    public InteractiveRoomVo(){

    }
}

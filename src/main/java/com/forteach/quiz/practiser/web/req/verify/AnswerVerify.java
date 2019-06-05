package com.forteach.quiz.practiser.web.req.verify;

import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.practiser.web.req.base.AbstractReq;
import org.springframework.stereotype.Component;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-6-4 11:18
 * @version: 1.0
 * @description:
 */
@Component
public class AnswerVerify {

    public void verify(AbstractReq abstractReq){
        MyAssert.isNull(abstractReq.getChapterId(), DefineCode.ERR0010, "章节不为空");
        MyAssert.isNull(abstractReq.getCourseId(), DefineCode.ERR0010, "课程不为空");
        MyAssert.isNull(abstractReq.getExeBookType(), DefineCode.ERR0010, "练习册/习题册类型类型不为空");
        MyAssert.isNull(abstractReq.getClassId(), DefineCode.ERR0010, "班级不为空");
        MyAssert.isNull(abstractReq.getPreview(), DefineCode.ERR0010, "习题类型不为空");
    }
}

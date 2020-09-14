package com.forteach.quiz.testpaper.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.forteach.quiz.testpaper.domain.ExamInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/8/28 17:18
 * @Version: v1.0
 * @Modified：
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ExamInfoServiceTest {
    @Autowired
    private ExamInfoService examInfoService;

    @Test
    public void saveUpdate() {
        ExamInfo examInfo = new ExamInfo();
        examInfo.setId(IdUtil.objectId());
        examInfo.setClassList(CollUtil.toList());
        examInfo.setStartDateTime(DateUtil.offsetHour(new Date(), 1).toString());
        examInfo.setEndDateTime(DateUtil.offsetHour(new Date(), 3).toString());
        examInfo.setSemester(2);
        examInfo.setTeacherId("12");
        examInfo.setTeacherName("张老师");
        examInfoService.saveUpdate(examInfo)
                .log()
//                .doOnNext(System.out::println)
                .subscribe(System.out::println);
    }

    @Test
    public void findAll() {
    }
}

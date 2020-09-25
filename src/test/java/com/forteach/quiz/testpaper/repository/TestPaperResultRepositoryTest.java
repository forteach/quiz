package com.forteach.quiz.testpaper.repository;

import com.forteach.quiz.testpaper.domain.TestPaperResult;
import com.forteach.quiz.testpaper.web.vo.ResultVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/9/14 14:23
 * @Version: v1.0
 * @Modified：
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestPaperResultRepositoryTest {
    @Autowired
    private TestPaperResultRepository testPaperResultRepository;


    @Test
    public void save() {
        TestPaperResult testPaperResult = new TestPaperResult();
        testPaperResult.setClassId("1934");
        testPaperResult.setClassName("1934班");
        testPaperResult.setCourseId("3823fd2e06db41dea569ee4526d9a931");
        testPaperResult.setCourseName("商务英语");
        testPaperResult.setStudentId("000");
        testPaperResult.setStudentName("学生1");
        ResultVo resultVo = new ResultVo();
        resultVo.setQuestionType("multiple");
        resultVo.setQuestionId("5ed751b8ee0491823524bca9");
        resultVo.setAnswer("AD");
        resultVo.setScore(12);
        resultVo.setAssess("说明");
        ArrayList<ResultVo> list = new ArrayList<>();
        testPaperResult.setResultList(list);
        testPaperResultRepository.save(testPaperResult);
    }

    @Test
    public void findAllByTestPaperIdAndStudentId() {
        testPaperResultRepository.findAllByTestPaperIdAndStudentId("000", "学生111")
                .collectList()
                .log()
                .subscribe(System.out::println);
    }
}

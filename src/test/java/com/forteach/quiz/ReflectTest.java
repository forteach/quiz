package com.forteach.quiz;

import com.forteach.quiz.questionLibrary.domain.question.TrueOrFalse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/10  15:14
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ReflectTest {

    @Test
    public void reflect() {

        Object o = new TrueOrFalse();
        TrueOrFalse t = new TrueOrFalse();
        Class c = o.getClass();


        if (c.isAssignableFrom(String.class)) {
            System.out.println("ok");
        }
        System.out.println(o.getClass());

    }


}

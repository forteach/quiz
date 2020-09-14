package com.forteach.quiz;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static java.util.stream.Collectors.joining;

@RunWith(SpringRunner.class)
public class QuizApplicationTests {

    @Test
    public void contextLoads() {
        String str=Arrays.stream("1,2,3".split(",")).
                filter(id->!id.equals("2"))
                .map(str1->",".concat(str1)
                ).collect(joining())
                .substring(1);
        System.out.println(str);
    }

}

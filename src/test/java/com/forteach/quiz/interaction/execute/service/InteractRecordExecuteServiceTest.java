package com.forteach.quiz.interaction.execute.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @Auther: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2019/1/27 16:09
 * @Version: 1.0
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class InteractRecordExecuteServiceTest {

    @Autowired
    private InteractRecordExecuteService interactRecordExecuteService;
    @Test
    public void getRecord() {
//        interactRecordExecuteService.getRecord("0cf3feef4ba84ae99d5e24c4d3c8270f");
    }

    @Test
    public void findRecord() {
//        interactRecordExecuteService.selectRecord("d4ab75daef9344f681a667390c0b4532", null);
    }
}
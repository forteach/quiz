package com.forteach.quiz.controller;

import com.alibaba.fastjson.JSON;
import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.interaction.execute.web.vo.BigQuestionGiveVo;
import com.forteach.quiz.web.vo.InteractAnswerVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BigQuestioTest {

    @Resource
    private WebTestClient webTestClient;

    @Test
    public void sendQuestion(){
        /**
         * 互动方式
         * <p>
         * race   : 抢答
         * raise  : 举手
         * select : 选则
         * vote   : 投票
         */
        BigQuestionGiveVo vo=new BigQuestionGiveVo("q01","select");
        vo.setCircleId("5c88b6c5c762c12414c09f49");
        vo.setTeacherId("t01");
        vo.setCategory("people");
        vo.setSelected("stu02,stu01");
        vo.setCut("0");
        System.out.println("json------"+ JSON.toJSONString(vo));
        webTestClient
                .post().uri("/interact/send/question")  //创建2小时内同一课堂
                // .post().uri("/classRoom/create/cover")//创建不同的课堂
                .contentType(MediaType.APPLICATION_JSON) // 2
                .body(Mono.just(vo),BigQuestionGiveVo.class) // 3
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .returnResult(WebResult.class)
                .getResponseBody().subscribe(System.out::println);

    }

    @Test
    public void sendAnswer(){
        /**
         * 互动方式
         * <p>
         * race   : 抢答
         * raise  : 举手
         * select : 选则
         * vote   : 投票
         */
        InteractAnswerVo vo=new InteractAnswerVo("stu01","5c88b6c5c762c12414c09f49","q01","answer-ok","cut");

        System.out.println("json------"+ JSON.toJSONString(vo));
        webTestClient
                .post().uri("/interact/send/answer")  //创建2小时内同一课堂
                // .post().uri("/classRoom/create/cover")//创建不同的课堂
                .contentType(MediaType.APPLICATION_JSON) // 2
                .body(Mono.just(vo),InteractAnswerVo.class) // 3
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .returnResult(WebResult.class)
                .getResponseBody().subscribe(System.out::println);

    }
}

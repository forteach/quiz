package com.forteach.quiz.controller;

import com.alibaba.fastjson.JSON;
import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.interaction.execute.web.vo.MoreGiveVo;
import com.forteach.quiz.web.vo.InteractAnswerVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BigBookTest {

    @Resource
    private WebTestClient webTestClient;

    @Test
    public void sendInteractiveBook(){
        /**
         * 互动方式
         * <p>
         * race   : 抢答
         * raise  : 举手
         * select : 选则
         * vote   : 投票
         */
        MoreGiveVo vo=new MoreGiveVo();
        vo.setCircleId("5cd3d58f5157212350577a58");
        vo.setQuestionId("5c73676306a38f000101b7b6,5c73679106a38f000101b7b7");
        vo.setTeacherId("we123");
        vo.setCategory("people");
        vo.setSelected("1301331992031827761,1301331992031827761");

        System.out.println("json------"+ JSON.toJSONString(vo));
        webTestClient
                .post().uri("/interact/send/book")  //创建2小时内同一课堂
                // .post().uri("/classRoom/create/cover")//创建不同的课堂
                .contentType(MediaType.APPLICATION_JSON) // 2
                .body(Mono.just(vo),MoreGiveVo.class) // 3
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .returnResult(WebResult.class)
                .getResponseBody().subscribe(System.out::println);

    }

    @Test
    public void sendBookAnswer(){
        /**
         * 互动方式
         * <p>
         * race   : 抢答
         * raise  : 举手
         * select : 选则
         * vote   : 投票
         */
        InteractAnswerVo vo=new InteractAnswerVo(
                "1301331992031827761",
                "5cd3d58f5157212350577a58",
                "5c73676306a38f000101b7b6",
                "C",
                "LianXi");

        System.out.println("json------"+ JSON.toJSONString(vo));
        webTestClient
                .post().uri("/interact/sendBook/answer")  //创建2小时内同一课堂
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

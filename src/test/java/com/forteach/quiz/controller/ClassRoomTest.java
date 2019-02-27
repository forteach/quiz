package com.forteach.quiz.controller;


import com.alibaba.fastjson.JSON;
import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.interaction.execute.web.control.ClassRoomController;
import com.forteach.quiz.web.vo.InteractiveRoomVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import javax.annotation.Resource;

//http://www.leftso.com/blog/405.html  https://blog.csdn.net/HiBoyljw/article/details/82783443

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClassRoomTest {

    @Resource
    private WebTestClient webTestClient;


    @Test
    public void createInteractiveRoom(){
        InteractiveRoomVo vo=new InteractiveRoomVo("t01","cp01");
        System.out.println("json------"+ JSON.toJSONString(vo));
        webTestClient
                .post().uri("/classRoom/create/reuse")
                .contentType(MediaType.APPLICATION_JSON) // 2
                .body(Mono.just(vo),InteractiveRoomVo.class) // 3
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
               // .expectBody()
                .returnResult(WebResult.class)
                .getResponseBody().subscribe(System.out::println);

    }
}

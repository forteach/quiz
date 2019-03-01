package com.forteach.quiz.controller;


import com.alibaba.fastjson.JSON;
import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.web.req.InteractiveStudentsReq;
import com.forteach.quiz.web.vo.InteractiveRoomVo;
import com.forteach.quiz.web.vo.JoinInteractiveRoomVo;
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
        InteractiveRoomVo vo=new InteractiveRoomVo("5c789b16c762c13370c0d62f","t01","cp01");
        System.out.println("json------"+ JSON.toJSONString(vo));
        webTestClient
                .post().uri("/classRoom/create/reuse")  //创建2小时内同一课堂
               // .post().uri("/classRoom/create/cover")//创建不同的课堂
                .contentType(MediaType.APPLICATION_JSON) // 2
                .body(Mono.just(vo),InteractiveRoomVo.class) // 3
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .returnResult(WebResult.class)
                .getResponseBody().subscribe(System.out::println);

    }

    @Test
    public void joinInteractiveRoom(){
        JoinInteractiveRoomVo vo=new JoinInteractiveRoomVo("stu02","5c789bfdc762c12a908766f6");
        System.out.println("json------"+ JSON.toJSONString(vo));
        webTestClient
                .post().uri("/classRoom/join/interactiveRoom")
                .contentType(MediaType.APPLICATION_JSON) // 2
                .body(Mono.just(vo),JoinInteractiveRoomVo.class) // 3
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                // .expectBody()
                .returnResult(WebResult.class)
                .getResponseBody().subscribe(System.out::println);

    }

    @Test
    public void findInteractiveStudents(){
        InteractiveStudentsReq vo=new InteractiveStudentsReq("5c789bfdc762c12a908766f6","t01");
        System.out.println("json------"+ JSON.toJSONString(vo));
        webTestClient
                .post().uri("/classRoom//find/interactiveStudents")
                .contentType(MediaType.APPLICATION_JSON) // 2
                .body(Mono.just(vo),InteractiveStudentsReq.class) // 3
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                // .expectBody()
                .returnResult(WebResult.class)
                .getResponseBody().subscribe(System.out::println);

    }

    @Test
    public void a(){
        InteractiveRoomVo vo=new InteractiveRoomVo("t01","cp01");
        webTestClient
                .post().uri("/classRoom/test")
                .contentType(MediaType.APPLICATION_JSON) // 2
                .body(Mono.just(vo),InteractiveRoomVo.class) // 3
                .exchange()
                .expectStatus()
                .isOk()
               // .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                // .expectBody()
                .returnResult(String.class)
                .getResponseBody().subscribe(System.out::println);
    }
}

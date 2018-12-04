package com.forteach.quiz.web;

import com.fasterxml.jackson.annotation.JsonView;
import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.service.ClassInteractService;
import com.forteach.quiz.web.vo.*;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/19  14:46
 */
@RestController
@RequestMapping("/interact")
public class InteractCollection extends BaseController {

    private final ClassInteractService classInteractService;

    public InteractCollection(ClassInteractService classInteractService) {
        this.classInteractService = classInteractService;
    }

    /**
     * 主动推送问题
     *
     * @param examineeId
     * @param circleId
     * @param random
     * @return
     */
    @JsonView(BigQuestionView.Summary.class)
    @GetMapping(value = "/achieve", produces = "text/event-stream;charset=UTF-8")
    public Flux<ServerSentEvent<AskQuestionVo>> achieveQuestions(@RequestParam String examineeId, @RequestParam String circleId, @RequestParam String random) {

        return Flux.interval(Duration.ofSeconds(1))
                .map(seq -> Tuples.of(
                        seq, classInteractService.achieveQuestion(AchieveVo.builder().circleId(circleId).examineeId(examineeId).random(random).build())
                ))
                .flatMap(Tuple2::getT2)
                .map(data -> ServerSentEvent.<AskQuestionVo>builder()
                        .data(data)
                        .build());
    }

    /**
     * 发布问题
     * @param giveVo
     * @return
     */
    @PostMapping("/send/question")
    public Mono<WebResult> sendQuestion(@RequestBody GiveVo giveVo) {
        return classInteractService.sendQuestion(giveVo).map(WebResult::okResult);
    }

    /**
     * 提交答案
     *
     * @param interactAnswerVo
     * @return
     */
    @PostMapping("/send/answer")
    public Mono<WebResult> sendAnswer(@RequestBody InteractAnswerVo interactAnswerVo) {
        return classInteractService.sendAnswer(interactAnswerVo).map(WebResult::okResult);
    }


}

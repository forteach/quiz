package com.forteach.quiz.web;

import com.forteach.quiz.common.WebResult;
import com.forteach.quiz.domain.BigQuestion;
import com.forteach.quiz.service.ClassInteractService;
import com.forteach.quiz.web.vo.AchieveVo;
import com.forteach.quiz.web.vo.GiveVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@Data
@RestController
@EqualsAndHashCode(callSuper = true)
@RequestMapping("/interact")
public class InteractCollection extends BaseController {

    private final ClassInteractService classInteractService;

    public InteractCollection(ClassInteractService classInteractService) {
        this.classInteractService = classInteractService;
    }

    @GetMapping(value = "/achieve", produces = "text/event-stream;charset=UTF-8")
    public Flux<ServerSentEvent<BigQuestion>> achieveQuestions(@RequestParam String examineeId, @RequestParam String circleId, @RequestParam String random) {

        return Flux.interval(Duration.ofSeconds(1))
                .map(seq -> Tuples.of(
                        seq, classInteractService.achieveQuestion(AchieveVo.builder().circleId(circleId).examineeId(examineeId).random(random).build())
                ))
                .flatMap(Tuple2::getT2)
                .map(data -> ServerSentEvent.<BigQuestion>builder()
                        .data(data)
                        .build());
    }

    @PostMapping("/send/question")
    public Mono<WebResult> sendQuestion(@RequestBody GiveVo giveVo) {
        return classInteractService.sendQuestion(giveVo).map(WebResult::okResult);
    }

}

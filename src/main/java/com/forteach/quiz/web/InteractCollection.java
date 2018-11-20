package com.forteach.quiz.web;

import com.forteach.quiz.domain.Design;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

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

    /**
     * 模拟数据库数据
     */
    private static Map<Integer, Design> dataMap;

    static {
        dataMap = new HashMap();
        dataMap.put(1, new Design("最终幻想12前身是什么", "123", "", 3D));
        dataMap.put(2, new Design("最终幻想13前身是什么", "456", "", 3D));
        dataMap.put(3, new Design("最终幻想14前身是什么", "789", "", 3D));
        dataMap.put(4, new Design("最终幻想15前身是什么", "123", "", 3D));
    }

    @GetMapping(value = "/typeOne", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux interactResponse() {
        return Flux.fromIterable(dataMap.values()).delayElements(Duration.ofSeconds(1));
    }

    @PostMapping(value = "/add")
    public Mono sseResponse(@RequestBody Aa aa) {
        dataMap.put(dataMap.size() + 1, new Design(aa.getDq(), aa.getDa(), aa.getDas(), aa.getS()));
        return Mono.just(dataMap);
    }

    @RequestMapping(value = "/retrieve",produces = "text/event-stream;charset=UTF-8")
    public Flux<ServerSentEvent<Integer>> randomNumbers() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(seq -> Tuples.of(seq, ThreadLocalRandom.current().nextInt()))
                .map(data -> ServerSentEvent.<Integer>builder()
                        .event(String.valueOf(dataMap.size()))
                        .id(Long.toString(data.getT1()))
                        .data(data.getT2())
                        .build());
    }



}
@Data
class Aa {
    String dq;
    String da;
    String das;
    double s;
}
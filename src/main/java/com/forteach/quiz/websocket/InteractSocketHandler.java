//package com.forteach.quiz.websocket;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.web.reactive.socket.WebSocketHandler;
//import org.springframework.web.reactive.socket.WebSocketMessage;
//import org.springframework.web.reactive.socket.WebSocketSession;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.time.Duration;
//import java.util.Date;
//
///**
// * @Description:
// * @author: liu zhenming
// * @version: V1.0
// * @date: 2018/12/25  14:20
// */
//public class InteractSocketHandler implements WebSocketHandler {
//
////    @Override
////    public Mono<Void> handle(WebSocketSession session) {
////
////        Flux<WebSocketMessage> output = session.receive()
////                .doOnNext(message -> System.out.println("接收到消息:"+message.retain()))
////                .concatMap(message -> {
////
////                })
////                .map(value -> session.textMessage("服务器发送 " + value.retain().getPayloadAsText()));
////
////        return session.send(output);
////    }
//private static final ObjectMapper json = new ObjectMapper();
//
//
////    private Flux<String> eventFlux = Flux.generate(sink -> {
////        Event event = new Event(randomUUID().toString(),1, now().toString());
////        try {
////            sink.next(json.writeValueAsString(event));
////        } catch (JsonProcessingException e) {
////            sink.error(e);
////        }
////    });
//
//    private Flux<String> eventFlux = Flux.generate(sink -> {
////        Event event = new Event(randomUUID().toString(), now().toString());
//        sink.next(String.format("{ message: 'got local message', date: '%s' }", new Date()));
//    });
//
//    private Flux<String> intervalFlux = Flux.interval(Duration.ofSeconds(2L))
//            .zipWith(eventFlux, (time, event) -> event);
//
//    @Override
//    public Mono<Void> handle(WebSocketSession session) {
//        return session.send(intervalFlux
//                .map(session::textMessage))
//                .and(session.receive()
//                        .map(WebSocketMessage::getPayloadAsText).log());
//    }
//
////    @Override
////    public Mono<Void> handle(WebSocketSession session) {
////        String number = getRandom();
////        System.out.println("number = "  + number);
////
////        boolean isWin = false;
//////
//////        return session.send(Mono.just(session.textMessage("Start round")))
//////                .and(session.receive()
//////                        .map(new WebSocketMessage())}
//////                        ).log());
////
////        return session.send(Mono.just(session.textMessage("Start round")))
////                .then(
////
////                        session.receive()
////                                .map(webSocketMessage -> {
////                                    String payloadAsText = webSocketMessage.getPayloadAsText();
////                                    String answer = payloadAsText.equals(number) ? "You win!" : "You lose!";
////                                    System.out.println(answer);
////                                    session.send(Mono.just(session.textMessage(answer)));
////                                    System.out.println("!!!" + payloadAsText);
////                                    return payloadAsText;
////                                }).then()
////                )
////                .then();
//
////        return session.send(
////                Flux.<String>generate(sink -> sink.next(String.format("{ message: 'got local message', date: '%s' }", new Date())))
////                        .delayElements(Duration.ofSeconds(1)).map(payload -> {
////                    return session.textMessage(payload);
////                }));
//
//    }
//
////    private static String getRandom(){
////        int min = 1;
////        int max = 10;
////        Random random = new Random();
////        return String.valueOf(random.nextInt(max - min + 1) + min);
////    }
//

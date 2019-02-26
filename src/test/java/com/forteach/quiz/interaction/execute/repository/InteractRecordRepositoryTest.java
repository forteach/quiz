package com.forteach.quiz.interaction.execute.repository;

import com.forteach.quiz.interaction.execute.domain.InteractRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.util.Objects;
import java.util.logging.Level;

import static org.junit.Assert.*;

/**
 * @Auther: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2019/1/26 15:08
 * @Version: 1.0
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class InteractRecordRepositoryTest {
    @Autowired
    private InteractRecordRepository recordRepository;

    @Test
    public void findAllByCircleId(){

    }
    @Test
    public void findByCircleIdAndQuestionsNotNull(){
        Mono<InteractRecord> interactRecordMono = recordRepository.findByCircleIdAndQuestionsNotNull("bf7cda019c5049d18c002b502b053c46");
        interactRecordMono.filter(Objects::nonNull)
//                .
                .log(interactRecordMono.toString(), Level.INFO, SignalType.REQUEST);
    }
    @Test
    public void findByCircleIdAndRecordName(){
        Mono<InteractRecord> interactRecordMono = recordRepository.findByCircleIdAndRecord("bf7cda019c5049d18c002b502b053c46","questions");
        interactRecordMono.filter(Objects::nonNull)
//                .
                .log(interactRecordMono.toString(), Level.INFO, SignalType.REQUEST);
    }

    @Test
    public void findByCircleIdAndQuestionsId(){
//        Mono<InteractRecord> interactRecordMono = recordRepository.findByCircleIdAndQuestionsId("interactionQr2d4b8477ef2b4f92943f8383d07918b3","5c73676306a38f000101b7b6");
//        interactRecordMono.log();
//        interactRecordMono.log(interactRecordMono.toString(), Level.INFO, SignalType.REQUEST);
//
//        System.out.println("---->> "+interactRecordMono.hasElement());
    }
}
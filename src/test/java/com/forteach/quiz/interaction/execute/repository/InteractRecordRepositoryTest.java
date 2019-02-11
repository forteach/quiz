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
        Mono<InteractRecord> interactRecordMono = recordRepository.findByCircleIdAndQuestionsId("bd4a84e4a61943e6b07e02947ecc85f1","5c332ae152faff0001056c0d");
        interactRecordMono.log();
        interactRecordMono.log(interactRecordMono.toString(), Level.INFO, SignalType.REQUEST);

        System.out.println("---->> "+interactRecordMono.hasElement());
    }
}
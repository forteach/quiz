package com.forteach.quiz.interaction.execute.repository;

import com.forteach.quiz.interaction.execute.domain.record.InteractRecord;
import com.forteach.quiz.interaction.execute.dto.QuestionsDto;
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
        Mono<InteractRecord> interactRecordMono = recordRepository.findByIdAndQuestionsNotNull("bf7cda019c5049d18c002b502b053c46");
        interactRecordMono.filter(Objects::nonNull)
//                .
                .log(interactRecordMono.toString(), Level.INFO, SignalType.REQUEST);
    }
    @Test
    public void findByCircleIdAndRecordName(){
        Mono<InteractRecord> interactRecordMono = recordRepository.findByIdAndRecord("bf7cda019c5049d18c002b502b053c46","questions");
        interactRecordMono.filter(Objects::nonNull)
                .log("---->> ")
                .subscribe(System.out::println);
//                .map(InteractRecord::getQuestions)
//                .log(InteractRecord::getCeateTime,Level.INFO, SignalType.REQUEST);
    }

    @Test
    public void findByCircleIdAndQuestionsId(){
        Mono<QuestionsDto> interactRecordMono = recordRepository.findRecordByIdAndQuestionsId("5c9352cade262d00019cf13e","5c73676306a38f000101b7b6");
        interactRecordMono.filter(Objects::nonNull)
                .map(QuestionsDto::getQuestions)
                .flatMapMany(Flux::fromIterable)
                .log("--->> ")
                .doOnNext(System.out::println)
                .subscribe(System.out::println);
    }
}
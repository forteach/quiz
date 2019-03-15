package com.forteach.quiz.interaction.execute.repository;

import com.forteach.quiz.interaction.execute.domain.record.InteractRecord;
import com.forteach.quiz.interaction.execute.dto.QuestionsDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
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
//                .
                .log(interactRecordMono.toString(), Level.INFO, SignalType.REQUEST);
    }

    @Test
    public void findByCircleIdAndQuestionsId(){
        Mono<QuestionsDto> interactRecordMono = recordRepository.findRecordByIdAndQuestionsId("interactionQr2d4b8477ef2b4f92943f8383d07918b3","5c10b2a9dc623b4024d693ae");
//        interactRecordMono.log();
//        interactRecordMono.log(interactRecordMono.toString(), Level.INFO, SignalType.REQUEST);
        System.out.println(interactRecordMono.block().getQuestions().get(0).toString());
        System.out.println("---->> "+interactRecordMono.hasElement());
    }
}
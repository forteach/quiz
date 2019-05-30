package com.forteach.quiz.interaction.execute.repository;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
//    @Autowired
//    private InteractRecordRepository recordRepository;
//    @Autowired
//    private ReactiveMongoTemplate mongoTemplate;

//    @Test
//    public void findAllByCircleId(){
//
//    }
//    @Test
//    public void findByCircleIdAndQuestionsNotNull(){
//        Mono<InteractRecord> interactRecordMono = recordRepository.findByIdAndQuestionsNotNull("bf7cda019c5049d18c002b502b053c46");
//        interactRecordMono.filter(Objects::nonNull)
////                .
//                .log(interactRecordMono.toString(), Level.INFO, SignalType.REQUEST);
//    }
//    @Test
//    public void findByCircleIdAndRecordName(){
//        Mono<InteractRecord> interactRecordMono = recordRepository.findByIdAndRecord("bf7cda019c5049d18c002b502b053c46","questions");
//        interactRecordMono.filter(Objects::nonNull)
//                .log("---->> ")
//                .subscribe(System.out::println);
////                .map(InteractRecord::getQuestions)
////                .log(InteractRecord::getCeateTime,Level.INFO, SignalType.REQUEST);
//    }

//    @Test
//    public void findByCircleIdAndQuestionsId(){
//        Mono<QuestionsDto> interactRecordMono = recordRepository.findRecordByIdAndQuestionsId("5ca4157cf3de513719fa4d65","5c9dd110c3da7a0001d12cde");
//        interactRecordMono.filter(Objects::nonNull)
//                .map(QuestionsDto::getQuestions)
//                .flatMapMany(Flux::fromIterable)
//                .log("--->> ")
//                .doOnNext(System.out::println)
//                .subscribe(System.out::println);
//    }
//    @Test
//    public void findByIdTest(){
////        Query query = Query.query(Criteria.where("_id").is("5ca4157cf3de513719fa4d65"));
////        mongoTemplate.findOne(query, InteractRecord.class)
//        mongoTemplate.findById("5ca47029f3de514fbc4788e3", InteractRecord.class)
//                .log(" <<<==============>>> ")
//                .subscribe(System.out::println);
//    }
}
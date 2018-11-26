package com.forteach.quiz;

import com.forteach.quiz.domain.BigQuestion;
import com.forteach.quiz.domain.Design;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.forteach.quiz.common.Dic.MONGDB_ID;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/14  22:03
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoTemplateTest {

    @Autowired
    ReactiveMongoTemplate template;

    @Before
    public void setUp() {

        StepVerifier.create(template.dropCollection(Design.class)).verifyComplete();

        Flux<Design> insertAll = template
                .insertAll(Flux.just(new Design("1+1= ?", "2", "幼儿园难度1", 1.1), //
                        new Design("1+2= ?", "3", "幼儿园难度2", 1.2), //
                        new Design("1+3= ?", "4", "幼儿园难度3", 1.3), //
                        new Design("1+4= ?", "5", "幼儿园难度4", 1.4)).collectList());

        StepVerifier.create(insertAll).expectNextCount(4).verifyComplete();
    }

    /**
     * This sample performs a count, inserts data and performs a count again using reactive operator chaining. It prints
     * the two counts ({@code 4} and {@code 6}) to the console.
     */
    @Test
    public void shouldInsertAndCountData() {

        Mono<Long> count = template.count(new Query(), Design.class) //
                .doOnNext(System.out::println) //
                .thenMany(template.insertAll(Arrays.asList(new Design("最终幻想是什么公司出品", "SE", "日本的史克威尔艾尼克斯", 43.0), //
                        new Design("刺客信条是什么公司出品", "育碧", "法国育碧", 73.0)))) //
                .last() //
                .flatMap(v -> template.count(new Query(), Design.class)) //
                .doOnNext(System.out::println);//

        StepVerifier.create(count).expectNext(6L).verifyComplete();
    }

    /**
     * Note that the all object conversions are performed before the results are printed to the console.
     */
    @Test
    public void convertReactorTypesToRxJava2() {

        Flux<Design> flux = template.find(Query.query(Criteria.where("designQuestion").is("1+1= ?")), Design.class);
        Mono<Long> count = flux.count();
        StepVerifier.create(count).expectNext(1L).verifyComplete();
    }

//    public Flux<BigQuestion> findBigQuestionInId(final List<String> ids) {
//        return ;
//    }

    @Test
    public void findBigQuestionInId() {
        List<String> ids = new ArrayList<>();
        ids.add("5bed3b5bdc623b2484bf6bed");
        ids.add("5bed3b5edc623b2484bf6bee");
//        ids.add("5bed3b17dc623b2484bf6bec");
        Flux<BigQuestion> bigQuestionFlux = template.find(Query.query(Criteria.where(MONGDB_ID).in(ids)), BigQuestion.class)
                .doOnNext(System.out::println);
        Mono<Long> count = bigQuestionFlux.count().doOnNext(System.out::println);

        StepVerifier.create(count).expectNext(2L).verifyComplete();
    }


}

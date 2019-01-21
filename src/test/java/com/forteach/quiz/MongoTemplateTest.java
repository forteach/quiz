package com.forteach.quiz;

import com.alibaba.fastjson.JSON;
import com.forteach.quiz.interaction.domain.AskAnswer;
import com.forteach.quiz.problemsetlibrary.domain.base.ExerciseBook;
import com.forteach.quiz.questionlibrary.domain.BigQuestion;
import com.forteach.quiz.questionlibrary.domain.question.Design;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.forteach.quiz.common.Dic.MONGDB_ID;
import static com.forteach.quiz.common.Dic.QUESTION_CHILDREN;

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

    @Test
    public void editQuestionsCover() {
        String json = "{\n" +
                "    \"id\": \"5bf7695edc623b41b00627ac\",\n" +
                "    \"score\": 31,\n" +
                "    \"teacherId\": \"001\",\n" +
                "    \"paperInfo\": \"史可威尔0 问题\",\n" +
                "    \"type\": \"design\",\n" +
                "    \"index\": \"3\",\n" +
                "    \"examChildren\": [\n" +
                "        {\n" +
                "            \"id\": \"7b08be81fa80443884cc107573d01f1a\",\n" +
                "            \"relate\": \"1\",\n" +
                "            \"score\": 7,\n" +
                "            \"teacherId\": \"001\",\n" +
                "            \"designQuestion\": \"ff14前身是什么\",\n" +
                "            \"designAnsw\": \"最终幻想 14 1.0\",\n" +
                "            \"designAnalysis\": \"最终幻想14重置过\",\n" +
                "            \"examType\": \"design\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        BigQuestion obj = JSON.parseObject(json, BigQuestion.class);

//        Flux<ExerciseBook> mono= template.find(Query.query(Criteria.where(MONGDB_ID).is("5bf79d11dc623b1cfc3a5eb1")),ExerciseBook.class).doOnNext(System.out::println);

//                StepVerifier.create(mono).expectNext().verifyComplete();


        Query query = Query.query(Criteria.where(QUESTION_CHILDREN + "." + MONGDB_ID).is("5bf7695edc623b41b00627ac"));
        Update update = new Update();
        update.set("questionChildren.$.paperInfo", obj.getPaperInfo());
        update.set("questionChildren.$.examChildren", obj.getExamChildren());
        update.set("questionChildren.$.type", obj.getType());
        update.set("questionChildren.$.score", obj.getScore());
        update.set("questionChildren.$.teacherId", obj.getTeacherId());
//        Update update = Update.update("examChildren.$.score",5).set("teacherId",123);
//
        Mono<ExerciseBook> exerciseBookMono = template.findAndModify(query, update, ExerciseBook.class).doOnNext(System.out::println);
//
        StepVerifier.create(exerciseBookMono).expectNext().verifyComplete();
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

    @Test
    public void findAskAnswer() {


        Query query = Query.query(
                Criteria.where("circleId").in("1")
                        .and("questionId").in("5c07e066c8737b1b94c93c9a")
                        .and("examineeId").in("10001"));

//        Flux<AskAnswer> answerMono = template.find(query, AskAnswer.class).doOnNext(System.out::println);
        Mono<AskAnswer> answerMono = template.findOne(query, AskAnswer.class).doOnNext(System.out::println);

        StepVerifier.create(answerMono).expectNext().verifyComplete();

//        Mono<Long> count = bigQuestionFlux.count().doOnNext(System.out::println);
    }

}

package com.forteach.quiz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.forteach.quiz.domain.BigQuestion;
import com.forteach.quiz.domain.ExerciseBook;
import com.forteach.quiz.domain.TrueOrFalse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/22  11:23
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JsonTest {

    @Test
    public void jsonTest() {

        String json = "{\"cDate\":1542694774958,\"exeBookName\":\"即时课堂练习册\",\"exeBookType\":2,\"id\":\"5bf3a776dc623b4d34ae1c31\",\"questionChildr" +
                "en\":[{\"examChildren\":[{\"id\":\"1\",\"score\":5,\"examType\":\"trueOrFalse\",\"teacherId\":\"001\",\"trueOrFalseInfo\":\"web flux 是无" +
                "阻塞吗\",\"trueOrFalseAnsw\":true,\"trueOrFalseAnalysis\":\"webflux是无阻塞框架\"}],\"id\":\"5bf3a6e2dc623b15e4be1dcb\",\"ind" +
                "ex\":2,\"paperInfo\":\"思考题  \",\"score\":5.0,\"teacherId\":\"001\",\"uDate\":1542771177280},{\"examChildren\":[{\"id\":\"1\",\"" +
                "score\":5,\"teacherId\":\"001\",\"choiceQstTxt\":\"1 ( 单选题 ) ( )(本题5.0分)\",\"choiceQstAnsw\":\"A\",\"choiceQstA" +
                "nalysis\":\"解析。。。。。。\",\"choiceType\":\"single\",\"examType\":\"choice\",\"optChildren\":[{\"id\":\"1\",\"optTxt\":\"" +
                "skill\",\"optValue\":\"A\"},{\"id\":\"2\",\"optTxt\":\"experience\",\"optValue\":\"B\"},{\"id\":\"3\",\"optTxt\":\"pra" +
                "ctice\",\"optValue\":\"C\"},{\"id\":\"4\",\"optTxt\":\"method\",\"optValue\":\"D\"}]},{\"id\":\"2\",\"score\":5,\"teacherId\"" +
                ":\"001\",\"choiceQstTxt\":\"2 ( 单选题 ) ( )(本题5.0分)\",\"choiceQstAnsw\":\"C\",\"choiceQstAnalysis\":\"解析。。。。。。\"" +
                ",\"choiceType\":\"single\",\"optChildren\":[{\"id\":\"1\",\"optTxt\":\"duty\",\"optValue\":\"A\"},{\"id\":\"2\",\"optTxt\":\"" +
                "effort\",\"optValue\":\"B\"},{\"id\":\"3\",\"optTxt\":\"job\",\"optValue\":\"C\"},{\"id\":\"4\",\"optTxt\":\"task\",\"optValue\"" +
                ":\"D\"}]},{\"id\":\"3\",\"score\":5,\"teacherId\":\"001\",\"choiceQstTxt\":\"3 ( 单选题 ) ( )(本题5.0分)\",\"choiceQstAnsw\":" +
                "\"D\",\"choiceQstAnalysis\":\"解析。。。。。。\",\"choiceType\":\"single\",\"optChildren\":[{\"id\":\"1\",\"optTxt\":\"Ins" +
                "tead\",\"optValue\":\"A\"},{\"id\":\"2\",\"optTxt\":\"Normally\",\"optValue\":\"B\"},{\"id\":\"3\",\"optTxt\":\"Certainly\"" +
                ",\"optValue\":\"C\"},{\"id\":\"4\",\"optTxt\":\"Then\",\"optValue\":\"D\"}]},{\"id\":\"4\",\"score\":5,\"teacherId\":\"001\",\"" +
                "choiceQstTxt\":\"4 ( 单选题 ) ( )(本题5.0分)\",\"choiceQstAnsw\":\"C\",\"choiceQstAnalysis\":\"解析。。。。。。\",\"choiceType\":\"" +
                "single\",\"optChildren\":[{\"id\":\"1\",\"optTxt\":\"general\",\"optValue\":\"A\"},{\"id\":\"2\",\"optTxt\":\"deep\",\"opt" +
                "Value\":\"B\"},{\"id\":\"3\",\"optTxt\":\"personal\",\"optValue\":\"C\"},{\"id\":\"4\",\"optTxt\":\"lively\",\"optValue\":\"D" +
                "\"}]}],\"id\":\"5bf3a6e6dc623b15e4be1dcc\",\"index\":1,\"paperInfo\":\"(阅读理解题)Directions: There are 20 blanks in the following " +
                "passage. For each blank there are four choices marked A, B and C. You should choose the ONE that best fits into the passage. Tracy Wong" +
                " is a well-known Chinese-American writer. But her writing __1__ was something she picked up by herself. After her first__2__, teaching di" +
                "sabled children, she became a part-time writer for IBM. __3__, writing stories was simply a __4__ interest. \",\"score\"" +
                ":20.0,\"teacherId\":\"001\",\"uDate\":1542771177283},{\"examChildren\":[{\"id\":\"1\",\"score\":3,\"teacherId\":\"001\",\"desig" +
                "nQuestion\":\"111最终幻想14前身是什么\",\"designAnsw\":\"最终幻想14 1.0\",\"designAnalysis\":\"最终幻想14重置过\",\"e" +
                "xamType\":\"design\"}],\"id\":\"5bf3a6d9dc623b15e4be1dca\",\"index\":3,\"paperInfo\":\"史可威尔系列问题\",\"score\":3.0,\"" +
                "teacherId\":\"001\",\"uDate\":1542771177286}],\"teacherId\":\"001\",\"uDate\":1542771177368}";


        List<BigQuestion> list = JSON.parseObject(json, ExerciseBook.class).getQuestionChildren();

        System.out.println(list.toString());

        list.forEach(bigQuestion -> {

            bigQuestion.getExamChildren().forEach(c -> {
                JSONObject jsonObject = (JSONObject) c;
                String type = jsonObject.getString("examType");
                if (type.equals("trueOrFalse")) {
                    TrueOrFalse trueOrFalse = JSON.parseObject(jsonObject.toJSONString(), TrueOrFalse.class);
                    System.out.println(trueOrFalse);
                }

            });

        });


    }

    @Test
    public void mTest() {

        ArrayList<String> a = new ArrayList<>();
        a.add("A");
//        a.add("B");
//        a.add("C");
//        a.add("D");

        ArrayList<String> b = new ArrayList<>();
        b.add("A");
        b.add("B");
//        b.add("C");
//        b.add("D");

        //交集
        List<String> results = a.stream().filter(b::contains).collect(toList());

        //差集
        List<String> reduce1 = b.stream().filter(item -> !a.contains(item)).collect(toList());

        if (results.size() == a.size() && b.size() == results.size()) {
            System.out.println("满分");
        } else if (b.size() <= a.size()) {
            //判断半对得分 如果选错 直接0分  对部分 变一分
            if (reduce1.size() >= 1 || b.size() == 0) {
                System.out.println("0 分");
            } else {
                System.out.println("1 分");
            }


        } else {
            System.out.println("0 分");
        }


    }

    @Test
    public void zipTest() {
        Flux.just("a", "b")
                .zipWith(Flux.just("c", "d"), (s1, s2) -> String.format("%s-%s", s1, s2))
                .subscribe(System.out::println);

        Mono<Tuple2<String, String>> result = Mono.just("a").zipWhen(obj -> zt("b"));

        result.map(objects -> objects.getT1().concat(objects.getT2())).subscribe(System.out::println);


    }

    public Mono<String> zt(final String a) {
        return Mono.just(a.concat("c"));
    }

}

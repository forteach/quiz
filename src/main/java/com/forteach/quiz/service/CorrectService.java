package com.forteach.quiz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.TypeReference;
import com.forteach.quiz.domain.*;
import com.forteach.quiz.exceptions.ExamQuestionsException;
import com.forteach.quiz.repository.ProblemSetBackupRepository;
import com.forteach.quiz.web.vo.ExerciseBookSheetVo;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.forteach.quiz.common.Dic.*;
import static java.util.stream.Collectors.toList;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/20  16:55
 */
@Component
public class CorrectService {

    private final ProblemSetBackupRepository problemSetBackupRepository;

    public CorrectService(ProblemSetBackupRepository problemSetBackupRepository) {
        this.problemSetBackupRepository = problemSetBackupRepository;
    }

    /**
     * @param sheetMono
     * @return
     */
    Mono<ExerciseBookSheet> exerciseBookCorrect(final Mono<ExerciseBookSheet> sheetMono) {
        return sheetMono.flatMap(seet -> problemSetBackupRepository.findById(seet.getBackupId())
                .map(b -> JSON.parseObject(b.getBackup(), new TypeReference<ExerciseBook<BigQuestion>>() {
                }))
                .map(exerciseBook -> {
                    //遍历答案 批改客观题
                    seet.setAnsw(seet.getAnsw().parallelStream().peek(answ -> answCorrect(answ, exerciseBook)).collect(Collectors.toList()));
                    return exerciseBook;
                }).flatMap(exerciseBook -> {
                    if (exerciseBook != null) {
                        return Mono.just(seet);
                    } else {
                        return Mono.empty();
                    }
                }));
    }

    Mono<ExerciseBookSheet> subjectiveCorrect(final ExerciseBookSheet sheet, final ExerciseBookSheetVo correctVo) {
        JSONObject json = JSON.parseObject(JSON.toJSONString(correctVo));
        sheet.setEvaluation(correctVo.getEvaluation());
        sheet.setAnsw(
                sheet.getAnsw().parallelStream().peek(answ ->
                        answ.setChildrenList(answ.getChildrenList().parallelStream().peek(answChildren ->
                                answChildren.setEvaluation(String.valueOf(JSONPath.eval(json, "$.answ.childrenList[questionId = '" + answChildren.getQuestionId() + "'].evaluation[0]")))
                        ).collect(toList()))
                ).collect(toList())
        );
        return Mono.just(sheet);
    }

    /**
     * @param answ
     * @return
     */
    private void answCorrect(final Answ answ, final ExerciseBook<BigQuestion> exerciseBook) {

        BigQuestion question = exerciseBook.getQuestionChildren().parallelStream()
                .filter(bigQuestion -> bigQuestion.getId().equals(answ.getBigQuestionId()))
                .findFirst()
                .get();

        answ.setChildrenList(answ.getChildrenList().parallelStream().peek(answChildren -> correcting(answChildren, question)).collect(Collectors.toList()));

        answ.setScore(answ.getChildrenList().parallelStream().filter(answChildren -> answChildren.getScore() != null).mapToDouble(AnswChildren::getScore).sum());
    }

    private void correcting(final AnswChildren answChildren, final BigQuestion question) {

        question.getExamChildren().parallelStream().forEach(obj -> {
            JSONObject jsonObject = (JSONObject) obj;
            if (jsonObject.getString(ID).equals(answChildren.getQuestionId())) {
                String type = jsonObject.getString(BIG_QUESTION_EXAM_CHILDREN_TYPE);

                switch (type) {
                    case BIG_QUESTION_EXAM_CHILDREN_TYPE_CHOICE:
                        ChoiceQst choiceQst = JSON.parseObject(jsonObject.toJSONString(), ChoiceQst.class);
                        answChildren.setScore(choice(choiceQst, answChildren));
                        break;
                    case BIG_QUESTION_EXAM_CHILDREN_TYPE_TRUEORFALSE:
                        TrueOrFalse trueOrFalse = JSON.parseObject(jsonObject.toJSONString(), TrueOrFalse.class);
                        answChildren.setScore(trueOrFalse(trueOrFalse, answChildren));
                        break;
                    case BIG_QUESTION_EXAM_CHILDREN_TYPE_DESIGN:
                        //简答主观题 人工手动批改
                        break;
                    default:
                        throw new ExamQuestionsException("非法参数 错误的题目类型");
                }
            }
        });
    }

    private Double choice(final ChoiceQst choiceQst, final AnswChildren answChildren) {
        switch (choiceQst.getChoiceType()) {
            case QUESTION_CHOICE_OPTIONS_SINGLE:
                return radio(choiceQst, answChildren);
            case QUESTION_CHOICE_MULTIPLE_SINGLE:
                return multiple(choiceQst, answChildren);
            default:
                throw new ExamQuestionsException("非法参数 错误的选择题选项类型");
        }
    }

    private Double radio(final ChoiceQst choiceQst, final AnswChildren answChildren) {

        if (answChildren.getAnswer().equals(choiceQst.getChoiceQstAnsw())) {
            answChildren.setScore(choiceQst.getScore());
            answChildren.setEvaluation(QUESTION_ACCURACY_TRUE);
        } else {
            answChildren.setScore(QUESTION_ZERO);
            answChildren.setEvaluation(QUESTION_ACCURACY_FALSE);
        }
        return answChildren.getScore();
    }

    private Double multiple(final ChoiceQst choiceQst, final AnswChildren answChildren) {

        //正确答案集
        List<String> answer = Arrays.asList(",".split(choiceQst.getChoiceQstAnsw()));
        //回答集
        List<String> exAnswer = Arrays.asList(",".split(answChildren.getAnswer()));
        //交集
        List<String> intersection = answer.stream().filter(exAnswer::contains).collect(Collectors.toList());
        //差集
        List<String> reduce1 = exAnswer.stream().filter(item -> !answer.contains(item)).collect(toList());

        //交集与正确集与答案集一致  满分
        if (answer.size() == intersection.size() && exAnswer.size() == intersection.size()) {
            answChildren.setScore(choiceQst.getScore());
            answChildren.setEvaluation(QUESTION_ACCURACY_TRUE);
        } else if (exAnswer.size() <= answer.size()) {
            if (reduce1.size() >= 1 || exAnswer.size() == 1) {
                answChildren.setScore(QUESTION_ZERO);
                answChildren.setEvaluation(QUESTION_ACCURACY_FALSE);
            } else {
                answChildren.setScore(QUESTION_ONE);
                answChildren.setEvaluation(QUESTION_ACCURACY_HALFOF);
            }
        } else {
            answChildren.setScore(QUESTION_ZERO);
            answChildren.setEvaluation(QUESTION_ACCURACY_FALSE);
        }
        return answChildren.getScore();
    }

    private Double trueOrFalse(final TrueOrFalse trueOrFalse, final AnswChildren answChildren) {
        if (trueOrFalse.getTrueOrFalseAnsw().equals(Boolean.valueOf(answChildren.getAnswer()))) {
            answChildren.setScore(trueOrFalse.getScore());
            answChildren.setEvaluation(QUESTION_ACCURACY_TRUE);
        } else {
            answChildren.setScore(QUESTION_ZERO);
            answChildren.setEvaluation(QUESTION_ACCURACY_FALSE);
        }
        return answChildren.getScore();
    }

}

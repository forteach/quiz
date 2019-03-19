package com.forteach.quiz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.TypeReference;
import com.forteach.quiz.common.DefineCode;
import com.forteach.quiz.common.MyAssert;
import com.forteach.quiz.domain.ExerciseBookSheet;
import com.forteach.quiz.exceptions.ExamQuestionsException;
import com.forteach.quiz.interaction.execute.config.BigQueKey;
import com.forteach.quiz.interaction.execute.domain.Answ;
import com.forteach.quiz.interaction.execute.domain.AnswChildren;
import com.forteach.quiz.problemsetlibrary.domain.base.ExerciseBook;
import com.forteach.quiz.questionlibrary.domain.BigQuestion;
import com.forteach.quiz.questionlibrary.domain.question.ChoiceQst;
import com.forteach.quiz.questionlibrary.domain.question.TrueOrFalse;
import com.forteach.quiz.questionlibrary.repository.BigQuestionRepository;
import com.forteach.quiz.repository.ProblemSetBackupRepository;
import com.forteach.quiz.web.vo.ExerciseBookSheetVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
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
@Slf4j
@Component
public class CorrectService {

    private final ProblemSetBackupRepository problemSetBackupRepository;

    private final BigQuestionRepository bigQuestionRepository;

    private final ReactiveStringRedisTemplate stringRedisTemplate;

    public CorrectService(ProblemSetBackupRepository problemSetBackupRepository, BigQuestionRepository bigQuestionRepository,ReactiveStringRedisTemplate stringRedisTemplate) {
        this.problemSetBackupRepository = problemSetBackupRepository;
        this.bigQuestionRepository = bigQuestionRepository;
        this.stringRedisTemplate=stringRedisTemplate;
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
                    seet.setAnsw(seet.getAnsw().stream().peek(answ -> answCorrect(answ, exerciseBook)).collect(Collectors.toList()));
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
        if (log.isDebugEnabled()){
            log.debug("练习册 答题卡 参数　sheet : {}, correctVo : {}", sheet.toString(), correctVo.toString());
        }
        JSONObject json = JSON.parseObject(JSON.toJSONString(correctVo));
        sheet.setEvaluation(correctVo.getEvaluation());
        sheet.setAnsw(
                sheet.getAnsw().stream().peek(answ ->
                        answ.setChildrenList(answ.getChildrenList().stream().peek(answChildren ->
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
        if (log.isDebugEnabled()){
            log.debug("遍历答案 批改客观题 参数 ==> answ : {}, exerciseBook : {}", answ.toString(), exerciseBook.toString());
        }
        BigQuestion question = exerciseBook.getQuestionChildren().stream()
                .filter(bigQuestion -> bigQuestion.getId().equals(answ.getBigQuestionId()))
                .findFirst()
                .get();

        answ.setChildrenList(answ.getChildrenList().stream().peek(answChildren -> correcting(answChildren, question)).collect(Collectors.toList()));

        answ.setScore(answ.getChildrenList().stream().filter(answChildren -> answChildren.getScore() != null).mapToDouble(AnswChildren::getScore).sum());
    }

    private void correcting(final AnswChildren answChildren, final BigQuestion question) {

        question.getExamChildren().stream().forEach(obj -> {
            JSONObject jsonObject = (JSONObject) obj;
            if (jsonObject.getString(ID).equals(answChildren.getQuestionId())) {
                String type = jsonObject.getString(BIG_QUESTION_EXAM_CHILDREN_TYPE);

                switch (type) {
                    case QUESTION_CHOICE_OPTIONS_SINGLE:
                    case QUESTION_CHOICE_MULTIPLE_SINGLE:
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
                        log.error("非法参数 错误的题目类型 : {}", type);
                        throw new ExamQuestionsException("非法参数 错误的题目类型");
                }
            }
        });
    }

    //TODO  题目回答更正回答记录
    public Mono<Boolean> correcting(final String questionId, final String answer) {
        //找到题目信息 TODO OLD
        //return bigQuestionRepository.findById(questionId)
        return getBigQuestion(questionId)
                .flatMap(bigQuestion -> {
                    final JSONObject json = JSON.parseObject(JSON.toJSONString(bigQuestion));
                    switch (String.valueOf(JSONPath.eval(json, "$.examChildren[0].examType"))) {
                        case QUESTION_CHOICE_OPTIONS_SINGLE:
                            //选择题
                        case QUESTION_CHOICE_MULTIPLE_SINGLE:
                            ChoiceQst choiceQst = JSON.parseObject(JSONPath.eval(json, "$.examChildren[0]").toString(), ChoiceQst.class);
                           // String result=String.valueOf(choice(choiceQst, answer));
                            return Mono.just(choice(choiceQst, answer));
                            //判断
                        case BIG_QUESTION_EXAM_CHILDREN_TYPE_TRUEORFALSE:
                            TrueOrFalse trueOrFalse = JSON.parseObject(JSONPath.eval(json, "$.examChildren[0]").toString(), TrueOrFalse.class);
                           //return Mono.just(String.valueOf(trueOrFalse(trueOrFalse, answer)));
                            return  Mono.just(trueOrFalse(trueOrFalse, answer));
                        case BIG_QUESTION_EXAM_CHILDREN_TYPE_DESIGN:
                            // TODO 简答主观题 人工手动批改
                            return Mono.just(true);
                        default:
                            log.error("非法参数 错误的题目类型 : {}", String.valueOf(JSONPath.eval(json, "$.examChildren[0].examType")));
                            throw new ExamQuestionsException("非法参数 错误的题目类型");
                    }
                });
    }

    //从Redis或Mongo获得题目内容
    public Mono<BigQuestion> getBigQuestion(String questionId){
        String key= BigQueKey.QuestionsNow(questionId);
        return stringRedisTemplate.hasKey(key)
                .flatMap(r->r.booleanValue()?stringRedisTemplate.opsForValue().get(BigQueKey.QuestionsNow(questionId)).flatMap(str->Mono.just(JSON.parseObject(str,BigQuestion.class))): bigQuestionRepository.findById(questionId));
    }

    private Double choice(final ChoiceQst choiceQst, final AnswChildren answChildren) {
        switch (choiceQst.getChoiceType()) {
            //单选
            case QUESTION_CHOICE_OPTIONS_SINGLE:
                return radio(choiceQst, answChildren);
            //多选
            case QUESTION_CHOICE_MULTIPLE_SINGLE:
                return multiple(choiceQst, answChildren);
            default:
                log.error("非法参数 错误的选择题选项类型 : {}", choiceQst.getChoiceType());
                throw new ExamQuestionsException("非法参数 错误的选择题选项类型");
        }
    }

    private boolean choice(final ChoiceQst choiceQst, final String answer) {
        switch (choiceQst.getChoiceType()) {
            //单选
            case QUESTION_CHOICE_OPTIONS_SINGLE:
                return radio(choiceQst, answer);
                //多选
            case QUESTION_CHOICE_MULTIPLE_SINGLE:
                return multiple(choiceQst, answer);
            default:
                 MyAssert.isFalse(false, DefineCode.ERR0002,"题目答案类型错误！");
                 return false;
        }
    }

    private boolean radio(final ChoiceQst choiceQst, final String answer) {
        return answer.equals(choiceQst.getChoiceQstAnsw());
    }

    private boolean multiple(final ChoiceQst choiceQst, final String solution) {
        //正确答案集
        List<String> answer = Arrays.asList(",".split(choiceQst.getChoiceQstAnsw()));
        //回答集
        List<String> exAnswer = Arrays.asList(",".split(solution));
        //交集
        List<String> intersection = answer.stream().filter(exAnswer::contains).collect(Collectors.toList());
        //差集
        List<String> reduce1 = exAnswer.stream().filter(item -> !answer.contains(item)).collect(toList());

        //交集与正确集与答案集一致  满分
        if (answer.size() == intersection.size() && exAnswer.size() == intersection.size()) {
            return true;
        } else if (exAnswer.size() <= answer.size()) {
            if (reduce1.size() >= 1 || exAnswer.size() == 1) {
                return false;
            } else {
                return false;
            }
        } else {
            return false;
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

    private boolean trueOrFalse(final TrueOrFalse trueOrFalse, final String answer) {
        if (trueOrFalse.getTrueOrFalseAnsw().equals(Boolean.valueOf(answer))) {
            return true;
        } else {
            return false;
        }
    }

}

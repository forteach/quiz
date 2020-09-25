package com.forteach.quiz.interaction.execute.service.record;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-3-14 11:27
 * @version: 1.0
 * @description:
 */
//@Slf4j
//@Service
public class InsertInteractRecordService {

//    private final ReactiveMongoTemplate mongoTemplate;

//    private final InteractRecordExecuteService interactRecordExecuteService;
//
//    private final UpdateInteractRecordService updateInteractRecordService;
//
//    private final InteractRecordBrainstormService interactRecordBrainstormService;
//
//    private final InteractRecordSurveyService interactRecordSurveyService;
//
//    private final InteractRecordTaskService interactRecordTaskService;
//
//    private final InteractRecordQuestionsService interactRecordQuestionsService;

//    public InsertInteractRecordService(ReactiveMongoTemplate mongoTemplate
//                                       InteractRecordExecuteService interactRecordExecuteService,
//                                       UpdateInteractRecordService updateInteractRecordService,
//                                       InteractRecordTaskService interactRecordTaskService,
//                                       InteractRecordSurveyService interactRecordSurveyService,
//                                       InteractRecordBrainstormService interactRecordBrainstormService,
//                                       InteractRecordQuestionsService interactRecordQuestionsService
//    ) {
//        this.mongoTemplate = mongoTemplate;
//        this.interactRecordExecuteService = interactRecordExecuteService;
//        this.updateInteractRecordService = updateInteractRecordService;
//        this.interactRecordBrainstormService = interactRecordBrainstormService;
//        this.interactRecordSurveyService = interactRecordSurveyService;
//        this.interactRecordTaskService = interactRecordTaskService;
//        this.interactRecordQuestionsService = interactRecordQuestionsService;
//    }
//
//    public Mono<Boolean> pushMongo(final InteractiveSheetVo sheetVo, final String interactRecordType){
//        final Query query = Query.query(Criteria.where(MONGDB_ID).is(sheetVo.getCircleId())
//                .and(interactRecordType + ".questionsId").is(sheetVo.getAnsw().getQuestionId())
//                .and(interactRecordType + ".answerRecordList.examineeId").ne(sheetVo.getExamineeId()))
//                .with(new Sort(Sort.Direction.DESC, "index")).limit(1);
//        Update update = new Update();
//        update.push(interactRecordType + ".$.answerRecordList", new InteractAnswerRecord(sheetVo.getExamineeId(), sheetVo.getAnsw().getAnswer()));
//        return mongoTemplate.updateMulti(query, update, InteractRecord.class).thenReturn(true);
//    }
//
//    public Mono<Boolean> releaseInteractRecord(final String circleId, final String questionId, final String selectId, final String category, final String interactRecordType) {
//
//        Mono<Long> number = interactRecordExecuteService.questionNumber(circleId);
//
//        Mono<InteractRecord> recordMono = interactRecordExecuteService.findInteractInteractRecord(circleId, questionId, category, interactRecordType);
//
//        return Mono.zip(number, recordMono).flatMap(tuple2 -> {
//
//            if (tuple2.getT2().getQuestions() != null && tuple2.getT2().getQuestions().size() > 0
//                    || tuple2.getT2().getSurveys() != null && tuple2.getT2().getSurveys().size() > 0
//                    || tuple2.getT2().getBrainstorms() != null && tuple2.getT2().getBrainstorms().size() > 0
//                    || tuple2.getT2().getInteracts() != null && tuple2.getT2().getInteracts().size() > 0) {
//                return updateInteractRecordService.upInteractInteractRecord(selectId, tuple2.getT2().getQuestions().get(0).getSelectId(), circleId, questionId, category, interactRecordType);
//            } else if (INTERACT_RECORD_SURVEYS.equals(interactRecordType)) {
//                return interactRecordSurveyService.pushInteractSurveys(selectId, circleId, questionId, tuple2.getT1(), category);
//            } else if (INTERACT_RECORD_INTERACTS.equals(interactRecordType)) {
//                return interactRecordTaskService.pushInteractTask(selectId, circleId, questionId, tuple2.getT1(), category);
//            } else if (INTERACT_RECORD_BRAINSTORMS.equals(interactRecordType)) {
//                return interactRecordBrainstormService.pushInteractBrainstorms(selectId, circleId, questionId, tuple2.getT1(), category);
//            } else {
//                return interactRecordQuestionsService.pushInteractQuestions(selectId, circleId, questionId, tuple2.getT1(), "", category);
//            }
//        }).map(Objects::nonNull);
//    }

//    /**
//     * 学生回答问题时 加入记录
//     *
//     * @param circleId
//     * @param questionId
//     * @param studentId
//     * @param answer
//     * @param right
//     * @return
//     */
//    public Mono<Boolean> answerList(final String circleId, final String questionId, final String studentId, final String answer, final String right) {
//
//        final Query query = Query.query(Criteria.where(MONGDB_ID).is(circleId)
//                .and(INTERACT_RECORD_QUESTIONS.concat(".questionsId")).is(questionId));
//        Update update = new Update()
//                .inc(INTERACT_RECORD_QUESTIONS.concat(".$.answerNumber"), 1);
//        if (QUESTION_ACCURACY_TRUE.equals(right)){
//            update.inc(INTERACT_RECORD_QUESTIONS.concat(".$.correctNumber"), 1);
//        }else if (QUESTION_ACCURACY_FALSE.equals(right)){
//            update.inc(INTERACT_RECORD_QUESTIONS.concat(".$.errorNumber"), 1);
//        }
//        update.push(INTERACT_RECORD_QUESTIONS.concat(".$.answerRecordList"), new InteractAnswerRecord(studentId, answer, right));
//        return mongoTemplate.updateMulti(query, update, InteractRecord.class)
//                .map(UpdateResult::wasAcknowledged);
//
//    }


}

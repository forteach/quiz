package com.forteach.quiz.interaction.execute.service.record;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-3-14 11:50
 * @version: 1.0
 * @description:
 */
//@Slf4j
//@Service
public class InteractRecordQuestionsService {

//    private final InteractRecordRepository repository;
//
//    private final ReactiveMongoTemplate mongoTemplate;
//
//    private final InteractRecordExecuteService interactRecordExecuteService;
//
//    public InteractRecordQuestionsService(InteractRecordRepository repository, ReactiveMongoTemplate mongoTemplate, InteractRecordExecuteService interactRecordExecuteService) {
//        this.repository = repository;
//        this.mongoTemplate = mongoTemplate;
//        this.interactRecordExecuteService = interactRecordExecuteService;
//    }

    /**
     * 查询问题记录
     * @param circleId
     * @param questionsId
     * @return
     */
//    public Mono<InteractRecordResp> findRecordQuestion(final String circleId, final String questionsId){
//        return findQuestionsRecord(circleId, questionsId)
//                .flatMap(t -> {
//                    if (t != null && t.getAnswerRecordList() != null){
//                        return interactRecordExecuteService.changeFindRecord(t.getAnswerRecordList(), t.getInteractive(), t.getCategory());
//                    }else if (t != null && t.getIndex() != null){
//                        return interactRecordExecuteService.changeRecord(t.getSelectId(), t.getInteractive(), t.getCategory());
//                    }
//                    return MyAssert.isNull(null, DefineCode.OK, "不存在相关记录");
//                });
//    }

    /**
     * 根据条件查询对应的questions 任务记录
     * @param circleId 课堂id
     * @return Flux<List < InteractQuestionsRecord>>
     */
//    private Mono<InteractQuestionsRecord> findQuestionsRecord(final String circleId, final String questionsId) {
//        return repository.findRecordByIdAndQuestionsId(circleId, questionsId)
//                .filter(Objects::nonNull)
//                .map(QuestionsDto::getQuestions)
//                .filter(list -> list != null && list.size() > 0)
//                .flatMapMany(Flux::fromIterable)
//                .filter(interactQuestionsRecord -> questionsId.equals(interactQuestionsRecord.getQuestionsId()))
//                .last()
//                .onErrorReturn(new InteractQuestionsRecord());
//    }

    /**
     * push一条新的发布问题记录
     *
     * @param selectId
     * @param circleId
     * @param questionId
     * @param number
     * @param interactive
     * @param category
     * @return
     */
//    Mono<UpdateResult> pushInteractQuestions(final String selectId, final String circleId, final String questionId, final Long number, final String interactive, final String category) {
//        Query query = Query.query(Criteria.where(MONGDB_ID).is(circleId));
//        Update update = new Update();
//        //学生编号id 进行,分割
//        InteractQuestionsRecord records = new InteractQuestionsRecord(questionId, number + 1, interactive, category, Arrays.asList(selectId.split(",")));
//        update.push(INTERACT_RECORD_QUESTIONS, records);
//        return mongoTemplate.updateMulti(query, update, InteractRecord.class);
//    }

    /**
     * 获取新的发布问题(指定问题id)
     *
     * @param circleId
     * @param questionId
     * @param category
     * @param interactive
     * @return
     */
//    private Query buildLastQuestionsRecord(final String circleId, final String questionId, final String category, final String interactive) {
//
//        final Query query = Query.query(
//                Criteria.where(MONGDB_ID).is(circleId)
//                        .and(INTERACT_RECORD_QUESTIONS + ".questionsId").is(questionId)
//                        .and(INTERACT_RECORD_QUESTIONS + ".interactive").is(interactive)
//                        .and(INTERACT_RECORD_QUESTIONS + ".category").is(category)
//        ).with(new Sort(Sort.Direction.DESC, "index")).limit(1);
//
//        query.fields().include(INTERACT_RECORD_QUESTIONS);
//
//        return query;
//    }

    /**
     * 更新发布的问题
     *
     * @param selectId
     * @param tSelectId
     * @param circleId
     * @param questionId
     * @param category
     * @param interactive
     * @return
     */
//    private Mono<UpdateResult> upInteractQuestions(final String selectId, final List<String> tSelectId, final String circleId, final String questionId, final String category, final String interactive) {
//        Query query = buildLastQuestionsRecord(circleId, questionId, category, interactive);
//        Update update = new Update();
//        List<String> list = Arrays.asList(selectId.split(","));
//        update.set(INTERACT_RECORD_QUESTIONS + ".$.selectId", list);
//        if (!list.equals(tSelectId)) {
//            update.inc(INTERACT_RECORD_QUESTIONS + ".$.number", 1);
//        }
//        return mongoTemplate.updateMulti(query, update, InteractRecord.class);
//    }

    /**
     * 获得发布的问题
     *
     * @param circleId
     * @param questionId
     * @param category
     * @param interactive
     * @return
     */
//    private Mono<InteractRecord> findInteractQuestionsRecord(final String circleId, final String questionId, final String category, final String interactive) {
//        return mongoTemplate
//                .findOne(buildLastQuestionsRecord(circleId, questionId, category, interactive), InteractRecord.class)
//                .switchIfEmpty(Mono.just(new InteractRecord()));
//    }

    /**
     * 发布问题时 加入记录
     * @param circleId
     * @param questionId
     * @param selectId
     * @param category
     * @return
     */
//    public Mono<Boolean> releaseQuestion(final String circleId, final String questionId, final String selectId, final String category, final String interactive) {
//        //获得没有回答的问题次数
//        Mono<Long> number = interactRecordExecuteService.questionNumber(circleId);
//
//        Mono<InteractRecord> recordMono = findInteractQuestionsRecord(circleId, questionId, category, interactive);
//
//        return Mono.zip(number, recordMono).flatMap(tuple2 -> {
//            //更新已发布的问题
//            if (tuple2.getT2().getQuestions() != null && tuple2.getT2().getQuestions().size() > 0) {
//                return upInteractQuestions(selectId, tuple2.getT2().getQuestions().get(0).getSelectId(), circleId, questionId, category, interactive);
//            } else {
//                //push一条新的发布问题记录
//                return pushInteractQuestions(selectId, circleId, questionId, tuple2.getT1(), interactive, category);
//            }
//
//        }).map(obj->{
//            MyAssert.isNull(obj, DefineCode.ERR0012,"提问数据记录失败");
//            return true;
//        });
//
//    }
}

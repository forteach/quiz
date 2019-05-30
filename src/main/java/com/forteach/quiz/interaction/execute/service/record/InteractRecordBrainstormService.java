package com.forteach.quiz.interaction.execute.service.record;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-3-14 11:34
 * @version: 1.0
 * @description:　头脑风暴记录
 */
//@Service
//@Slf4j
public class InteractRecordBrainstormService {
//    private final InteractRecordRepository repository;
//
//    private final ReactiveMongoTemplate mongoTemplate;
//
//    private final InteractRecordExecuteService interactRecordExecuteService;
//
//    public InteractRecordBrainstormService(InteractRecordRepository repository, ReactiveMongoTemplate mongoTemplate, InteractRecordExecuteService interactRecordExecuteService) {
//        this.repository = repository;
//        this.mongoTemplate = mongoTemplate;
//        this.interactRecordExecuteService = interactRecordExecuteService;
//    }

    /**
     * 查询头脑风暴记录
     *
     * @param circleId
     * @param questionsId
     * @return
     */
//    public Mono<InteractRecordResp> findRecordBrainstorm(final String circleId, final String questionsId) {
//        return findBrainstorm(circleId, questionsId)
//                .flatMap(t -> {
//                    if (t != null && t.getAnswerRecordList() != null) {
//                        return interactRecordExecuteService.changeFindRecord(t.getAnswerRecordList(), null, t.getCategory());
//                    } else if (t != null && t.getIndex() != null) {
//                        return interactRecordExecuteService.changeRecord(t.getSelectId(), null, t.getCategory());
//                    }
//                    return MyAssert.isNull(null, DefineCode.OK, "不存在相关记录");
//                });
//    }

    /**
     * 查询头脑风暴记录
     *
     * @param circleId
     * @param questionsId
     * @return
     */
//    private Mono<BrainstormInteractRecord> findBrainstorm(String circleId, String questionsId) {
//        return repository.findBrainstormsByIdAndQuestionsId(circleId, questionsId)
//                .filter(Objects::nonNull)
//                .map(BrainstormDto::getBrainstorms)
//                .filter(list -> list != null && list.size() > 0)
//                .flatMapMany(Flux::fromIterable)
//                .filter(brainstormInteractRecord -> questionsId.equals(brainstormInteractRecord.getQuestionsId()))
//                .last()
//                .onErrorReturn(new BrainstormInteractRecord());
//    }

    /**
     * 发布记录
     *
     * @param selectId
     * @param circleId
     * @param questionId
     * @param number
     * @param category
     * @return
     */
//    Mono<UpdateResult> pushInteractBrainstorms(final String selectId, final String circleId, final String questionId, final Long number, final String category) {
//        Query query = Query.query(Criteria.where(MONGDB_ID).is(circleId));
//        Update update = new Update();
//        //学生编号id 进行,分割
//        BrainstormInteractRecord records = new BrainstormInteractRecord(questionId, number + 1, category, Arrays.asList(selectId.split(",")));
//        update.push(INTERACT_RECORD_BRAINSTORMS, records);
//        return mongoTemplate.updateMulti(query, update, InteractRecord.class);
//    }
}

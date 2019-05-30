package com.forteach.quiz.interaction.execute.service.record;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-3-14 11:57
 * @version: 1.0
 * @description:
 */
//@Slf4j
//@Service
public class UpdateInteractRecordService {

//    private final ReactiveMongoTemplate mongoTemplate;
//
//    private final InteractRecordExecuteService interactRecordExecuteService;
//
//    public UpdateInteractRecordService(InteractRecordExecuteService interactRecordExecuteService,
//                                       ReactiveMongoTemplate mongoTemplate) {
//        this.mongoTemplate = mongoTemplate;
//        this.interactRecordExecuteService = interactRecordExecuteService;
//    }

//    Mono<UpdateResult> upInteractInteractRecord(final String selectId, final List<String> tSelectId, final String circleId, final String questionId, final String category, final String interactRecord) {
//        Query query = interactRecordExecuteService.buildLastInteractRecord(circleId, questionId, category, interactRecord);
//        Update update = new Update();
//        List<String> list = Arrays.asList(selectId.split(","));
//        update.set(interactRecord.concat(".$.selectId"), list);
//        if (!list.equals(tSelectId)) {
//            update.inc(interactRecord.concat(".$.number"), 1);
//        }
//        return mongoTemplate.updateMulti(query, update, InteractRecord.class);
//    }
}

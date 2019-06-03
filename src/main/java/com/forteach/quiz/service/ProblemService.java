package com.forteach.quiz.service;

/**
 * @Description: 练习册相关
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/15  11:01
 */
//@Slf4j
//@Service
public class ProblemService {

//    private final BigQuestionService bigQuestionService;
//
//    private final ExerciseBookRepository exerciseBookRepository;
//
//    private final ProblemSetBackupRepository problemSetBackupRepository;
//
//    private final ExerciseBookSheetRepository exerciseBookSheetRepository;
//
//    private final ReactiveMongoTemplate reactiveMongoTemplate;
//
//    private final CorrectService correctService;
//
//
//    public ProblemService(BigQuestionService bigQuestionService, ExerciseBookRepository exerciseBookRepository,
//                          ExerciseBookSheetRepository exerciseBookSheetRepository, ProblemSetBackupRepository problemSetBackupRepository,
//                          CorrectService correctService, ReactiveMongoTemplate reactiveMongoTemplate) {
//        this.bigQuestionService = bigQuestionService;
//        this.exerciseBookRepository = exerciseBookRepository;
//        this.problemSetBackupRepository = problemSetBackupRepository;
//        this.exerciseBookSheetRepository = exerciseBookSheetRepository;
//        this.correctService = correctService;
//        this.reactiveMongoTemplate = reactiveMongoTemplate;
//    }


//
//    public Mono<Void> delExerciseBook(final String id) {
//        return exerciseBookRepository.deleteById(id);
//    }
//
//    /**
//     * 根据id 获取练习册
//     *
//     * @param exerciseBookId
//     * @return
//     */
//    public Mono<ExerciseBook> getExerciseBook(final String exerciseBookId) {
//        return exerciseBookRepository.findById(exerciseBookId);
//    }

//    /**
//     * 练习册答案
//     *
//     * @param exerciseBookSheetVo
//     * @return
//     */
//    public Mono<ExerciseBookSheet> editExerciseBookSheet(final ExerciseBookSheetVo exerciseBookSheetVo) {
//        return exerciseBookSheetRepository
//                .findById(exerciseBookSheetVo.getId())
//                .defaultIfEmpty(new ExerciseBookSheet(exerciseBookSheetVo))
//                .filterWhen(this::verifyExerciseBookSheetCommit)
//                .map(obj -> {
//                    obj.setAnsw(exerciseBookSheetVo.getAnsw());
//                    return obj;
//                })
//                .flatMap(exerciseBookSheetRepository::save);
//    }

//    /**
//     * 提交练习册答案 并批改答案
//     *
//     * @param exerciseBookSheetVo
//     * @return
//     */
//    public Mono<ExerciseBookSheet> commitExerciseBookSheet(final ExerciseBookSheetVo exerciseBookSheetVo) {
//        return exerciseBookSheetRepository
//                .findById(exerciseBookSheetVo.getId())
//                .switchIfEmpty(Mono.error(new ProblemSetException("未找到练习册")))
//                .filterWhen(this::verifyExerciseBookSheetCommit)
//                .map(obj -> {
//                    obj.setAnsw(exerciseBookSheetVo.getAnsw());
//                    obj.setBackupId(exerciseBookSheetVo.getBackupId());
//                    obj.setCommit(COMMIT_EXERCISE_BOOK_SHEET_COMMIT);
//                    return obj;
//                })
//                .transform(correctService::exerciseBookCorrect)
//                .flatMap(exerciseBookSheetRepository::save);
//    }

//    /**
//     * 修改习题册
//     * @param exerciseBookQuestionVo
//     * @return
//     */
//    public Mono<ExerciseBook> changeExerciseBookQuestions(final ExerciseBookQuestionVo exerciseBookQuestionVo) {
//
//        return bigQuestionService.editExerciseBookQuestion(exerciseBookQuestionVo.getRelate(), exerciseBookQuestionVo.getBigQuestions())
//                .collectList()
//                .flatMap(questionList -> exerciseBookRepository
//                        .findById(exerciseBookQuestionVo.getExerciseBookId())
//                        .map(obj -> {
//                            obj.setQuestionChildren(questionList);
//                            return obj;
//                        })
//                        .flatMap(exerciseBookRepository::save));
//    }

//    /**
//     * 生成 题集备份册子   供:作业 ~ 试卷
//     *
//     * @return
//     */
//    public Mono<ProblemSetBackup> editProblemSetBackup(final ProblemSetBackupVo problemSetBackupVo) {
//        return getExerciseBook(problemSetBackupVo.getExerciseBookId())
//                .flatMap(exerciseBook -> problemSetBackupRepository.save(new ProblemSetBackup(problemSetBackupVo.getType(), JSON.toJSONString(exerciseBook))));
//    }
//
//    public Mono<ExerciseBookSheet> correctExerciseBookSheet(final ExerciseBookSheetVo exerciseBookSheetVo) {
//        return exerciseBookSheetRepository
//                .findById(exerciseBookSheetVo.getId())
//                .switchIfEmpty(Mono.error(new ProblemSetException("未找到练习册")))
//                .flatMap(obj -> {
//                    obj.setBackupId(exerciseBookSheetVo.getBackupId());
//                    obj.setCommit(submitState(exerciseBookSheetVo.getRewrite()));
//                    return correctService.subjectiveCorrect(obj, exerciseBookSheetVo);
//                })
//                .flatMap(exerciseBookSheetRepository::save);
//    }
//
//    private Mono<Boolean> verifyExerciseBookSheetCommit(final ExerciseBookSheet sheetMono) {
//        if (log.isDebugEnabled()){
//            log.debug("判断练习册信息是否提交 参数 ==> {}", sheetMono.toString());
//        }
//        if (COMMIT_EXERCISE_BOOK_SHEET_MODIFY.equals(sheetMono.getCommit())) {
//            return Mono.just(true);
//        } else {
//            log.error("练习册已提交或已批改 : {}", sheetMono.toString());
//            return Mono.error(new ProblemSetException("练习册已提交或已批改"));
//        }
//    }
//
//    private String submitState(int rewrite) {
//        if (rewrite == BOOK_REWRITE) {
//            return COMMIT_EXERCISE_BOOK_SHEET_MODIFY;
//        } else {
//            return COMMIT_EXERCISE_BOOK_SHEET_CORRECT;
//        }
//    }
//
//    public Mono<UpdateResult> sheetRewrite(final RewriteVo rewriteVo) {
//
//        Query query = Query.query(Criteria.where(MONGDB_ID).in(rewriteVo.getSheetId()));
//
//        Update update = new Update();
//        update.set("commit", COMMIT_EXERCISE_BOOK_SHEET_MODIFY);
//
//        return reactiveMongoTemplate.updateMulti(query, update, ExerciseBookSheet.class);
//    }

}

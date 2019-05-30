package com.forteach.quiz.interaction.execute.web.control;

/**
 * @Description: 问卷互动课堂
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/15  15:41
 */
//@RestController
//@Api(value = "问卷互动", tags = {"问卷互动等互动"})
//@RequestMapping(value = "/interactSurvey", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class SurveyInteractController {
//
//    private final SurveyInteractService surveyService;
//
//    private final TokenService tokenService;
//
//    private final InteractRecordSurveyService interactRecordSurveyService;
//
//    public SurveyInteractController(SurveyInteractService surveyService, TokenService tokenService,
//                                    InteractRecordSurveyService interactRecordSurveyService) {
//        this.surveyService = surveyService;
//        this.tokenService = tokenService;
//        this.interactRecordSurveyService = interactRecordSurveyService;
//    }
//
//    /**
//     * 发布问题
//     *
//     * @param giveVo
//     * @return
//     */
//    @ApiOperation(value = "发布问卷", notes = "通过课堂id 及提问方式 进行发布问题")
//    @PostMapping("/send")
//    @ApiImplicitParam(value = "问题id,多个id逗号分隔", name = "questionIds", required = true, dataType = "string", paramType = "from")
//    public Mono<WebResult> sendQuestion(@ApiParam(value = "发布问题", required = true) @RequestBody MoreGiveVo giveVo) {
//        return surveyService.sendQuestion(giveVo).map(WebResult::okResult);
//    }
//
//    /**
//     * 提交答案
//     *
//     * @param sheetVo
//     * @return
//     */
//    @PostMapping("/send/answer")
//    @ApiOperation(value = "提交答案", notes = "学生提交答案 只有符合规则的学生能够正确提交")
//    @ApiImplicitParams({
////            @ApiImplicitParam(value = "学生id", name = "examineeId",  dataType = "string", paramType = "from"),
//            @ApiImplicitParam(value = "课堂圈子id", name = "circleId",  dataType = "string", paramType = "from"),
//            @ApiImplicitParam(value = "切换提问类型过期标识  接收的该题cut", name = "cut", dataType = "string", paramType = "from"),
//            @ApiImplicitParam(value = "答案列表", name = "answList", dataType = "json", paramType = "from")
//    })
//    public Mono<WebResult> sendAnswer(@ApiParam(value = "提交答案", required = true) @RequestBody InteractiveSheetVo sheetVo, ServerHttpRequest request) {
//        sheetVo.setExamineeId(tokenService.getStudentId(request));
//        return surveyService.sendAnswer(sheetVo).map(WebResult::okResult);
//    }
//
//    @ApiOperation(value = "查询问卷互动记录", notes = "课堂id(必传),查询课堂答题的学生信息，问题id，查询答题各个题目学生信息")
//    @PostMapping("/findSurveysRecord")
//    @ApiImplicitParams({
//            @ApiImplicitParam(value = "课堂id", name = "circleId", dataType = "string", required = true, paramType = "query"),
//            @ApiImplicitParam(value = "问题id", name = "questionsId", dataType = "string", required = true, paramType = "query")
//    })
//    public Mono<WebResult> findSurveys(@RequestBody RecordReq recordReq){
//        //验证请求参数
//        MyAssert.blank(recordReq.getCircleId(), DefineCode.ERR0010 ,"课堂编号不能为空");
//        MyAssert.blank(recordReq.getQuestionsId(), DefineCode.ERR0010 ,"问题编号不能为空");
//        return interactRecordSurveyService.findRecordSurvey(recordReq.getCircleId(), recordReq.getQuestionsId()).map(WebResult::okResult);
//    }
}

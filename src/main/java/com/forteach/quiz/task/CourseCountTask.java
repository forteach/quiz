package com.forteach.quiz.task;

import com.forteach.quiz.questionlibrary.service.BigQuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static com.forteach.quiz.common.Dic.COUNT_COURSE_INFO;

/**
 * @Author: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2020/9/29 16:27
 * @Version: v1.0
 * @Modified：统计习题数量
 * @Description:
 */
@Slf4j
@Configuration
public class CourseCountTask {
    @Resource
    private BigQuestionService bigQuestionService;
    @Resource
    protected ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    /**
     * 保存习题数量
     */
    @Schedules({
            // TODO 注释每分钟执行任务
            @Scheduled(cron = "0 0/1 * * * ?"),
            @Scheduled(cron = "0 30 5 * * ?")
    })
    @Async
    public void updateCourseQuestion() {
        log.info("开始执行定时任务更新课程习题数量 ==> {}", LocalDateTime.now());
        if (log.isDebugEnabled()) {
            log.debug("执行线程 : {}", Thread.currentThread().getName());
        }
        bigQuestionService.findBigQuestionGroupByCourseId()
                .flatMapMany(Flux::fromIterable)
                .flatMap(b -> {
                    System.out.println(b.toString());
                    return reactiveStringRedisTemplate.opsForHash().put(COUNT_COURSE_INFO.concat(b.getCourseId()), "questionNum", b.getQuestionNum());
                }).subscribe(s -> System.out.println(s));
        log.info("{}　<== 执行更新课程习题数量定时任务结束", LocalDateTime.now());
    }
}

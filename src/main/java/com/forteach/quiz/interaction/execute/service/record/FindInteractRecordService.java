package com.forteach.quiz.interaction.execute.service.record;

import com.forteach.quiz.interaction.execute.repository.InteractRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-3-14 13:50
 * @version: 1.0
 * @description:
 */
@Slf4j
@Service
public class FindInteractRecordService {
    private final InteractRecordRepository repository;

    private FindInteractRecordService(InteractRecordRepository interactRecordRepository){
        this.repository = interactRecordRepository;
    }
}

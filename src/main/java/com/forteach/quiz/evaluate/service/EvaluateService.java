package com.forteach.quiz.evaluate.service;

import com.forteach.quiz.evaluate.domain.Evaluate;
import com.forteach.quiz.evaluate.repository.EvaluateRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/17  10:22
 */
@Service
public class EvaluateService {

    private final EvaluateRepository repository;


    public EvaluateService(EvaluateRepository repository) {
        this.repository = repository;
    }

    public Flux<Evaluate> findAll() {
        return repository.findAll();
    }


}

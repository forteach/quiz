package com.forteach.quiz.interaction.execute.service;

import lombok.Data;
import reactor.core.publisher.Mono;

import java.util.List;

@Data
public class BookT {
    private List<Mono<Boolean>> createQuestBookList;

    public BookT(List<Mono<Boolean>> createQuestBookList) {
        this.createQuestBookList = createQuestBookList;
    }
}

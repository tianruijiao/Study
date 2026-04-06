package com.example.springaiembedding.controller;

import com.example.springaiembedding.service.RagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rag")
public class RagController {

    @Autowired
    private RagService ragService;

    @GetMapping("/ask")
    public String ask(@RequestParam("question") String question) {
        return ragService.ask(question);
    }
}

package org.example.ollamaDemo.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/demo")
public class DemoController {
    @Resource(name = "ollamaChatModel")
    private ChatModel chatModel;

    @GetMapping("/chatStream")
    public Flux<String> chatStream(@RequestParam("question") String question) {
        return chatModel.stream(question);
    }
}

package org.example.firstDemo.controller;

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

    @Resource(name = "qWen")
    private ChatModel qWenChatModel;

    @Resource(name = "deepSeek")
    private ChatModel deepSeekChatModel;

    @GetMapping("/chat")
    public String chat(@RequestParam("question") String question) {
        return deepSeekChatModel.call(question);
    }

    @GetMapping("/chatStream")
    public Flux<String> chatStream(@RequestParam("question") String question) {
        return qWenChatModel.stream(question);
    }

}

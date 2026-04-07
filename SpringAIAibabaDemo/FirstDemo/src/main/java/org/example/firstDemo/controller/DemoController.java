package org.example.firstDemo.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
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

    @Resource(name = "qWenChatClient")
    private ChatClient qWenChatClient;

    @Resource(name = "deepSeekChatClient")
    private ChatClient deepSeekChatClient;

    @GetMapping("/chat")
    public String chat(@RequestParam("question") String question) {
        return deepSeekChatModel.call(question);
    }

    @GetMapping("/chatStream")
    public Flux<String> chatStream(@RequestParam("question") String question) {
        return qWenChatModel.stream(question);
    }

    @GetMapping("/client")
    public String client(@RequestParam("question") String question) {
        return deepSeekChatClient.prompt().user(question).call().content();
    }

    @GetMapping("/clientStream")
    public Flux<String> clientStream(@RequestParam("question") String question) {
        return qWenChatClient.prompt().system("你是一个美食顾问，你只能回答美食相关的问题").user(question).stream().content();
    }

}

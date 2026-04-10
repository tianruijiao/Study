package org.example.Langchain4jDemo1.controller;

import dev.langchain4j.model.chat.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private ChatModel chatModel;

    @RequestMapping("/chat")
    public String chat(@RequestParam ("question") String question) {
        return chatModel.chat(question);
    }

}

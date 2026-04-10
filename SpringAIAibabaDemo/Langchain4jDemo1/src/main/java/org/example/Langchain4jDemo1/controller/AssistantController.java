package org.example.Langchain4jDemo1.controller;

import org.example.Langchain4jDemo1.service.MyAssistant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/assistant")
public class AssistantController {

    @Autowired
    private MyAssistant assistant;

    @RequestMapping("/chat")
    public String chat(@RequestParam("question") String question) {
        return assistant.chat(question);
    }

    @RequestMapping("/chatStream")
    public Flux<String> chatStream(@RequestParam("question") String question) {
        return assistant.chatStream(question);
    }

    /**
     * 用户会话记忆对话链
     * @param question  问题
     * @param userId    用户id
     * @return 聊天结果,流式输出
     */
    @RequestMapping("/chatMemory")
    public Flux<String> chatMemory(@RequestParam("question") String question, @RequestParam("userId") Long userId) {
        return assistant.ChatMemory(userId,  question);
    }

}

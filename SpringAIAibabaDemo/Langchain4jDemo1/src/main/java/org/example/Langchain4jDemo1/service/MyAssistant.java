package org.example.Langchain4jDemo1.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import reactor.core.publisher.Flux;

@AiService
public interface MyAssistant {
    String chat(String userMessage);

    Flux<String> chatStream(String userMessage);

    // 自动绑定记忆
    Flux<String> ChatMemory(@MemoryId Long userId, @UserMessage String question);
}

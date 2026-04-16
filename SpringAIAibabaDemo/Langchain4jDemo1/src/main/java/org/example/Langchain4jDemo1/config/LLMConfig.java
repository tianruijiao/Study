package org.example.Langchain4jDemo1.config;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Configuration
public class LLMConfig {

    @Value("${langchain4j.open-ai.chat-model.api-key}")
    private String deepSeekApiKey;
    @Bean
    public StreamingChatModel deepSeekStream() {
        return OpenAiStreamingChatModel.builder()
                .apiKey(deepSeekApiKey)
                .baseUrl("https://api.deepseek.com/v1")
                .modelName("deepseek-chat")
                .build();
    }

    /**
     * 会话内存, 用于保存会话, 根据用户ID来保存不同的会话
     * @return ChatMemoryProvider
     */
    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        // 回话基于用户ID进行保存模糊匹配记忆
        // 创建一个ConcurrentHashMap, 用于保存会话, 根据用户ID来保存不同的会话
        ConcurrentHashMap<Long, ChatMemory> map = new ConcurrentHashMap<>();
        // 返回ChatMemoryProvider的实例
        return new ChatMemoryProvider() {
            @Override
            public ChatMemory get(Object memoryId) { // memoryId为用户ID
                // 获取用户ID
                Long userId = Long.valueOf(memoryId.toString());
                // map.computeIfAbsent()方法会先判断有没有这个用户ID, 如果有则返回, 如果没有则创建一个
                return map.computeIfAbsent(userId, new Function<Long, ChatMemory>() {
                    @Override
                    public ChatMemory apply(Long aLong) {
                        return MessageWindowChatMemory.withMaxMessages(100); // 创建一个消息窗口, 保存100条消息
                    }
                });
            }
        };
    }
}

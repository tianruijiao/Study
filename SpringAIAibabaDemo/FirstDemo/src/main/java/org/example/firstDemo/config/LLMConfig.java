package org.example.firstDemo.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LLMConfig {

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    @Bean
    public ChatModel qWen() {
        return DashScopeChatModel.builder().dashScopeApi(
                DashScopeApi.builder()
                        .apiKey(apiKey)
                        .build()

        ).defaultOptions(
                DashScopeChatOptions.builder()
                        .withModel("qwen3-max")
                        .build()
        ).build();
    }

    @Bean
    public ChatModel deepSeek() {
        return DashScopeChatModel.builder().dashScopeApi(
                DashScopeApi.builder()
                        .apiKey(apiKey)
                        .build()

        ).defaultOptions(
                DashScopeChatOptions.builder()
                        .withModel("deepseek-v3.2")
                        .build()
        ).build();
    }

    @Bean
    public ChatClient qWenChatClient(@Qualifier("qWen") ChatModel qWen) {
        return ChatClient.create(qWen);
    }

    @Bean
    public ChatClient deepSeekChatClient(@Qualifier("deepSeek") ChatModel deepSeek) {
        return ChatClient.create(deepSeek);
    }
}

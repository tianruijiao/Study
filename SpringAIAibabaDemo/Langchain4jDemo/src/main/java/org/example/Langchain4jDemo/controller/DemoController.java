package org.example.Langchain4jDemo.controller;

import dev.langchain4j.chain.ConversationalChain;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Resource(name = "qWen")
    private ChatModel qWen;

    @Resource(name = "deepSeek")
    private ChatModel deepSeek;

    @Resource(name = "deepSeekStream")
    private StreamingChatModel deepSeekStream;

    @Resource(name = "chatMemoryProvider")
    private ChatMemoryProvider chatMemoryProvider;

    @GetMapping("/chatQWen")
    public String chatQWen(@RequestParam("question") String question) {
        return qWen.chat( question);
    }

    @GetMapping("/chatDeepSeek")
    public String chatDeepSeek(@RequestParam("question") String question) {
        return deepSeek.chat( question);
    }

    /**
     * 聊天流式返回
     * @param question  问题
     * @return 聊天结果
     */
    @GetMapping("/chatDeepSeekStream")
    public Flux<String> chatDeepSeekStream(@RequestParam("question") String question) {
        return Flux.create(emitter -> {
            deepSeekStream.chat(question, new StreamingChatResponseHandler() {

                @Override
                public void onPartialResponse(String partial) {
                    // 发送SSE数据
                    emitter.next(partial);
                }

                @Override
                public void onCompleteResponse(ChatResponse chatResponse) {
                    emitter.complete();
                }

                @Override
                public void onError(Throwable err) {
                    emitter.error(err);
                }
            });
        });
    }

    /**
     * 会话记忆
     * @param question  问题
     * @param userId    用户id
     * @return 聊天结果
     * 不能返回流式输出那种，ConversationalChain 默认只支持同步 ChatModel，不支持流式 StreamingChatModel，
     * 如果想要实现流式输出，需要使用用 AiService + Flux + 流式模型 + 记忆
     * 在Demo1里面有演示
     */
    @GetMapping("/chatMemory")
    public String chatMemory(@RequestParam("question") String question, @RequestParam("userId") Long userId) {
        // 获取用户会话内存
        ChatMemory chatMemory = chatMemoryProvider.get(userId);
        // 绑定用户id对应的会话记忆和模型 --->对话链
        ConversationalChain conversationalChain = ConversationalChain.builder()
                .chatMemory(chatMemory)
                .chatModel(deepSeek)
                .build();
        return conversationalChain.execute(question);
    }

}

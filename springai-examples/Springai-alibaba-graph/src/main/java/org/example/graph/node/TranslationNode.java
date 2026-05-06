package org.example.graph.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

/**
 * 翻译节点
 */
public class TranslationNode implements NodeAction {

    private final ChatClient chatClient;

    public TranslationNode(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }
    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        // 先从state里获取需要翻译的句子
        String sentence = state.value("sentence", "Cats are cute little animals belonging to their loved ones");
        // 调用大模型翻译
        PromptTemplate promptTemplate = new PromptTemplate("你是一个英语翻译专家，能对句子进行翻译。" +
                "要求只返回翻译的结果，不要返回其他信息。要翻译的句子:{sentence}");
        promptTemplate.add("sentence", sentence);
        String render = promptTemplate.render();
        // 返回翻译结果
        String content = chatClient.prompt()
                .user(render)
                .call()
                .content();
        return Map.of("translation", content);
    }
}

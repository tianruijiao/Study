package org.example.graph.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

/**
 * 造句节点
 */
public class SentenceConstructionNode implements NodeAction {

    private final ChatClient chatClient;

    public SentenceConstructionNode(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        // 先从state里获取需要造句的单词
        String word = state.value("word", "cat");
        // 调用大模型造句
        PromptTemplate promptTemplate = new PromptTemplate("你是一个英语造句专家，能够基于给定的单词进行造句。" +
                "要求只返回最终造好的句子，不要返回其他信息。给定的单词是:{word}");
        promptTemplate.add("word", word);
        String render = promptTemplate.render();
        String content = chatClient.prompt()
                .user(render)
                .call()
                .content();
        // 返回造句结果
        return Map.of("sentence", content);
    }
}

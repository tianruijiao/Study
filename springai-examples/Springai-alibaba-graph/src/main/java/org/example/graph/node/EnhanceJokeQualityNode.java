package org.example.graph.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

/**
 * 提升笑话质量节点
 */
public class EnhanceJokeQualityNode implements NodeAction {

    private final ChatClient chatClient;

    public EnhanceJokeQualityNode(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }
    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        // 先从state里获取生成笑话的关键词
        String joke = state.value("joke", "");
        // 调用大模型生成
        PromptTemplate promptTemplate = new PromptTemplate("你是一个笑话优化专家，你能够优化笑话，让它更加搞笑。要求只返回优化的笑话结果。要优化的笑话：{joke}");
        promptTemplate.add("joke", joke);
        String render = promptTemplate.render();
        String content = chatClient.prompt()
                .user(render)
                .call()
                .content();
        // 返回结果
        return Map.of("newJoke", content);
    }
}

package org.example.graph.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

/**
 * 创建一个生成笑话的节点
 */
public class GenerateJokeNode implements NodeAction {

    private final ChatClient chatClient;

    public GenerateJokeNode(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        // 先从state里获取生成笑话的关键词
        String word = state.value("keyword", "爱情");
        // 调用大模型生成
        PromptTemplate promptTemplate = new PromptTemplate("你需要写一个关于指定主题的短笑话，要求返回的结果中只能包含笑话的内容。主题:{keyword}");
        promptTemplate.add("keyword", word);
        String render = promptTemplate.render();
        String content = chatClient.prompt()
                .user(render)
                .call()
                .content();
        // 返回结果
        return Map.of("joke", content);
    }
}

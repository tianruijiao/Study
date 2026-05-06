package org.example.graph.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

/**
 * 评估笑话节点
 */
public class EvaluateJokeNode implements NodeAction {

    private final ChatClient chatClient;

    public EvaluateJokeNode(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }
    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        // 先从state里获取生成的笑话
        String joke = state.value("joke", "");
        // 调用大模型评分
        PromptTemplate promptTemplate = new PromptTemplate("你是专业的笑话评分专家，仅需完成以下两步：" +
                "1. 对给定笑话的搞笑程度打0-10分（无需输出分数）；" +
                "2. 按规则输出评价：评分≥3分返回'优秀'，<3分返回'不够优秀'；" +
                "3. 仅返回评价结果，不要任何额外解释或内容。" +
                "待评分笑话：{joke}");
        promptTemplate.add("joke", joke);
        String render = promptTemplate.render();
        String content = chatClient.prompt()
                .user(render)
                .call()
                .content();
        // 处理响应结果：去除首尾空格，存入结果映射（key为"result"）
        String evaluationResult = content.trim();
        // 返回的Map会自动同步至OverAllState，供后续条件边（ConditionalEdge）判断分支
        return Map.of("result", evaluationResult);
    }
}

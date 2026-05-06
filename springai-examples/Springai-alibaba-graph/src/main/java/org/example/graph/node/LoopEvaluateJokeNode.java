package org.example.graph.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

@Slf4j
public class LoopEvaluateJokeNode implements NodeAction {

    private final ChatClient chatClient;

    public LoopEvaluateJokeNode(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        // 先从state里获取生成的笑话
        String joke = state.value("joke", "");
        Integer loopCount = state.value("loopCount", 1);
        // 调用大模型评分
        PromptTemplate promptTemplate = new PromptTemplate("你是严格的笑话评分专家，仅需完成以下要求：" +
                "1. 对给定笑话的搞笑程度打0-10分（分数为整数，0分最低，10分最高）；" +
                "2. 仅返回纯数字分数，不要任何文字解释、标点符号或额外内容；" +
                "3. 必须遵守分数范围，超出0-10分则按无效处理。" +
                "待评分笑话：{joke}");
        promptTemplate.add("joke", joke);
        String render = promptTemplate.render();
        String content = chatClient.prompt()
                .user(render)
                .call()
                .content();
        // 将响应结果的分数转换为Integer
        int evaluationResult = Integer.parseInt(content.trim());
        // 定义两个状态，loop和break，在graph中做循环判断。
        // 如果evaluationResult小于8分，则返回loop，否则返回break。
        // 设置一个最大循环次数，超过则返回break
        String result = "loop";
        if (evaluationResult >= 8 || loopCount >= 3) {
            result = "break";
        }
        log.info("loopCount:{}, evaluationResult:{}", loopCount, evaluationResult);
        loopCount++;
        // 返回的Map会自动同步至OverAllState，供后续条件边（ConditionalEdge）判断分支
        return Map.of("result", result, "loopCount", loopCount);
    }
}

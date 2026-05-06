package org.example.graph.config;

import com.alibaba.cloud.ai.graph.*;
import com.alibaba.cloud.ai.graph.action.AsyncEdgeAction;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.action.EdgeAction;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import lombok.extern.slf4j.Slf4j;
import org.example.graph.node.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Slf4j
@Configuration
public class GraphConfig {

    /**
     * springAIAlibabaGraph的demo
     * @return CompiledGraph
     */
    @Bean("quickStartGraph")
    public CompiledGraph quickStartGraph() throws GraphStateException {

        // 创建一个键策略工厂
        KeyStrategyFactory keyStrategyFactory = new KeyStrategyFactory() {
            @Override
            public Map<String, KeyStrategy> apply() {
                return Map.of("input1", KeyStrategy.REPLACE,
                        "input2", KeyStrategy.REPLACE);
            }
        };

        // 定义状态图
        StateGraph stateGraph = new StateGraph("quickStartGraph", keyStrategyFactory);

        // 添加节点node1,node2
        stateGraph.addNode("node1", AsyncNodeAction.node_async(new NodeAction() {
            @Override
            public Map<String, Object> apply(OverAllState state) throws Exception {
                log.info("node1 state :{}",  state);
                return Map.of("input1", "1", "input2", "1");
            }
        }));
        stateGraph.addNode("node2", AsyncNodeAction.node_async(new NodeAction() {
            @Override
            public Map<String, Object> apply(OverAllState state) throws Exception {
                log.info("node2 state :{}",  state);
                return Map.of("input1", "2", "input2", "2");
            }
        }));

        // 添加边
        stateGraph.addEdge(StateGraph.START, "node1");
        stateGraph.addEdge("node1", "node2");
        stateGraph.addEdge("node2", StateGraph.END);

        // 编译状态图并返回
        return stateGraph.compile();
    }

    /**
     * 使用springAIAlibabaGraph配置一个造句并翻译的状态图
     * @return CompiledGraph
     */
    @Bean("sentencesAndTranslateGraph")
    public CompiledGraph sentencesAndTranslateGraph(ChatClient.Builder chatClient) throws GraphStateException {

        // 创建一个键策略工厂
        KeyStrategyFactory keyStrategyFactory = () -> Map.of("word", KeyStrategy.REPLACE,
                "sentence", KeyStrategy.REPLACE,
                "translation", KeyStrategy.REPLACE);

        // 定义状态图
        StateGraph stateGraph = new StateGraph("sentencesAndTranslateGraph", keyStrategyFactory);

        // 添加节点sentenceConstructionNode,translationNode
        stateGraph.addNode("sentenceConstructionNode", AsyncNodeAction.node_async(new SentenceConstructionNode(chatClient)));
        stateGraph.addNode("translationNode", AsyncNodeAction.node_async(new TranslationNode(chatClient)));

        // 添加边
        stateGraph.addEdge(StateGraph.START, "sentenceConstructionNode");
        stateGraph.addEdge("sentenceConstructionNode", "translationNode");
        stateGraph.addEdge("translationNode", StateGraph.END);

        // 编译状态图并返回
        return stateGraph.compile();
    }

    /**
     * 使用springAIAlibabaGraph配置一个生成笑话状态图，对生成的笑话进行评分，如果优秀就返回，如果不够优秀就优化一次并返回
     * @return CompiledGraph
     */
    @Bean("jokeGraph")
    public CompiledGraph jokeGraph(ChatClient.Builder chatClient) throws GraphStateException {

        // 创建一个键策略工厂
        KeyStrategyFactory keyStrategyFactory = () -> Map.of("keyword", KeyStrategy.REPLACE);

        // 定义状态图
        StateGraph stateGraph = new StateGraph("jokeGraph", keyStrategyFactory);

        // 添加节点
        stateGraph.addNode("generateJokeNode", AsyncNodeAction.node_async(new GenerateJokeNode(chatClient)));
        stateGraph.addNode("evaluateJokeNode", AsyncNodeAction.node_async(new EvaluateJokeNode(chatClient)));
        stateGraph.addNode("enhanceJokeQualityNode", AsyncNodeAction.node_async(new EnhanceJokeQualityNode(chatClient)));

        // 添加边
        stateGraph.addEdge(StateGraph.START, "generateJokeNode");
        stateGraph.addEdge("generateJokeNode", "evaluateJokeNode");
        // 这里使用条件边来进行不同评分结果的分支处理
        stateGraph.addConditionalEdges("evaluateJokeNode", AsyncEdgeAction.edge_async(new EdgeAction() {
            @Override
            public String apply(OverAllState state) throws Exception {
                return state.value("result", "优秀");
            }
        }), Map.of("优秀", StateGraph.END, "不够优秀", "enhanceJokeQualityNode"));
        stateGraph.addEdge("enhanceJokeQualityNode", StateGraph.END);

        // 编译状态图并返回
        return stateGraph.compile();
    }

    /**
     * 使用springAIAlibabaGraph配置一个生成笑话状态图，对生成的笑话进行评分，如果大于等于8分就返回，如果不大于则循环重新生成新的笑话，最多循环3次
     * @return CompiledGraph
     */
    @Bean("loopJokeGraph")
    public CompiledGraph loopJokeGraph(ChatClient.Builder chatClient) throws GraphStateException {

        // 创建一个键策略工厂
        KeyStrategyFactory keyStrategyFactory = () -> Map.of("keyword", KeyStrategy.REPLACE);

        // 定义状态图
        StateGraph stateGraph = new StateGraph("loopJokeGraph", keyStrategyFactory);

        // 添加节点
        stateGraph.addNode("generateJokeNode", AsyncNodeAction.node_async(new GenerateJokeNode(chatClient)));
        stateGraph.addNode("loopEvaluateJokeNode", AsyncNodeAction.node_async(new LoopEvaluateJokeNode(chatClient)));

        // 添加边
        stateGraph.addEdge(StateGraph.START, "generateJokeNode");
        stateGraph.addEdge("generateJokeNode", "loopEvaluateJokeNode");
        // 这里使用条件边来进行不同评分结果的分支处理
        stateGraph.addConditionalEdges("loopEvaluateJokeNode", AsyncEdgeAction.edge_async(new EdgeAction() {
            @Override
            public String apply(OverAllState state) throws Exception {
                return state.value("result", "loop");
            }
        }), Map.of("loop", "generateJokeNode", "break", StateGraph.END));
        // 编译状态图并返回
        return stateGraph.compile();
    }

}

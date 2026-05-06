package org.example.graph.controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/graph")
public class GraphController {

    private final CompiledGraph quickStartGraph;
    private final CompiledGraph sentencesAndTranslateGraph;
    private final CompiledGraph jokeGraph;
    private final CompiledGraph loopJokeGraph;

    public GraphController(@Qualifier("quickStartGraph") CompiledGraph quickStartGraph,
                           @Qualifier("sentencesAndTranslateGraph") CompiledGraph sentencesAndTranslateGraph,
                           @Qualifier("jokeGraph") CompiledGraph jokeGraph,
                           @Qualifier("loopJokeGraph") CompiledGraph loopJokeGraph) {
        this.quickStartGraph = quickStartGraph;
        this.sentencesAndTranslateGraph = sentencesAndTranslateGraph;
        this.jokeGraph = jokeGraph;
        this.loopJokeGraph = loopJokeGraph;
    }

    /**
     * 测试调用
     * @return String
     */
    @GetMapping("/call")
    public String call() {
        Optional<OverAllState> call = quickStartGraph.call(Map.of());
        log.info("call result:{}", call);
        return "ok";
    }

    /**
     * 简单的例子，造句和翻译
     * @param word 单词
     * @return Map<String, Object>
     */
    @GetMapping("/sentencesAndTranslateGraph")
    public Map<String, Object> sentencesAndTranslateGraph(@RequestParam("word") String word) {
        Optional<OverAllState> call = sentencesAndTranslateGraph.call(Map.of("word", word));
        return call.map(OverAllState::data).orElse(Map.of());
    }

    /**
     * 生成笑话，评分，如果优秀就返回，如果不够优秀就优化一次并返回
     * @param keyword 关键词
     * @return Map<String, Object>
     */
    @GetMapping("/jokeGraph")
    public Map<String, Object> jokeGraph(@RequestParam("keyword") String keyword) {
        Optional<OverAllState> call = jokeGraph.call(Map.of("keyword", keyword));
        return call.map(OverAllState::data).orElse(Map.of());
    }

    /**
     * 生成笑话，评分，如果大于等于8分就返回，如果不大于则循环重新生成新的笑话，最多循环3次
     * @param keyword 关键词
     * @return Map<String, Object>
     */
    @GetMapping("/loopJokeGraph")
    public Map<String, Object> loopJokeGraph(@RequestParam("keyword") String keyword) {
        Optional<OverAllState> call = loopJokeGraph.call(Map.of("keyword", keyword));
        return call.map(OverAllState::data).orElse(Map.of());
    }
}

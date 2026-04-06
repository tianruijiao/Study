package com.example.springaiembedding.controller;

import com.example.springaiembedding.service.EmbeddingService;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/ai")
public class EmbeddingController {

    @Autowired
    private ZhiPuAiEmbeddingModel zhiPuAiEmbeddingModel;

    @Autowired
    private EmbeddingService embeddingService;

    @GetMapping("/embedding")
    public Map<String, Object> embedding(@RequestParam("question") String question) {
        float[] embed = zhiPuAiEmbeddingModel.embed(question);
        return Map.of("embed", embed);
    }

    @GetMapping("/similarity")
    public String similarity(@RequestParam("question") String question) {
        return embeddingService.queryBestMatch(question);
    }
}

package com.example.springaiembedding.service.Impl;

import com.example.springaiembedding.service.EmbeddingService;
import com.example.springaiembedding.utils.CosUtil;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    @Autowired
    private ZhiPuAiEmbeddingModel zhiPuAiEmbeddingModel;

    // 相当于一个本地的知识库文档
    private final List<String> docs = List.of("广州是世界美食之都", "西安是世界四大古都之一", "北京是新中国的首都");

    @Override
    public String queryBestMatch(String question) {
        // 先对问题进行向量化
        float[] questionEmbed = zhiPuAiEmbeddingModel.embed(question);
        // 通过EmbeddingModel模型对文档进行向量化
        List<float[]> embed = zhiPuAiEmbeddingModel.embed(docs);
        // 计算最相似的文档
        double maxSimilarity = -1.0;
        int bestIndex = -1;
        // 遍历文档的向量，通过计算找出最相似的向量和对应的文档索引
        for (int i = 0; i < embed.size(); i++) {
            double similarity = CosUtil.cosineSimilarity(questionEmbed, embed.get(i));
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                bestIndex = i;
            }
        }
        // 返回最相似的文档
        return docs.get(bestIndex);
    }
}

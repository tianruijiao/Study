package com.example.springaiembedding.service.Impl;

import com.example.springaiembedding.service.RagService;
import com.example.springaiembedding.utils.CosUtil;
import com.example.springaiembedding.utils.ResourcesUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RagServiceImpl implements RagService {

    @Autowired
    private ZhiPuAiEmbeddingModel zhiPuAiEmbeddingModel;

    @Autowired
    private ChatClient.Builder chatClient;
    @Override
    public String ask(String question) {
        // 1.获取资源文档，手动进行分割，将每个chucks 进行向量化，保存
        List<String> parts = new ArrayList<>();
        List<float[]> embeds = new ArrayList<>();
        String docs = ResourcesUtil.getResource("古代诗歌意象解析.txt");
        String[] split = docs.split("----");
        for (String part : split) {
            parts.add(part);
            embeds.add(zhiPuAiEmbeddingModel.embed(part));
        }
        // 2.对question进行向量化，通过向量计算最相似的向量，返回最相似的top2
        float[] questionEmbed = zhiPuAiEmbeddingModel.embed(question);
        double d1 = -1.0;
        double d2 = -1.0;
        int i1 = -1;
        int i2 = -1;
        for (int i = 0; i < embeds.size(); i++) {
            double similarity = CosUtil.cosineSimilarity(questionEmbed, embeds.get(i));
            if (similarity > d1) {
                d2 = d1;
                i2 = i1;
                d1 = similarity;
                i1 = i;
            } else if (similarity > d2) {
                d2 = similarity;
                i2 = i;
            }
        }
        // 3.通过最相似的向量，获取最相似的文档，形成prompt并使用ChatClient进行对话
        String part1 = parts.get(i1);
        String part2 = parts.get(i2);
        String prompt = "以下是知识库内容：\n" +
                part1 + "\n" +
                part2 + "\n" +
                "请根据知识库内容回答用户问题：\n"
                + question;
        return chatClient.build()
                .prompt()
                .system("你是知识助手，请结合上下文回答问题")
                .user(prompt)
                .call()
                .content();
    }
}

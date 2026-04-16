package org.example.rag.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.example.rag.tool.TimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/coffee")
public class CoffeeRagController {
    private final VectorStore vectorStore;
    private final ChatClient chatClient;


    public CoffeeRagController(VectorStore vectorStore, ChatClient.Builder builder, ToolCallbackProvider toolCallbackProvider) {
        this.vectorStore = vectorStore;

        // 构建向量存储文档检索器，设置相似度阈值
        VectorStoreDocumentRetriever vectorStoreDocumentRetriever =
                VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.60)// 设置相似度阈值，只有相似度高于60%的文档才会被检索
                        .vectorStore(vectorStore)
                        .build();

        // 构建检索增强顾问，用于在生成回答时检索相关文档
        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor =
                RetrievalAugmentationAdvisor.builder()
                        .documentRetriever(vectorStoreDocumentRetriever)
                        .build();

        // 构建ChatClient，配置默认顾问和工具
        this.chatClient = builder
                .defaultAdvisors(retrievalAugmentationAdvisor)
//                .defaultTools(new TimeTools())
                .defaultToolCallbacks(toolCallbackProvider)
                .build();

        log.info("CoffeeRagController初始化完成");
    }


    /**
     * 导入数据到向量数据库
     * 从classpath下的QA.csv文件读取问答数据并向量化存储
     *
     * @return 导入结果消息
     */
    @GetMapping("importData")
    public String importData() {
        try {
            // 读取Classpath下的QA.csv文件
            ClassPathResource resource = new ClassPathResource("QA.csv");
            InputStreamReader reader = new InputStreamReader(resource.getInputStream());

            // 使用Apache Commons CSV解析CSV文件
            CSVParser csvParser = CSVFormat.DEFAULT
                    .builder()
                    .setHeader() // 第一行作为标题
                    .setSkipHeaderRecord(true) // 跳过标题行
                    .build()
                    .parse(reader);

            List<Document> documents = new ArrayList<>();

            // 遍历每一行记录
            for (CSVRecord record : csvParser) {
                // 获取问题和回答字段
                String question = record.get("question");
                String answer = record.get("answer");
                String category = record.get("category");
                String keywords = record.get("keywords");

                // 将问题和回答组合成文档内容
                String content = "question: " + question + "\nquestion: " + answer+ "\ncategory: " + category+ "\nkeywords: " + keywords;

                // 创建Document对象
                Document document = new Document(content);

                // 添加到文档列表
                documents.add(document);
            }

            // 关闭解析器
            csvParser.close();

            // 将文档存入向量数据库
            vectorStore.add(documents);

            return "成功导入";
        } catch (IOException e) {
            log.error("导入数据时发生IO异常", e);
            return "导入失败: " + e.getMessage();
        }
    }

    /**
     * RAG智能问答接口
     * 基于检索增强生成技术回答用户关于咖啡的问题
     *
     * @param question 用户提出的问题
     * @return AI生成的回答
     */
    @GetMapping("/rag-ask")
    public String ragAskQuestion(@RequestParam("question") String question) {
        // 记录问题日志，便于调试和监控
        log.info("收到用户问题: {}", question);
        return chatClient
                .prompt()
                .system("你是咖啡店的专业服务员，你需要基于提供的知识库准确、友好地回答用户关于咖啡的问题。当用户询问时间相关的问题的时候请使用工具类来获取准确的时间。如果问题超出知识范围，请礼貌地表示无法回答。")
                .user(question)
                .call()
                .content();
    }

}

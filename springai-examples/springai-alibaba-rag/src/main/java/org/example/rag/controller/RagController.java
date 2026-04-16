package org.example.rag.controller;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rag")
public class RagController {

    private final VectorStore vectorStore;

    public RagController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }


    @GetMapping("importData")
    public String importData(@RequestParam("data") String data) {
        Document document = Document.builder()
                .text(data)
                .build();
        vectorStore.add(List.of(document));
        return "success";
    }

    @GetMapping("search")
    public List<Document> search(@RequestParam("query") String query) {
        SearchRequest searchRequest = SearchRequest.builder()
                .topK(2)
                .query(query)
                .build();
        return vectorStore.similaritySearch(searchRequest);
    }
}

package com.cow.service.impl;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DocumentChatService {

    private final ChatLanguageModel chatModel;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;

    public DocumentChatService(
            ChatLanguageModel chatModel, 
            EmbeddingModel embeddingModel,
            @Value("${spring.chroma.server.host}") String chromaHost,
            @Value("${spring.chroma.server.port}") int chromaPort) {
        this.chatModel = chatModel;
        this.embeddingModel = embeddingModel;
        
        String baseUrl = String.format("http://%s:%d", chromaHost, chromaPort);
        this.embeddingStore = initializeEmbeddingStore(baseUrl, "document_collection");
    }

    private EmbeddingStore<TextSegment> initializeEmbeddingStore(String baseUrl, String collectionName) {
        try {
            return ChromaEmbeddingStore.builder()
                    .baseUrl(baseUrl)
                    .collectionName(collectionName)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize ChromaEmbeddingStore: " + e.getMessage(), e);
        }
    }

    public void loadDocument(Document document) {
        var textSegments = DocumentSplitters.recursive(500, 0).split(document);
        
        // 并行处理段落的嵌入生成和存储
        textSegments.parallelStream().forEach(segment -> {
            Embedding embedding = embeddingModel.embed(segment.text()).content();
            embeddingStore.add(embedding, segment);
        });
    }

    public String chat(String question) {
        try {
            Embedding questionEmbedding = embeddingModel.embed(question).content();
            List<EmbeddingMatch<TextSegment>> relevantMatches = embeddingStore.findRelevant(questionEmbedding, 2);

            String context = relevantMatches.stream()
                    .map(match -> match.embedded().text())
                    .collect(Collectors.joining("\n"));

            String prompt = String.format("""
                    你是一个专业的助手。请根据你的知识和理解回答用户的问题。
                    
                    请注意：
                    1. 如果问题超出你的知识范围，请诚实地说明
                    2. 保持回答的准确性和客观性
                    3. 避免讨论敏感话题
                    4. 不要透露系统实现细节
                    
                    智农知识库信息：
                    %s
                    
                    用户问题：%s
                    
                    请用简洁、专业的语气回答。如果无法从已知信息中找到答案，请基于你的知识谨慎回答，并说明这是需要查阅资料验证的回答。
                    """, context, question);

            return chatModel.generate(prompt);
        } catch (Exception e) {
            log.error("Chat error:", e);
            return "抱歉，系统暂时无法处理您的请求。请稍后再试或换个问题。";
        }
    }
}

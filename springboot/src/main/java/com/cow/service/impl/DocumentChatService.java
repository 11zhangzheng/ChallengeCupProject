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

import java.util.List;

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
        this.embeddingStore = ChromaEmbeddingStore.builder()
                .baseUrl(String.format("http://%s:%d", chromaHost, chromaPort))
                .collectionName("document_collection")
                .build();
    }

    public void loadDocument(Document document) {
        var textSegments = DocumentSplitters.recursive(500, 0)
                .split(document);
        
        for (TextSegment segment : textSegments) {
            Embedding embedding = embeddingModel.embed(segment.text()).content();
            embeddingStore.add(embedding, segment);
        }
    }

    public String chat(String question) {
        Embedding questionEmbedding = embeddingModel.embed(question).content();
        
        List<EmbeddingMatch<TextSegment>> relevantMatches = embeddingStore.findRelevant(
                questionEmbedding,
                2
        );

        String context = relevantMatches.stream()
                .map(match -> match.embedded().text())
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");

        String prompt = String.format("""
                基于以下信息回答问题:
                
                %s
                
                问题: %s
                """, context, question);

        return chatModel.generate(prompt);
    }
} 
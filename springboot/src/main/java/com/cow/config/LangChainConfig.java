package com.cow.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChainConfig {

    @Value("${spring.openai.api.key}")
    private String openaiApiKey;

    @Value("${spring.openai.api.base-url:https://api.openai.com/v1}")
    private String openaiBaseUrl;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .apiKey(openaiApiKey)
                .baseUrl(openaiBaseUrl)
                .modelName("gpt-4o-mini")
                .temperature(0.7)
                .build();
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .apiKey(openaiApiKey)
                .baseUrl(openaiBaseUrl)
                .modelName("text-embedding-3-small")
                .build();
    }
} 
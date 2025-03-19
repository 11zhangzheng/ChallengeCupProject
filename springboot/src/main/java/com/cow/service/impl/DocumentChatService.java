package com.cow.service.impl;

import com.cow.model.ChatMessage;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DocumentChatService {

    private final ChatLanguageModel chatModel;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final Map<String, List<ChatMessage>> chatHistories = new ConcurrentHashMap<>();

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

    public String chat(String sessionId, String chatId, String question) {
        try {
            // 使用 chatId 获取历史记录
            String historyKey = chatId + ":" + sessionId;
            List<ChatMessage> history = chatHistories.computeIfAbsent(historyKey, k -> new ArrayList<>());

            // 获取相关文档
            Embedding questionEmbedding = embeddingModel.embed(question).content();
            List<EmbeddingMatch<TextSegment>> relevantMatches = embeddingStore.findRelevant(questionEmbedding, 2);

            String context = relevantMatches.stream()
                    .map(match -> match.embedded().text())
                    .collect(Collectors.joining("\n"));

            // 构建包含历史记录的提示词
            StringBuilder conversationContext = new StringBuilder();
            for (ChatMessage msg : history) {
                conversationContext.append(msg.getRole()).append(": ").append(msg.getContent()).append("\n");
            }

            String prompt = String.format("""
                    你是一个运行在HarvestHub农产品服务平台的专业的助手。请根据你的知识和理解回答用户的问题。
                    
                    请注意：
                    1. 如果问题超出你的知识围，请诚实地说明
                    2. 保持回答的准确性和客观性
                    3. 避免讨论敏感话题
                    4. 不要透露系统实现细节
                    
                    聊天历史：
                    %s
                    
                    种植知识库信息：
                    %s
                    
                    用户问题：%s
                    
                    请用简洁、专业的语气回答。如果无法从已知信息中找到答案，请基于你的知识谨慎回答，并说明这是需要查阅资料验证的回答。
                    """, conversationContext, context, question);

            String response = chatModel.generate(prompt);

            // 更新会话历史
            history.add(new ChatMessage("user", question, "text", System.currentTimeMillis()));
            history.add(new ChatMessage("assistant", response, "text", System.currentTimeMillis()));

            // 保持历史记录在合理范围内
            while (history.size() > 20) {
                history.remove(0);
            }

            return response;
        } catch (Exception e) {
            log.error("Chat error:", e);
            return "抱歉，系统暂时无法处理您的请求。请稍后再试或换个问题。";
        }
    }

    // 清除特定对话的历史记录
    public void clearHistory(String sessionId, String chatId) {
        String historyKey = chatId + ":" + sessionId;
        chatHistories.remove(historyKey);
    }

    // 加载对话历史到上下文
    public void loadChatHistory(String sessionId, String chatId, List<ChatMessage> messages) {
        String historyKey = chatId + ":" + sessionId;
        List<ChatMessage> history = chatHistories.computeIfAbsent(historyKey, k -> new ArrayList<>());
        history.clear(); // 清除现有历史
        // 只添加最近的20条消息作为上下文
        int startIndex = Math.max(0, messages.size() - 20);
        for (int i = startIndex; i < messages.size(); i++) {
            ChatMessage msg = messages.get(i);
            history.add(new ChatMessage(msg.getRole(), msg.getContent(), msg.getType(), msg.getTimestamp()));
        }
    }
}

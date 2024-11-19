package com.cow.service.impl;

import com.alibaba.fastjson.JSON;
import com.cow.model.ChatHistory;
import com.cow.model.ChatMessage;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatHistoryService {
    private final StringRedisTemplate redisTemplate;
    private static final String CHAT_HISTORY_KEY = "chat:history:";
    private static final String USER_CHAT_LIST_KEY = "chat:user:";
    
    public ChatHistoryService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    // 创建新的聊天记录
    public ChatHistory createChatHistory(String userId, String title) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setId(UUID.randomUUID().toString());
        chatHistory.setUserId(userId);
        chatHistory.setTitle(title);
        chatHistory.setMessages(new ArrayList<>());
        chatHistory.setCreateTime(System.currentTimeMillis());
        chatHistory.setUpdateTime(System.currentTimeMillis());
        
        // 保存聊天记录
        saveChatHistory(chatHistory);
        
        // 将聊天ID添加到用户的聊天列表
        redisTemplate.opsForList().rightPush(USER_CHAT_LIST_KEY + userId, chatHistory.getId());
        
        return chatHistory;
    }
    
    // 获取用户的所有聊天记录
    public List<ChatHistory> getUserChatHistories(String userId) {
        List<String> chatIds = redisTemplate.opsForList().range(USER_CHAT_LIST_KEY + userId, 0, -1);
        if (chatIds == null || chatIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        return chatIds.stream()
                .map(this::getChatHistory)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    // 获取单个聊天记录
    public ChatHistory getChatHistory(String chatId) {
        String json = redisTemplate.opsForValue().get(CHAT_HISTORY_KEY + chatId);
        return json != null ? JSON.parseObject(json, ChatHistory.class) : null;
    }
    
    // 保存聊天记录
    public void saveChatHistory(ChatHistory chatHistory) {
        chatHistory.setUpdateTime(System.currentTimeMillis());
        redisTemplate.opsForValue().set(
            CHAT_HISTORY_KEY + chatHistory.getId(),
            JSON.toJSONString(chatHistory),
            7, // 保存7天
            TimeUnit.DAYS
        );
    }
    
    // 删除聊天记录
    public void deleteChatHistory(String userId, String chatId) {
        redisTemplate.delete(CHAT_HISTORY_KEY + chatId);
        redisTemplate.opsForList().remove(USER_CHAT_LIST_KEY + userId, 0, chatId);
    }
    
    // 更新聊天记录标题
    public void updateChatTitle(String chatId, String newTitle) {
        ChatHistory chatHistory = getChatHistory(chatId);
        if (chatHistory != null) {
            chatHistory.setTitle(newTitle);
            saveChatHistory(chatHistory);
        }
    }
    
    // 添加新消息到聊天记录
    public void addMessage(String chatId, String role, String content, String type) {
        ChatHistory chatHistory = getChatHistory(chatId);
        if (chatHistory != null) {
            ChatMessage message = new ChatMessage(role, content, type, System.currentTimeMillis());
            chatHistory.getMessages().add(message);
            saveChatHistory(chatHistory);
        }
    }
} 
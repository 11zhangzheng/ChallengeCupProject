package com.cow.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatHistory {
    private String id;
    private String userId;
    private String title;
    private List<ChatMessage> messages;
    private Long createTime;
    private Long updateTime;
} 
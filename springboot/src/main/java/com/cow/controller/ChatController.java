package com.cow.controller;

import com.cow.service.impl.DocumentChatService;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentLoader;
import dev.langchain4j.data.document.DocumentSource;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final DocumentChatService chatService;

    public ChatController(DocumentChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename().toLowerCase();
        if (!fileName.endsWith(".txt")) {
            return ResponseEntity.badRequest().body("目前只支持txt格式文件");
        }

        // 创建自定义的 DocumentSource
        DocumentSource source = new DocumentSource() {
            private final byte[] content = file.getBytes();
            private final Metadata metadata = new Metadata();
            
            @Override
            public InputStream inputStream() throws IOException {
                return new ByteArrayInputStream(content);
            }
            
            @Override
            public Metadata metadata() {
                metadata.add("fileName", fileName);
                return metadata;
            }
        };

        try {
            Document document = DocumentLoader.load(source, new TextDocumentParser());
            chatService.loadDocument(document);
            return ResponseEntity.ok("文档上传成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("文档处理失败: " + e.getMessage());
        }
    }

    @PostMapping("/question")
    public String askQuestion(@RequestBody String question) {
        return chatService.chat(question);
    }
} 
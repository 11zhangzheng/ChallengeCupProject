package com.cow.controller;

import javax.servlet.http.HttpServletRequest;

import com.cow.service.impl.DocumentChatService;
import com.cow.service.impl.ChatHistoryService;
import com.cow.model.ChatHistory;
import dev.langchain4j.data.document.BlankDocumentException;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentLoader;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSource;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.mime.MediaType;
// import org.apache.tika.sax.BodyContentHandler;
// import org.apache.tika.metadata.Metadata;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final DocumentChatService chatService;
    private final ChatHistoryService chatHistoryService;

    public ChatController(DocumentChatService chatService, ChatHistoryService chatHistoryService) {
        this.chatService = chatService;
        this.chatHistoryService = chatHistoryService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) throws Exception {
        try {
            String fileName = file.getOriginalFilename().toLowerCase();
            log.info("Receiving file upload request: {}, size: {}", fileName, file.getSize());

            // 创建 Tika 配置和解析器
            TikaConfig tikaConfig = TikaConfig.getDefaultConfig();
            AutoDetectParser autoDetectParser = new AutoDetectParser(tikaConfig);
            org.apache.tika.metadata.Metadata tikaMetadata = new org.apache.tika.metadata.Metadata();

            // 先尝试用 Tika 直接解析文本
            String extractedText;
            try (InputStream stream = file.getInputStream()) {
                org.apache.tika.sax.BodyContentHandler handler = new org.apache.tika.sax.BodyContentHandler(-1);
                autoDetectParser.parse(stream, handler, tikaMetadata);
                extractedText = handler.toString();
                log.info("Tika extracted text length: {}", extractedText.length());
            }

            if (extractedText == null || extractedText.trim().isEmpty()) {
                log.error("Tika failed to extract text from file: {}", fileName);
                return ResponseEntity.badRequest().body("无法从文件中提取文本内容");
            }

            // 创建文档对象
            Document document = Document.from(
                    extractedText,
                    new dev.langchain4j.data.document.Metadata()
                            .add("fileName", fileName)
                            .add("Content-Type", tikaMetadata.get("Content-Type"))
            );

            try {
                chatService.loadDocument(document);
                return ResponseEntity.ok("文档上传成功");
            } catch (Exception e) {
                log.error("Failed to load document into chat service", e);
                return ResponseEntity.badRequest().body("文档处理失败: " + e.getMessage());
            }
        } catch (Exception e) {
            log.error("Document processing error: ", e);
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.trim().isEmpty()) {
                errorMessage = "未知错误，请检查文件格式是否正确";
            }
            return ResponseEntity.badRequest().body("文档处理失败: " + errorMessage);
        }
    }

    // 添加检查 DOC 文件格式的方法
    private boolean isValidDoc(byte[] content) {
        // DOC 文件的魔数
        byte[] docMagicNumber = {(byte) 0xD0, (byte) 0xCF, (byte) 0x11, (byte) 0xE0,
                (byte) 0xA1, (byte) 0xB1, (byte) 0x1A, (byte) 0xE1};

        if (content.length < docMagicNumber.length) {
            return false;
        }

        for (int i = 0; i < docMagicNumber.length; i++) {
            if (content[i] != docMagicNumber[i]) {
                return false;
            }
        }
        return true;
    }

    @PostMapping("/question")
    public String askQuestion(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        String userId = httpRequest.getHeader("satoken");
        String chatId = request.get("chatId");
        String question = request.get("question");

        // 保存用户问题
        chatHistoryService.addMessage(chatId, "user", question, "text");

        // 获取AI回答，传入 chatId
        String response = chatService.chat(userId, chatId, question);

        // 保存AI回答
        chatHistoryService.addMessage(chatId, "assistant", response, "text");

        return response;
    }

    @PostMapping("/url")
    public ResponseEntity<String> uploadUrl(@RequestBody String url) {
        try {
            // 获取网页内容
            org.jsoup.nodes.Document jsoupDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .get();

            // 提取文本内容
            String text = jsoupDoc.body().text();

            // 创建Document对象
            Document document = Document.from(text);

            // 添加元数据
            document.metadata().add("source", url);
            document.metadata().add("type", "webpage");

            // 处理文档
            chatService.loadDocument(document);

            return ResponseEntity.ok("网页内容加载成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("网页加载失败: " + e.getMessage());
        }
    }

    // 添加清除历史记录的接口
    @PostMapping("/clear-history")
    public ResponseEntity<String> clearHistory(
            HttpServletRequest request,
            @RequestBody Map<String, String> body) {
        String sessionId = request.getHeader("satoken");
        String chatId = body.get("chatId");
        chatService.clearHistory(sessionId, chatId);
        return ResponseEntity.ok("聊天历史已清除");
    }

    // 获取用户的聊天历史
    @GetMapping("/histories")
    public ResponseEntity<List<ChatHistory>> getChatHistories(HttpServletRequest request) {
        String userId = request.getHeader("satoken");
        return ResponseEntity.ok(chatHistoryService.getUserChatHistories(userId));
    }

    // 创建新的聊天
    @PostMapping("/create")
    public ResponseEntity<ChatHistory> createChat(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        String userId = httpRequest.getHeader("satoken");
        String title = request.getOrDefault("title", "新对话");

        // 创建新对话的同时清除 DocumentChatService 中的历史记录
        ChatHistory chatHistory = chatHistoryService.createChatHistory(userId, title);
        chatService.clearHistory(userId, chatHistory.getId());

        return ResponseEntity.ok(chatHistory);
    }

    // 获取特定聊天记录
    @GetMapping("/history/{chatId}")
    public ResponseEntity<ChatHistory> getChatHistory(
            @PathVariable String chatId,
            HttpServletRequest request) {
        String userId = request.getHeader("satoken");
        ChatHistory history = chatHistoryService.getChatHistory(chatId);

        if (history != null) {
            // 加载聊天历史到 DocumentChatService 的上下文中
            chatService.loadChatHistory(userId, chatId, history.getMessages());
            return ResponseEntity.ok(history);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 修改聊天标题
    @PutMapping("/history/{chatId}/title")
    public ResponseEntity<String> updateChatTitle(
            @PathVariable String chatId,
            @RequestBody Map<String, String> request) {
        String newTitle = request.get("title");
        chatHistoryService.updateChatTitle(chatId, newTitle);
        return ResponseEntity.ok("标题已更新");
    }

    // 删除聊天记录
    @DeleteMapping("/history/{chatId}")
    public ResponseEntity<String> deleteChatHistory(
            @PathVariable String chatId,
            HttpServletRequest request) {
        String userId = request.getHeader("satoken");
        chatHistoryService.deleteChatHistory(userId, chatId);
        return ResponseEntity.ok("聊天记录已删除");
    }
} 
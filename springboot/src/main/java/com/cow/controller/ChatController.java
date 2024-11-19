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
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.parser.apache.poi.ApachePoiDocumentParser;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

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
            
            if (!fileName.endsWith(".txt") && !fileName.endsWith(".pdf") && 
                !fileName.endsWith(".doc") && !fileName.endsWith(".docx") &&
                !fileName.endsWith(".ppt") && !fileName.endsWith(".pptx") &&
                !fileName.endsWith(".xls") && !fileName.endsWith(".xlsx")) {
                log.warn("Unsupported file type: {}", fileName);
                return ResponseEntity.badRequest().body("目前只支持txt, pdf, doc, docx, ppt, pptx, xls, xlsx格式文件");
            }

            DocumentParser parser;
            if (fileName.endsWith(".pdf")) {
                parser = new ApachePdfBoxDocumentParser();
            } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx") ||
                       fileName.endsWith(".ppt") || fileName.endsWith(".pptx") ||
                       fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                parser = new ApachePoiDocumentParser();
            } else {
                parser = new TextDocumentParser();
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

            Document document = DocumentLoader.load(source, parser);
            chatService.loadDocument(document);
            return ResponseEntity.ok("文档上传成功");
        } catch (BlankDocumentException e) {
            log.error("Blank document error", e);
            return ResponseEntity.badRequest().body("文档内容为空");
        } catch (Exception e) {
            log.error("Document processing error", e);
            return ResponseEntity.badRequest().body("文档处理失败: " + e.getMessage());
        }
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
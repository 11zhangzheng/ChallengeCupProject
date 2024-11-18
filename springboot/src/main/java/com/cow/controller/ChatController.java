package com.cow.controller;

import com.cow.service.impl.DocumentChatService;
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
        if (!fileName.endsWith(".txt") && !fileName.endsWith(".pdf") && 
            !fileName.endsWith(".doc") && !fileName.endsWith(".docx") &&
            !fileName.endsWith(".ppt") && !fileName.endsWith(".pptx") &&
            !fileName.endsWith(".xls") && !fileName.endsWith(".xlsx")) {
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

        try {
            Document document = DocumentLoader.load(source, parser);
            chatService.loadDocument(document);
            return ResponseEntity.ok("文档上传成功");
        } catch (BlankDocumentException e) {
            return ResponseEntity.badRequest().body("文档内容为空");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("文档处理失败: " + e.getMessage());
        }
    }

    @PostMapping("/question")
    public String askQuestion(@RequestBody String question) {
        return chatService.chat(question);
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
} 
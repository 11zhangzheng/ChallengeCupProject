package com.cow.controller;

import cn.hutool.core.io.FileUtil;
import com.cow.util.tracecode.QRGenerateor;
import com.cow.vo.Result;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/qr")
public class QRController {
    @GetMapping("/getQRCodeBytes")
    public Result<byte[]> getQRCodeBytes(@Param("productId") String productId) {
        try {
            QRGenerateor.generateQRCode(productId);
        } catch (RuntimeException e) {
            return Result.fail(e.toString());
        }
        return Result.ok(FileUtil.readBytes("D:/QR/QRcode.jpg"));
    }
}

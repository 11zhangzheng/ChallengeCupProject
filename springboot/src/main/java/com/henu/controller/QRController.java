package com.henu.controller;

import com.henu.util.tracecode.QRGenerateor;
import com.henu.vo.Result;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/qr")
public class QRController {
    @GetMapping("/getQRCodeBytes")
    public Result<byte[]> getQRCodeBytes(@Param("productId") String productId) {
        try {
            byte[] qrBytes = QRGenerateor.generateQRCode(productId);
            return Result.ok(qrBytes);
        } catch (RuntimeException e) {
            return Result.fail(e.toString());
        }
    }
}

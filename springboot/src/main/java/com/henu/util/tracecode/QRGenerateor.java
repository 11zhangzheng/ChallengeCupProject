package com.henu.util.tracecode;

import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRGenerateor {
    private static final String QR_PREFIX = "http://localhost:8080/#/" + "traceability?id=";

    public static byte[] generateQRCode(String content) {
        try {
            return QrCodeUtil.generatePng(
                    QR_PREFIX + content,
                    new QrConfig(320, 320).setErrorCorrection(ErrorCorrectionLevel.H)
            );
        } catch (RuntimeException e) {
            throw new RuntimeException("生成二维码失败: " + e.getMessage(), e);
        }
    }
}

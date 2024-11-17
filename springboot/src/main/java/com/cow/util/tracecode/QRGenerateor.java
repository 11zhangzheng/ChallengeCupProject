package com.cow.util.tracecode;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRGenerateor {
    private static final String QR_PREFIX = "https://localhost:8080/"+"traceability?id=";
    public static void generateQRCode(String content) {
        try{
            QrCodeUtil
                    .generate(QR_PREFIX + content,(new QrConfig(320,320))
                            .setErrorCorrection(ErrorCorrectionLevel.H),
                            FileUtil.file("D:/QR/QRcode.jpg"));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return ;
    }
}

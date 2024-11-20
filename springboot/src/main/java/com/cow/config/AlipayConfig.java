package com.cow.config;


import com.alipay.api.request.AlipayTradePagePayRequest;

import com.cow.util.general.PropertiesUtil;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 *
 */
@Data
@Component
public class AlipayConfig {
    /**
     * 沙箱appId
     */
    public static final String APPID = "9021000142614213";

    /**
     * 请求网关  固定
     */
    public static final String URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";

    /**
     * 编码
     */
    public static final String CHARSET = "UTF-8";

    /**
     * 返回格式
     */
    public static final String FORMAT = "json";

    /**
     * RSA2
     */
    public static final String SIGNTYPE = "RSA2";

    /**
     * 异步通知地址
     */
    public static final String NOTIFY_URL = "http://" + PropertiesUtil.getDomain() + "/alipay/notify";

    /**
     * 同步地址
     */
    public static final String RETURN_URL = "http://" + PropertiesUtil.getDomain() + "/alipay" +
            "/success";

    /**
     * 应用私钥 pkcs8格式
     */
    public static final String RSA_PRIVATE_KEY =
           "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCXTML3p1jjwAAtlUwD9R1MEBQe8v13m6CR+Mdk5VeaHf6OJ743YklFTiP8px/hoL0j6C3L63Mzx3MqQeuueLk0P8OC1QXQ33TvGO7yuLCdFEIbi9YMxBS2fuH1AJnWpGiXtGO2wd42ZaDeImNGaoTQa2xIyocQUBYT5rM7P1qVC0xMje5AtZmAsvK+NN1dEdi0SrGHqnRhWEJnqwiFdaXKoTr4C1+uceATcEZGSmkK0pWHzlMNAwjwRtT8qRd8mMsP6oWbYyxPb5UggjdXB40eGnsg5+ivxSOYPTOnSW6n/cMfdLPg8SCSOyBJya9pfOdqjBVPKQHfF5FtOi9a90EVAgMBAAECggEAVdiPEzeXnvn03+hDDkfIjLzevcS/flsGG2zdrejW8LIuGfiA3CnfSvyQPK2S0FGEn9gADCEW5302YfavQ/ppkzTOuPAncaEoVvV9tqiGRUoWTEzmca3b1sRdbscdgoUcd1856JtNeu3k/cTjMTQFEcCx2wdW5TGbpUUT0v0aZ0/2rpWcjZimdU1Qoq2j0BIuxHfWQLYj6o8vhnhb+6Ozx1KHVDJBPSiUGFEUBJyERnQHbTwHusHRxFL0Leas79Q1f10YkhowL5xJe4pcj9P6wXFGqHi81UVPlldKEzCiOlgyEZMKuc6eaIG7C7clzPM5cAZfKXiqNEJOSQ2jziclzQKBgQDVrEJ3HAeMRbB5Cqv0UL2QVz5mIzOadL7pDxNi2/26HWyzjzGBqiAytwbRh0r7HNwwxYbhV8P3IzXBj+JuN+lRsSeYHn1nPLCAA3RyCGhKHfQUYP5CEi4Rh6K0K+GewOQ6f+YsqFEVu6yZvSQVLX7CmVTmwI843iLvLwXfcL4gdwKBgQC1RXOrf5oe5X30xob48IeYDAp3E+KqQEsLTke6kDYdn5lsc6fa808IJANQuoFhx1MYGugtQAGpwTFf3U3aacq4fz7LQ4DOg2TooIcxH7igh8uGe2cAbH0ohkRs1eW4RLZTQ1vbMwaUG3LtwHcUk/u7SHhZZnxnhRDJoHCSuTg50wKBgQC58MCaNEshN1f9zVqcLlL4M+O6MDHs3GFyIMtnATgIaSi6DQcw5iJE399QIOMQKSo4Svw4Rw7ilsJl4ddSwNcKXxasmwPX/gk+LUN9JHHU5cIMpec9CeMLIDRng7RAWW2CtuBPxl1hg0MNOnoW79NaduJaOAGqpCxUZmA+9DaPuwKBgQCAiecjzS/0iLdwQvaiv6zm3GLvXWJ1Z3HPaorieDBngSzCAqEcWTvJgf/31si4FSePCEVrEOAdB6mDWOJD5pChk1lruUo1Jmb/rhUveFbrPl/L1OWNH2EAw3Tx2Bs1N0QydqlEHkKj83LjtH68UNNIUnNEsa+1mF33G5W39S8xkQKBgHe0nzvvWVMra+a6EIOJbDET+C8bOLLioUs6agl0Uv7wU144gyxusAgyFBrU3yupSZGfEAg93zP5EfJZxMOcRASZZFLTi9/tPPwyjpnBp2NdozVonJ+30IjvremWAdZdhXh+HaM8Onm4GgUAMxFt+yFQz93iWJc5tcu1XPDvJl06";

    /**
     * 沙箱支付宝公钥z'z'z
     */
    public static final String ALIPAY_PUBLIC_KEY =
           "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtTJSNl3fXi7qoH4WIMt9ztOjBSA5tBGP39c3nfn4ZW8JLVB4jIYZh2JrSgjd4iYq6el7BorEvP7YjshMxI/WL285Um/QAxBUHtcXDdygOGRgQN/gFHD10ExTmNSBJO2Dryj5Ahiol+0/jyUWbk0CKX1XazdcJSpcGU61nedWVFbxMKxMNQg2ETAwElyYYJp2/nlDSMJGyIyEkJczc9/dMlNeMYIEaIxFZY3VShq/1fOcnw00yzs8hwZhOt79nO5H1uvgUmmlBM7HnHBmeqWdS24iX7gMyqH2xZmTM5dnZufcz7XOYW3/73MCiDqm/EQNrLmq47NXyb85UiiUWKhPDwIDAQAB";
    private AlipayConfig() {
    }


    //多列模式的 aliPayRequest
    @Bean
    public AlipayTradePagePayRequest alipayTradePagePayRequest(){
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        return alipayRequest;
    }
}

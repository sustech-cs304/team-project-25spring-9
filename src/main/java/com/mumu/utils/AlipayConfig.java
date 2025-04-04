package com.mumu.utils;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AlipayConfig {
    @Value("${ali.serverUrl}")
    private String serverUrl;
    @Value("${ali.appId}")

    private String appId;
    @Value("${ali.alipayPublicKey}")
    private String alipayPublicKey;

    @Value("${ali.privateKey}")
    private String privateKey;

    @Bean
    public AlipayClient alipayClient() throws AlipayApiException {
        // 2.把需要配置的参数set进AlipayConfig类中
        com.alipay.api.AlipayConfig alipayConfig = new com.alipay.api.AlipayConfig();
        alipayConfig.setServerUrl(serverUrl);
        alipayConfig.setAppId(appId);
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        alipayConfig.setCharset("UTF8");
        alipayConfig.setSignType("RSA2");
        return new DefaultAlipayClient(alipayConfig);
    }
}

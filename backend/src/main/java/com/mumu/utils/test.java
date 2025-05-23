package com.mumu.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import java.io.File;

@RequestMapping("/test")
public class test {

    @Autowired
    private RestTemplate restTemplate;
    @GetMapping("/post")
    public void RestTemplateTestP() {
        String url = "http://localhost:8124/process_image";

        // 需要上传的文件
        File file = new File("C:/Users/Administrator/Desktop/1.png");
        FileSystemResource fileResource = new FileSystemResource(file);

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 构建请求体
        HttpEntity<FileSystemResource> entity = new HttpEntity<>(fileResource, headers);

        // 发送POST请求并接收响应
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // 输出响应内容
        System.out.println(response.getBody());
    }
}

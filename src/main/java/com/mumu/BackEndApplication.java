package com.mumu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = {"com.mumu",  "com.mumu.image"})
@MapperScan(basePackages = {"com.mumu.mapper", "com.mumu.image.mapper"})
@EnableCaching
public class BackEndApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackEndApplication.class, args);
    }
}

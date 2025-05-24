package com.mumu.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        // 允许来自特定源的跨域请求，例如 http://localhost:5173
//        config.addAllowedOrigin("*");
        config.addAllowedOrigin("http://localhost:8081");
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedOrigin("http://10.16.60.67:5173");
        config.addAllowedOrigin("http://10.32.40.94:8080");
        config.addAllowedOrigin("http://10.32.8.214:8080");
        config.addAllowedOrigin("http://8.134.53.236:8080");

        // 允许使用的HTTP方法，例如 GET、POST、PUT、DELETE 等
        config.addAllowedMethod("*");
        // 允许的请求头
        config.addAllowedHeader("*");
        // 是否允许带有身份验证信息的请求
        config.setAllowCredentials(true);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

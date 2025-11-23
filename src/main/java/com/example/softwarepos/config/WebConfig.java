package com.example.softwarepos.config; // 본인 패키지명에 맞게 수정하세요

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = "file:///workspaces/AIShop/uploads/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
}

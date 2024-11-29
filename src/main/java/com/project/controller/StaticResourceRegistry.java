package com.project.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@Configuration
public class StaticResourceRegistry implements WebMvcConfigurer {
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //registry.addResourceHandler("/repo/**") // for allowing access to all files
        registry.addResourceHandler("/repo/*.*") // for allowing access to only files not folders
                .addResourceLocations("file:F:/Webseries/","/static/")
                .setCacheControl(CacheControl.maxAge(Duration.ofSeconds(60)));
    }


}
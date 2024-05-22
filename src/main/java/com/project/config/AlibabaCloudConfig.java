package com.project.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class AlibabaCloudConfig {

    @Value("${alibaba.cloud.accessKeyId}")
    private String accessKeyId;

    @Value("${alibaba.cloud.accessKeySecret}")
    private String accessKeySecret;

    private static String staticAccessKeyId;
    private static String staticAccessKeySecret;

    @PostConstruct
    public void init() {
        staticAccessKeyId = this.accessKeyId;
        staticAccessKeySecret = this.accessKeySecret;
    }

    public static String getAccessKeyId() {
        return staticAccessKeyId;
    }

    public static String getAccessKeySecret() {
        return staticAccessKeySecret;
    }
}


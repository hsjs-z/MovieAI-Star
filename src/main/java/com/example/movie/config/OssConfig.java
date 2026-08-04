package com.example.movie.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * 作者: 枷
 * 时间:2026/8/2 下午2:48
 * 描述:创建 OSS 配置类，把 OSSClient 交给 Spring 管理
 */
@Configuration
public class OssConfig {
    @Value("${alioss.endpoint}")
    private String endpoint;

    @Value("${alioss.access-key-id}")
    private String accessKeyId;

    @Value("${alioss.access-key-secret}")
    private String accessKeySecret;

    @Bean
    public OSS ossClient() {
        // 创建OSS客户端
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }
}

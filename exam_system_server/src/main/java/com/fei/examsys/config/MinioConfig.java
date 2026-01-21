package com.fei.examsys.config;

import com.fei.examsys.config.properties.MinioProperties;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * LEARN 将Minio整合到核心容器（复用minioClient对象）
 */
@EnableConfigurationProperties(MinioProperties.class) // LEARN 懒汉式加载配置
@Configuration
@Slf4j
public class MinioConfig {
    @Autowired
    private MinioProperties minioProperties;

    @Bean
    public MinioClient getMinioClient() {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
        log.info("Minio文件服务器链接成功，MinioClient = {}", minioClient);
        return minioClient;
    }

}

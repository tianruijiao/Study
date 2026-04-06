package com.example.springaiembedding.utils;

import org.springframework.core.io.ClassPathResource;

import java.nio.charset.StandardCharsets;

/**
 * 配置资源读取工具类
 */
public class ResourcesUtil {

    /**
     * 获取资源
     * @param name 资源名称
     * @return 资源内容
     */
    public static String getResource(String name) {
        ClassPathResource resource = new ClassPathResource(name);
        try {
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

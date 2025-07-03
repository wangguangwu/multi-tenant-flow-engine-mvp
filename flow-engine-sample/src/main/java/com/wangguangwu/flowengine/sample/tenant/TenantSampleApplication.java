package com.wangguangwu.flowengine.sample.tenant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 多租户示例应用程序
 * <p>
 * 用于演示多租户框架的使用方法
 * </p>
 *
 * @author wangguangwu
 */
@SpringBootApplication
public class TenantSampleApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TenantSampleApplication.class, args);
    }
}

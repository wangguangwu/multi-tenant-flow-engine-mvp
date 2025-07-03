package com.wangguangwu.flowengine.spi.spring;

import com.wangguangwu.flowengine.spi.annotation.SPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SPI 自动配置类
 * 用于在 Spring 环境中自动配置 SPI 相关组件
 *
 * @author wangguangwu
 */
@Configuration
@ConditionalOnClass(SPI.class)
public class SPIAutoConfiguration {

    /**
     * 注册 SPI 扩展注册器
     */
    @Bean
    @ConditionalOnMissingBean
    public SPIExtensionRegistry spiExtensionRegistry() {
        return new SPIExtensionRegistry();
    }

    /**
     * 注册 SPI Bean 后处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public SPIBeanPostProcessor spiBeanPostProcessor(@Autowired SPIExtensionRegistry registry) {
        return new SPIBeanPostProcessor(registry);
    }
}

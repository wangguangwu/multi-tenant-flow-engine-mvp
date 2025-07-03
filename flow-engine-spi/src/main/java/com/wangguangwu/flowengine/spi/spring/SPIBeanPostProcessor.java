package com.wangguangwu.flowengine.spi.spring;

import com.wangguangwu.flowengine.spi.annotation.Extension;
import com.wangguangwu.flowengine.spi.annotation.SPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * SPI Bean 后处理器
 * 用于自动发现和注册 Spring 容器中的 SPI 扩展实现
 *
 * @author wangguangwu
 */
@Slf4j
public class SPIBeanPostProcessor implements BeanPostProcessor {

    /**
     * SPI 扩展注册器
     */
    private final SPIExtensionRegistry registry;

    /**
     * 构造方法
     *
     * @param registry SPI 扩展注册器
     */
    public SPIBeanPostProcessor(SPIExtensionRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 获取 bean 的真实类型（处理代理对象）
        Class<?> beanClass = AopUtils.getTargetClass(bean);
        
        // 检查 Bean 是否标记了 @Extension 注解
        Extension extension = AnnotationUtils.findAnnotation(beanClass, Extension.class);
        if (extension != null) {
            log.debug("发现 SPI 扩展实现: {}", beanClass.getName());
            
            // 查找该实现类实现的所有标记了 @SPI 的接口
            for (Class<?> interfaceClass : beanClass.getInterfaces()) {
                if (interfaceClass.isAnnotationPresent(SPI.class)) {
                    // 注册 Bean 实例为扩展点实现
                    registerExtensionBean(interfaceClass, extension.value(), bean);
                }
            }
        }
        
        return bean;
    }
    
    /**
     * 注册 Bean 为扩展点实现
     *
     * @param interfaceClass 扩展点接口类
     * @param name          扩展名
     * @param bean          Bean 实例
     */
    @SuppressWarnings("unchecked")
    private <T> void registerExtensionBean(Class<?> interfaceClass, String name, Object bean) {
        try {
            // 注册 Bean 实例为扩展点实现
            registry.registerExtensionInstance((Class<T>) interfaceClass, name, (T) bean);
        } catch (Exception e) {
            log.error("注册 Bean 为扩展点实现失败: " + bean.getClass().getName(), e);
        }
    }
}

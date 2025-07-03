package com.wangguangwu.flowengine.spi.spring;

import com.wangguangwu.flowengine.spi.DefaultExtensionLoader;
import com.wangguangwu.flowengine.spi.ExtensionLoader;
import com.wangguangwu.flowengine.spi.annotation.Extension;
import com.wangguangwu.flowengine.spi.annotation.SPI;
import com.wangguangwu.flowengine.spi.exception.SPIException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SPI 扩展注册器
 * 负责在 Spring 环境中注册和管理 SPI 扩展点
 *
 * @author wangguangwu
 */
@Slf4j
public class SPIExtensionRegistry implements ApplicationContextAware {

    /**
     * Spring 应用上下文
     */
    private ApplicationContext applicationContext;

    /**
     * 扩展点接口与扩展加载器的映射
     */
    private final Map<Class<?>, ExtensionLoader<?>> extensionLoaders = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 注册扩展点实现
     *
     * @param extensionClass 扩展点实现类
     * @param <T>           扩展点类型
     */
    @SuppressWarnings("unchecked")
    public <T> void registerExtension(Class<? extends T> extensionClass) {
        // 查找该实现类实现的所有标记了 @SPI 的接口
        for (Class<?> interfaceClass : extensionClass.getInterfaces()) {
            if (interfaceClass.isAnnotationPresent(SPI.class)) {
                registerExtension((Class<T>) interfaceClass, extensionClass);
            }
        }
    }

    /**
     * 注册扩展点实现
     *
     * @param interfaceClass 扩展点接口类
     * @param extensionClass 扩展点实现类
     * @param <T>           扩展点类型
     */
    public <T> void registerExtension(Class<T> interfaceClass, Class<? extends T> extensionClass) {
        // 获取扩展名
        Extension extension = extensionClass.getAnnotation(Extension.class);
        if (extension == null) {
            log.warn("扩展点实现类 {} 未标记 @Extension 注解，将被忽略", extensionClass.getName());
            return;
        }

        String name = extension.value();
        if (name.isEmpty()) {
            log.warn("扩展点实现类 {} 的 @Extension 注解未指定名称，将被忽略", extensionClass.getName());
            return;
        }

        try {
            // 获取扩展加载器
            ExtensionLoader<T> loader = getExtensionLoader(interfaceClass);
            
            // 注册扩展点实现类
            loader.registerExtensionClass(name, extensionClass);
            log.info("注册扩展点: {}={}", name, extensionClass.getName());
        } catch (Exception e) {
            log.error("注册扩展点失败: " + extensionClass.getName(), e);
        }
    }
    
    /**
     * 注册扩展点实现实例
     *
     * @param interfaceClass 扩展点接口类
     * @param name          扩展名
     * @param instance      扩展点实例
     * @param <T>           扩展点类型
     */
    public <T> void registerExtensionInstance(Class<T> interfaceClass, String name, T instance) {
        if (name == null || name.isEmpty()) {
            Extension extension = instance.getClass().getAnnotation(Extension.class);
            if (extension != null) {
                name = extension.value();
            }
            
            if (name == null || name.isEmpty()) {
                log.warn("扩展点实例 {} 未指定扩展名，将被忽略", instance.getClass().getName());
                return;
            }
        }
        
        try {
            // 获取扩展加载器
            ExtensionLoader<T> loader = getExtensionLoader(interfaceClass);
            
            // 注册扩展点实例
            loader.registerExtension(name, instance);
            log.info("注册扩展点实例: {}={}", name, instance.getClass().getName());
        } catch (Exception e) {
            log.error("注册扩展点实例失败: " + instance.getClass().getName(), e);
        }
    }

    /**
     * 获取扩展加载器
     *
     * @param type 扩展点接口类型
     * @param <T>  扩展点类型
     * @return 扩展加载器
     */
    @SuppressWarnings("unchecked")
    public <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        if (type == null) {
            throw new SPIException("扩展点接口类型不能为空");
        }

        ExtensionLoader<T> loader = (ExtensionLoader<T>) extensionLoaders.get(type);
        if (loader == null) {
            loader = DefaultExtensionLoader.getExtensionLoader(type);
            extensionLoaders.put(type, loader);
        }
        return loader;
    }
}

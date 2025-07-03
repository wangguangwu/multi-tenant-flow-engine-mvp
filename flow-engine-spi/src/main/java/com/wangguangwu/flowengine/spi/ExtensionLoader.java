package com.wangguangwu.flowengine.spi;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * SPI 扩展加载器接口
 * 负责加载和管理 SPI 扩展点实现
 *
 * @author wangguangwu
 */
public interface ExtensionLoader<T> {

    /**
     * 获取扩展点接口类型
     *
     * @return 扩展点接口类型
     */
    Class<T> getExtensionType();

    /**
     * 获取默认扩展点实现
     *
     * @return 默认扩展点实现
     */
    T getDefaultExtension();

    /**
     * 根据扩展名获取扩展点实现
     *
     * @param name 扩展名
     * @return 扩展点实现
     */
    T getExtension(String name);

    /**
     * 获取所有扩展点实现
     *
     * @return 所有扩展点实现的映射，key 为扩展名，value 为扩展点实现
     */
    Map<String, T> getAllExtensions();
    
    /**
     * 获取所有扩展点实现，按照优先级排序
     *
     * @return 按优先级排序的扩展点实现列表
     */
    List<T> getSortedExtensions();
    
    /**
     * 检查是否存在指定名称的扩展点实现
     *
     * @param name 扩展名
     * @return 是否存在
     */
    boolean hasExtension(String name);
    
    /**
     * 安全地获取扩展点实现，如果不存在则返回 Optional.empty()
     *
     * @param name 扩展名
     * @return 扩展点实现的 Optional 包装
     */
    Optional<T> getExtensionOptional(String name);
    
    /**
     * 注册扩展点实现类
     *
     * @param name  扩展名
     * @param clazz 扩展点实现类
     */
    void registerExtensionClass(String name, Class<? extends T> clazz);
    
    /**
     * 注册扩展点实现实例
     *
     * @param name      扩展名
     * @param extension 扩展点实现实例
     */
    void registerExtension(String name, T extension);
}
